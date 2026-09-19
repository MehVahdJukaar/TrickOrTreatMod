package net.mehvahdjukaar.hauntedharvest.reg;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.mehvahdjukaar.hauntedharvest.HauntedHarvest;
import net.mehvahdjukaar.hauntedharvest.blocks.PumpkinType;
import net.mehvahdjukaar.hauntedharvest.client.CarvedPumpkinSpecialRenderer;
import net.mehvahdjukaar.hauntedharvest.client.CarvedPumpkinTileRenderer;
import net.mehvahdjukaar.hauntedharvest.client.SeasonConfigOverlay;
import net.mehvahdjukaar.hauntedharvest.client.SplatteredEggRenderer;
import net.mehvahdjukaar.hauntedharvest.client.model.CarvedPumpkinBlockModel;
import net.mehvahdjukaar.hauntedharvest.client.model.HalloweenMaskModel;
import net.mehvahdjukaar.hauntedharvest.client.screens.CarvingTooltipComponent;
import net.mehvahdjukaar.hauntedharvest.client.screens.PumpkinShowcaseWidget;
import net.mehvahdjukaar.hauntedharvest.items.components.PumpkinCarvingData;
import net.mehvahdjukaar.moonlight.api.misc.EventCalled;
import net.mehvahdjukaar.moonlight.api.platform.ClientHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.particle.HeartParticle;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.sprite.SpriteId;
import net.minecraft.data.AtlasIds;
import net.minecraft.resources.Identifier;

import java.util.Map;

public class ClientRegistry {

    public static final ModelLayerLocation VILLAGER_MASK = loc("villager_mask");

    public static final SpriteId PUMPKIN_HIGHLIGHT = blockSprite(HauntedHarvest.res("block/pumpkin_highlight"));
    public static final SpriteId PUMPKIN = blockSprite(Identifier.parse("block/pumpkin_side"));
    public static final SpriteId CARVING_OUTLINE = blockSprite(HauntedHarvest.res("block/carving_grid"));

    public static final Identifier OUTLINE_SPRITE = HauntedHarvest.res("outline");

    private static final Map<PumpkinType, SpriteId[]> PUMPKIN_SPRITES = new Object2ObjectOpenHashMap<>();

    public static SpriteId blockSprite(Identifier texture) {
        return new SpriteId(AtlasIds.BLOCKS, texture);
    }

    public static TextureAtlasSprite sprite(SpriteId id) {
        return Minecraft.getInstance().getAtlasManager().get(id);
    }

    public static SpriteId getSprite(PumpkinType type, int ordinal) {
        var m = PUMPKIN_SPRITES.get(type);
        if (m != null) return m[ordinal];
        else {
            throw new NullPointerException();
        }
    }

    private static ModelLayerLocation loc(String name) {
        return new ModelLayerLocation(HauntedHarvest.res(name), name);
    }

    public static void init() {
        ClientHelper.addEntityRenderersRegistration(ClientRegistry::registerEntityRenderers);
        ClientHelper.addParticleRegistration(ClientRegistry::registerParticles);
        ClientHelper.addBlockModelRegistration(ClientRegistry::registerBlockModels);
        ClientHelper.addSpecialModelRegistration(ClientRegistry::registerSpecialModels);
        ClientHelper.addBlockEntityRenderersRegistration(ClientRegistry::registerBlockEntityRenderers);
        ClientHelper.addTooltipComponentRegistration(ClientRegistry::registerTooltipComponent);
        ClientHelper.addModelLayerRegistration(ClientRegistry::registerModelLayers);
        ClientHelper.addClientSetup(ClientRegistry::setup);
        SeasonConfigOverlay.register();
        PumpkinShowcaseWidget.register();
    }


    public static void setup() {
        //render layers come from the textures themselves now, no registerRenderType
        for (var t : PumpkinType.REGISTRY) {
            SpriteId shade = blockSprite(HauntedHarvest.res("block/" + t.getTextureKey() + "_shade"));
            SpriteId background = blockSprite(HauntedHarvest.res("block/" + t.getTextureKey() + "_background"));
            PUMPKIN_SPRITES.put(t, new SpriteId[]{PUMPKIN, shade, background, PUMPKIN_HIGHLIGHT});
        }
    }

    @EventCalled
    private static void registerModelLayers(ClientHelper.ModelLayerEvent event) {
        event.register(VILLAGER_MASK, HalloweenMaskModel::createMesh);
    }

    @EventCalled
    private static void registerParticles(ClientHelper.ParticleEvent event) {
        event.register(ModRegistry.SPOOKED_PARTICLE.get(), HeartParticle.AngryVillagerProvider::new);
    }

    @EventCalled
    private static void registerEntityRenderers(ClientHelper.EntityRendererEvent event) {
        event.register(ModRegistry.SPLATTERED_EGG_ENTITY.get(), SplatteredEggRenderer::new);
    }

    @EventCalled
    private static void registerTooltipComponent(ClientHelper.TooltipComponentEvent event) {
        event.register(PumpkinCarvingData.class, CarvingTooltipComponent::new);
    }

    @EventCalled
    private static void registerBlockEntityRenderers(ClientHelper.BlockEntityRendererEvent event) {
        event.register(ModRegistry.MOD_CARVED_PUMPKIN_TILE.get(), CarvedPumpkinTileRenderer::new);
    }

    @EventCalled
    private static void registerBlockModels(ClientHelper.BlockModelEvent event) {
        event.register(HauntedHarvest.res("carved_pumpkin"), CarvedPumpkinBlockModel.Unbaked.CODEC);
    }

    @EventCalled
    private static void registerSpecialModels(ClientHelper.SpecialModelEvent event) {
        event.register(HauntedHarvest.res("pumpkin_carving"), CarvedPumpkinSpecialRenderer.Unbaked.CODEC);
    }
}
