# 26.1.2 port notes

scratch notes for the 1.21.1 -> 26.1.2 port. reference material lives in
../Moonlight/docs/primers (README + digests + VERIFIED_API_NOTES.md - that last one is the one
to trust, it's checked against the real classpath).

## where it stands

`:common`, `:fabric` and `:neoforge` all compile. nothing has been run in game yet.

client side, how the old stuff maps now:

- carved pumpkin items: `items/<id>.json` is a `minecraft:composite` of the block model plus a
  `minecraft:special` of type `hauntedharvest:pumpkin_carving` (CarvedPumpkinSpecialRenderer), which
  draws the carving texture as one quad on the north face. models/item/<pumpkin>.json just parent the
  block model now (builtin/entity is gone). every other item got its `items/` definition too.
- paper bag overlay: vanilla `Equippable.cameraOverlay` on the item (misc/paper_bag_overlay), so
  PaperBagRenderExtension and BlurOverlay are gone.
- config showcase pumpkin: its own picture in picture renderer (PumpkinShowcaseRenderer), same idea as
  moonlight's AnimatedGuiItem.
- villager masks: babies use BabyVillagerModel now, so the mask mesh (HalloweenMaskModel) is its own
  layer (hauntedharvest:villager_mask) with the adult hat uvs squashed onto the baby head. mask texture
  and the converting shake travel on VillagerRenderState through VillagerRenderStateMixin.
- snow golem custom pumpkin: an ItemStackRenderState on SnowGolemRenderState
  (SnowGolemRenderStateMixin / SnowGolemRendererMixin), submitted in place of headBlock.

mixins retargeted that would have failed to apply at runtime:

- SnowGolemMixin shear: drops go through `dropFromShearingLootTable` after `setPumpkin(false)` clears
  the custom pumpkin, so it's remembered at HEAD (@Share) and swapped in with a @WrapOperation.
- Zombie/AbstractSkeleton finalizeSpawn: `EquipmentSlot#getIndex` isnt called any more, now a
  @ModifyExpressionValue on `SpecialDates.isHalloween()` that also stops vanilla overwriting our pumpkin.
- ThrownEgg onHit: `EntityType#create` gained the EntitySpawnReason.
- Villager wantsToPickUp gained a ServerLevel param.

worth eyeballing in game: item carving face orientation, mask fit on baby heads, showcase widget
crumbs drawing over the pumpkin, the carving cursor outline.

## parked on purpose

- **blur shader**. `PumpkinTextureGenerator.drawBlur` and `CarvingManager.getCachedBlurTexture`
  are commented out / stubbed to null. the old path was a core shader driven by
  `RenderSystem.setShader` + `BufferUploader`, none of which exists. it needs a `RenderPipeline`
  registered through `ClientHelper.addRenderPipelineRegistration` and
  `DynamicTextureRenderer.drawAsInGUI` (moonlight renamed `RenderedTexturesManager` ->
  `DynamicTextureRenderer` and `FrameBufferBackedDynamicTexture` -> `RenderableDynamicTexture`).
  assets/hauntedharvest/shaders/core/blur.* still needs porting to the std140 uniform format.
  effect: wearing a carved pumpkin has no blurred overlay. the paper bag overlay is unaffected.
  the overlay itself also needs a new hook: the old ItemRenderExtension#renderHelmetOverlay is gone and
  the carving is per stack, so the static `cameraOverlay` component cant carry it. probably a mixin on
  `Gui#extractCameraOverlays`.
- **trick or treat scheduling**. 26.1 deleted `Schedule` for datapack `Timeline`s, so moonlight's
  `IVillagerBrainEvent#scheduleActivity` went with it. the activity and all its behaviours are
  still registered and still added to baby villagers, but nothing switches them into it. the 26.1
  way is a `timeline` datapack entry driving `EnvironmentAttributes.BABY_VILLAGER_ACTIVITY`,
  tagged into `minecraft:in_overworld`. since the window is config driven it probably wants to be
  generated at runtime through moonlight's `RegHelper.registerDynamicResourceProvider`.
  marked with a TODO in HalloweenVillagerAI.
- **copper golems**. 26.1 lets you build one from a carved pumpkin. ModCarvedPumpkinBlock only
  handles snow and iron. TODO is in the file.

## disabled compat (no 26.1.2 build of the mod yet)

commented out with a `//TODO: add back` header, uncomment when the mod ports:

- supplementaries (SuppCompat stubbed rather than commented - candy sweet tooth, flax in farm fields)
- farmers delight (FDCompat + both FDCompatImpl; the corn crate / cornbread / succotash items go
  with it, and FarmFieldFeature tomato placement)
- jei / rei / emi (JEICompat, REICompat, EMICompat, SpecialRecipeDisplays)
- quark (QuarkCompatImpl on neoforge)
- serene seasons / fabric seasons (SeasonModCompatImpl stubbed on both loaders)
- immediatelyfast (ImmediatelyFastCompat stubbed)
- cloth, yacl, configured, amendments, autumnity, caverns and chasms, environmental, deep aether
  (build script deps only)

modmenu is back on 18.0.0.

## behaviour changes, not just api

- **enderman gaze**: `isEnderMask` is gone, it's the `minecraft:gaze_disguise_equipment` item tag.
  added data/minecraft/tags/item/gaze_disguise_equipment.json. the PAPER_BAG_ENDERMAN config no
  longer does anything - either drop the config or make the tag conditional.
- **wearing a bag/pumpkin**: `Equipable` on the block is gone, it's `Properties#equippable` on the
  item. jack o' lanterns stay unwearable, checked per type in `regPumpkin`.
- **ModCarvedPumpkinBlockTile** lost its two backwards compat nbt paths (minecraft-namespaced
  "type", legacy "Pixels" long array). ValueInput has no getLongArray so they couldn't be kept.
  only affects worlds older than 3.x.
- **iron golems** now build from custom carved pumpkins. the old code reused vanilla's pattern,
  which only matches vanilla pumpkins, so it never fired.
- **ModFoods**: effects and fast eating moved off FoodProperties onto a paired `Consumable`.
- **carving pixel quads** now emit in the unrotated (north facing) model space and let the
  blockstate variant rotation handle facing, instead of applying the ModelState rotation by hand.
  worth eyeballing in game that the faces land the right way round.
- the model jsons under models/block still use `"loader": "hauntedharvest:carved_pumpkin"` with a
  nested `"model"`, which moonlight still dispatches on. the top level `"textures"` block and
  `"render_type"` in them are dead weight now (render layer comes from the texture's alpha).

## moonlight changes made for this port

published as 26.1.2-4.0.3 (`./gradlew :common:publishToMavenLocal` etc in ../Moonlight).

- `IVillagerBrainEvent#registerMemory` is back. it disappeared in the 26.1 port because
  `Villager.MEMORY_TYPES` is gone, but villagers still need memory slots registered for behaviours
  added through the event.
- the villager mixin moved from `registerBrainGoals` RETURN to `makeBrain` RETURN so it has the
  `Brain.Packed`. after the event fires, saved memories are re-applied - otherwise any slot a
  listener registers is created empty, since `Brain`'s constructor loads memories before
  `registerBrainGoals` runs. this is what the old "can't get brain memory saving to work" TODO in
  VillagerMixin was about.
- `addTaskToActivity` now registers the behaviour's required memories, like `Brain#addActivity`
  does. it bypasses addActivity so it was silently skipping them.

## neoforge

the `Compat*EggEntityMixin` descriptors still point at `EntityType#create(Level)`, which gained an
`EntitySpawnReason`. they're `@OptionalMixin` with `require = 0` so they just don't apply;
retarget when those mods port.
