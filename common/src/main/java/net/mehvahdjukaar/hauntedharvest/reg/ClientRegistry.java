package net.mehvahdjukaar.hauntedharvest.reg;

import net.mehvahdjukaar.hauntedharvest.HauntedHarvest;
import net.mehvahdjukaar.hauntedharvest.client.CarvedPumpkinBlockLoader;
import net.mehvahdjukaar.hauntedharvest.client.CarvedPumpkinTileRenderer;
import net.mehvahdjukaar.hauntedharvest.client.SplatteredEggRenderer;
import net.mehvahdjukaar.moonlight.api.misc.EventCalled;
import net.mehvahdjukaar.moonlight.api.platform.ClientPlatformHelper;
import net.minecraft.client.particle.HeartParticle;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.resources.model.Material;
import net.minecraft.resources.ResourceLocation;

import static net.minecraft.client.renderer.texture.TextureAtlas.LOCATION_BLOCKS;

public class ClientRegistry {

    public static final Material CARVED_PUMPKIN_BACKGROUND = new Material(LOCATION_BLOCKS,HauntedHarvest.res("block/carved_pumpkin_background"));
    public static final Material CARVED_PUMPKIN_SHADE = new Material(LOCATION_BLOCKS,HauntedHarvest.res("block/carved_pumpkin_shade"));

    public static final ResourceLocation JACK_O_LANTERN_BACKGROUND = HauntedHarvest.res("block/jack_o_lantern_background");
    public static final ResourceLocation JACK_O_LANTERN_SHADE_1 = HauntedHarvest.res("block/jack_o_lantern_shade_1");
    public static final ResourceLocation JACK_O_LANTERN_SHADE_2 = HauntedHarvest.res("block/jack_o_lantern_shade_2");

    public static final Material PUMPKIN_HIGHLIGHT =  new Material(LOCATION_BLOCKS,HauntedHarvest.res("block/pumpkin_highlight"));
    public static final Material PUMPKIN =  new Material(LOCATION_BLOCKS,new ResourceLocation("block/pumpkin_side"));

    public static final Material CARVING_OUTLINE = new Material(LOCATION_BLOCKS, HauntedHarvest.res("block/carving_grid"));


    public static void init() {
        ClientPlatformHelper.addEntityRenderersRegistration(ClientRegistry::registerEntityRenderers);
        ClientPlatformHelper.addParticleRegistration(ClientRegistry::registerParticles);
        ClientPlatformHelper.addAtlasTextureCallback(TextureAtlas.LOCATION_BLOCKS, ClientRegistry::registerTextures);
        ClientPlatformHelper.addModelLoaderRegistration(ClientRegistry::registerModelLoaders);
        ClientPlatformHelper.addBlockEntityRenderersRegistration(ClientRegistry::registerBlockEntityRenderers);
    }

    private static void registerTextures(ClientPlatformHelper.AtlasTextureEvent event) {
        //event.addSprite(PUMPKIN_HIGHLIGHT_TEXTURE);
        //event.addSprite(PUMPKIN_BACKGROUND_TEXTURE);
        event.addSprite(CARVED_PUMPKIN_BACKGROUND.texture());
        event.addSprite(CARVED_PUMPKIN_SHADE.texture());
        event.addSprite(PUMPKIN_HIGHLIGHT.texture());
        event.addSprite(CARVING_OUTLINE.texture());
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
    private static void registerBlockEntityRenderers(ClientPlatformHelper.BlockEntityRendererEvent event) {
        event.register(ModRegistry.MOD_CARVED_PUMPKIN_TILE.get(), CarvedPumpkinTileRenderer::new);
    }

    @EventCalled
    private static void registerModelLoaders(ClientPlatformHelper.ModelLoaderEvent event) {
        event.register(HauntedHarvest.res("carved_pumpkin"), new CarvedPumpkinBlockLoader());
    }
}