# Issue tracker sweep (last 2 years, branch `1.21`)

Nothing was posted or closed on GitHub. This is just a status list.

## Fixed in this pass

### #82 - Autumnity + Caverns & Chasms jack o' lantern visual glitches

Three separate bugs, all confirmed from the screenshot:

1. **Missing texture on top.** Every compat model referenced `hauntedharvest:block/<x>_jack_o_lantern_top`,
   but those textures live in `textures/block/autumnity/`. Fixed the paths in
   `soul/redstone/cupric/ender_jack_o_lantern{,_frame}.json`. While there, redstone's `background`
   was still pointing at the soul one (copy-paste), now points at its own.
2. **Invisible in inventory.** `ClientRegistry.registerItemRenderers` only registered the BEWLR for
   `CARVED_PUMPKIN` and `JACK_O_LANTERN`, so the compat block items got a `builtin/entity` model with
   no renderer behind it. Now it loops over `PumpkinType.REGISTRY`.
3. **Blank face when lighting a plain carved pumpkin.** Fallout from the #81 fix: compat types have no
   plain non-tile block to become, so `getVanillaPumpkin()` returns the tile-based block and the new
   block entity starts with no carving. `HauntedHarvest.onRightClickBlock` now carves the vanilla face
   on it instead (the `classic` entry in `pumpkin_carvings` is a 1:1 copy of the vanilla face, exposed
   through `CustomCarvingsManager.getVanillaFace()`).

### #71 - Sword carving outline shows on the wrong face

`CarvedPumpkinTileRenderer` did `mulPose(RotHlpr.rot(dir))` *and* `mulPose(RotHlpr.XN90)`, but Moonlight's
`RotHlpr.rot(dir)` already bakes `XN90` in (`d.getOpposite().getRotation().mul(XN90)`). The net result was a
frame whose local +Z points straight up, i.e. the outline was drawn flat on the top face. Removed the extra
rotation; with `rot(dir)` alone local -Z faces outwards, so the outline lands on the carved face with Y up.
The quad offset was flipped to match (`-0.5` / `-0.001` on Z instead of `+0.5` / `+0.001`), and X is now
`(15 - px)` so the highlight lines up with the pixel that actually gets toggled - the same flip
`CarvedPumpkinBakedModel` does. Also fixes the (previously invisible) mirrored X.

### #74 and the duck egg half of #63 - modded eggs don't splatter

Done with one ad hoc mixin per mod, cloning the existing `CompatTurkeyEggEntityMixin`:

- `CompatQuailEggEntityMixin` → `io.github.razordevs.deep_aether.entity.projectile.ThrownQuailEgg`
- `CompatDuckEggEntityMixin` → `com.teamabnormals.environmental.common.entity.projectile.ThrownDuckEgg`

Both mods copied vanilla `ThrownEgg#onHit` verbatim, so the turkey mixin's two injection points transfer
unchanged: flag the hatch at the `EntityType#create` call, then spawn the splatter after
`broadcastEntityEvent` if nothing hatched. That reads the mod's *actual* hatch branch rather than guessing
from what appeared in the world, so "hatched → no splatter" is exact. Config gate is checked in the same
place, and `@OptionalMixin` means they're skipped when the mod isn't installed.

Environmental and Deep Aether were added as `modCompileOnly` deps next to Autumnity and Caverns & Chasms,
so the IDE resolves the targets and both `@At`s - otherwise the descriptors are unverifiable strings that
silently no-op under `require = 0`. Both mixins now validate clean.

I first tried a generic `Projectile#onHitBlock` + item-tag approach, but it can't tell *why* an entity
appeared, and eggs spawning something is the norm rather than the exception - so it's been dropped along
with the `splatterable_eggs` tag json. (#11 is the broad "any modded egg" request; still open, since this
covers only the mods that were actually reported.)

### Compat loot tables erroring on load - #58 all over again

Caught by actually running the dev client:

```
Couldn't parse element ...:hauntedharvest:blocks/soul_jack_o_lantern
  - Unknown registry key ...: hauntedharvest:soul_jack_o_lantern
```

`soul` and `cupric` had no load conditions at all, and `redstone` and `ender` were gated on **`botanypots`**,
copy-pasted in from another project. The conditions now match what `AutumnityCompatImpl` actually registers -
the whole file only class-loads when Autumnity is present, and cupric/ender sit behind their own mod check
on top of that:

| loot table                | conditions                         |
|---------------------------|------------------------------------|
| `soul_jack_o_lantern`     | `autumnity`                        |
| `redstone_jack_o_lantern` | `autumnity`                        |
| `ender_jack_o_lantern`    | `autumnity` + `endergetic`         |
| `cupric_jack_o_lantern`   | `autumnity` + `caverns_and_chasms` |

The `jack_o_lanterns` block tag already used `required: false`, so it was fine.

## Build / dev environment

- `build.gradle.kts` didn't compile: the IDE had auto-imported
  `org.gradle.internal.logging.progress.ResourceOperation` and rewritten the publishing block as
  `ResourceOperation.Type.upload { }` (that enum has a constant named `upload`). Back to a plain
  `upload { }` with no import, same as the other mods in the parent folder.
- Configured was pinned to `curse.maven:configured-457570:5873783`, which reflects on
  `ConfigTracker.closeConfig` - gone in NeoForge 21.1.248's FML. Opening the config screen crashed the
  client. Bumped to `7276577`, what Moonlight, Supplementaries and WoodGood use on 1.21.1. Note nothing in
  Haunted Harvest references Configured anymore since `775acee` moved configs to `nautilus_studio`, so it
  could just as well be dropped or demoted to `modCompileOnly`.

## Already fixed, just not closed yet

- **#81** (C&C cupric torch crash / green jack o' lantern) - fixed on `1.21`, changelog already mentions it.
- **#80** (corn crate `#c:storage_blocks`) - tag jsons added in `cd9364c`.
- **#77** (Quark 1.21.1 compat) - `QuarkCompatImpl` fixed in `cd9364c`.
- **#58** (`Couldn't parse element loot_tables:hauntedharvest:blocks/green_jack_o_lantern`) - same root cause
  as #81, the block is `cupric_jack_o_lantern` now and there is no `green_` anything left in the tree.
  1.20.1 only report.
- **#62** (Deviled Eggs advancement fires outside the nether) - reporter confirmed 1.20.1 only. The `1.20`
  branch grants it unconditionally; `1.21` checks `dimensionTypeRegistration().is(BuiltinDimensionTypes.NETHER)`.
- **#51** (snow golems lose custom pumpkin heads on relog) - Forge 1.20.1. `1.21` added
  `HauntedHarvest.onClientEntityLoad`, which re-requests the pumpkin from the server when the entity loads
  clientside; the `1.20` branch has no such hook.

## Not ours / needs more info

- **#63** (A Balanced Diet) - you already answered this: modded food doesn't count towards the vanilla
  advancement unless a mod patches it. Only the duck egg half was actionable and it's fixed.
- **#61** (game freezes, has to be killed from task manager) - no crash in the log, no reproduction, thread
  died two years ago. Needs a proper hang report or a binary search from the reporter.
- **#56** (Smarter Farmers doesn't replant corn) - corn is a multi-block crop, their farming logic has to
  opt in. Their side.
