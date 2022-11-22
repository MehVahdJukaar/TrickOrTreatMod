package net.mehvahdjukaar.hauntedharvest.reg;

import net.mehvahdjukaar.hauntedharvest.HauntedHarvest;
import net.mehvahdjukaar.hauntedharvest.client.CarvedPumpkinBlockLoader;
import net.mehvahdjukaar.hauntedharvest.client.CarvedPumpkinTileRenderer;
import net.mehvahdjukaar.hauntedharvest.client.CarvingManager;
import net.mehvahdjukaar.hauntedharvest.client.SplatteredEggRenderer;
import net.mehvahdjukaar.hauntedharvest.client.gui.CarvingTooltipComponent;
import net.mehvahdjukaar.moonlight.api.misc.EventCalled;
import net.mehvahdjukaar.moonlight.api.platform.ClientPlatformHelper;
import net.minecraft.client.particle.HeartParticle;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.resources.model.Material;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Blocks;

import static net.minecraft.client.renderer.texture.TextureAtlas.LOCATION_BLOCKS;

public class ClientRegistry {


    public static final ResourceLocation JACK_O_LANTERN_FRAME = HauntedHarvest.res("block/jack_o_lantern_frame");
    public static final ResourceLocation PUMPKIN_FRAME = HauntedHarvest.res("block/pumpkin_frame");

    public static final Material CARVED_PUMPKIN_BACKGROUND = new Material(LOCATION_BLOCKS, HauntedHarvest.res("block/carved_pumpkin_background"));
    public static final Material CARVED_PUMPKIN_SHADE = new Material(LOCATION_BLOCKS, HauntedHarvest.res("block/carved_pumpkin_shade"));

    public static final Material JACK_O_LANTERN_BACKGROUND = new Material(LOCATION_BLOCKS, HauntedHarvest.res("block/jack_o_lantern_background"));
    public static final Material JACK_O_LANTERN_SHADE_1 = new Material(LOCATION_BLOCKS, HauntedHarvest.res("block/jack_o_lantern_shade_1"));
    public static final Material JACK_O_LANTERN_SHADE_2 = new Material(LOCATION_BLOCKS, HauntedHarvest.res("block/jack_o_lantern_shade_2"));

    public static final Material PUMPKIN_HIGHLIGHT = new Material(LOCATION_BLOCKS, HauntedHarvest.res("block/pumpkin_highlight"));
    public static final Material PUMPKIN = new Material(LOCATION_BLOCKS, new ResourceLocation("block/pumpkin_side"));

    public static final Material CARVING_OUTLINE = new Material(LOCATION_BLOCKS, HauntedHarvest.res("block/carving_grid"));


    public static void init() {
        ClientPlatformHelper.addEntityRenderersRegistration(ClientRegistry::registerEntityRenderers);
        ClientPlatformHelper.addParticleRegistration(ClientRegistry::registerParticles);
        ClientPlatformHelper.addAtlasTextureCallback(TextureAtlas.LOCATION_BLOCKS, ClientRegistry::registerTextures);
        ClientPlatformHelper.addModelLoaderRegistration(ClientRegistry::registerModelLoaders);
        ClientPlatformHelper.addBlockEntityRenderersRegistration(ClientRegistry::registerBlockEntityRenderers);
        ClientPlatformHelper.addTooltipComponentRegistration(ClientRegistry::registerTooltipComponent);
        ClientPlatformHelper.addSpecialModelRegistration(ClientRegistry::registerSpecialModels);
    }

    public static void setup() {
        ClientPlatformHelper.registerRenderType(ModRegistry.CORN_BASE.get(), RenderType.cutout());
        ClientPlatformHelper.registerRenderType(ModRegistry.CORN_MIDDLE.get(), RenderType.cutout());
        ClientPlatformHelper.registerRenderType(ModRegistry.CORN_TOP.get(), RenderType.cutout());
        ClientPlatformHelper.registerRenderType(ModRegistry.MOD_JACK_O_LANTERN.get(), RenderType.cutout());
        ClientPlatformHelper.registerRenderType(ModRegistry.MOD_CARVED_PUMPKIN.get(), RenderType.cutout());
        ClientPlatformHelper.registerRenderType(Blocks.JACK_O_LANTERN, RenderType.cutout());
    }

    @EventCalled
    private static void registerParticles(ClientPlatformHelper.ParticleEvent event) {
        event.register(ModRegistry.SPOOKED_PARTICLE.get(), HeartParticle.AngryVillagerProvider::new);
    }

    @EventCalled
    private static void registerEntityRenderers(ClientPlatformHelper.EntityRendererEvent event) {
        event.register(ModRegistry.SPLATTERED_EGG_ENTITY.get(), SplatteredEggRenderer::new);
    }

    @EventCalled
    private static void registerTooltipComponent(ClientPlatformHelper.TooltipComponentEvent event) {
        event.register(CarvingManager.Key.class, CarvingTooltipComponent::new);
    }

    @EventCalled
    private static void registerTextures(ClientPlatformHelper.AtlasTextureEvent event) {
        event.addSprite(CARVED_PUMPKIN_BACKGROUND.texture());
        event.addSprite(CARVED_PUMPKIN_SHADE.texture());
        event.addSprite(JACK_O_LANTERN_SHADE_1.texture());
        event.addSprite(PUMPKIN_HIGHLIGHT.texture());
        event.addSprite(CARVING_OUTLINE.texture());
    }

    @EventCalled
    private static void registerBlockEntityRenderers(ClientPlatformHelper.BlockEntityRendererEvent event) {
        event.register(ModRegistry.MOD_CARVED_PUMPKIN_TILE.get(), CarvedPumpkinTileRenderer::new);
    }

    @EventCalled
    private static void registerModelLoaders(ClientPlatformHelper.ModelLoaderEvent event) {
        event.register(HauntedHarvest.res("carved_pumpkin"), new CarvedPumpkinBlockLoader());
    }

    @EventCalled
    private static void registerSpecialModels(ClientPlatformHelper.SpecialModelEvent event) {
        event.register(JACK_O_LANTERN_FRAME);
        event.register(PUMPKIN_FRAME);
    }


}