package net.mehvahdjukaar.hauntedharvest.reg;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.mehvahdjukaar.hauntedharvest.HauntedHarvest;
import net.mehvahdjukaar.hauntedharvest.blocks.PumpkinType;
import net.mehvahdjukaar.hauntedharvest.client.CarvedPumpkinTileRenderer;
import net.mehvahdjukaar.hauntedharvest.client.CarvingManager;
import net.mehvahdjukaar.hauntedharvest.client.HalloweenMaskLayer;
import net.mehvahdjukaar.hauntedharvest.client.SplatteredEggRenderer;
import net.mehvahdjukaar.hauntedharvest.client.gui.CarvingTooltipComponent;
import net.mehvahdjukaar.hauntedharvest.client.model.CarvedPumpkinBakedModel;
import net.mehvahdjukaar.moonlight.api.client.CoreShaderContainer;
import net.mehvahdjukaar.moonlight.api.client.model.NestedModelLoader;
import net.mehvahdjukaar.moonlight.api.misc.EventCalled;
import net.mehvahdjukaar.moonlight.api.platform.ClientHelper;
import net.minecraft.Util;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.particle.HeartParticle;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.client.resources.model.Material;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Blocks;

import java.util.Map;

public class ClientRegistry {

    public static final ResourceLocation LOCATION_BLOCKS = ResourceLocation.parse("textures/atlas/blocks.png");

    public static final ModelLayerLocation VILLAGER_MASK = loc("villager_mask");

    public static final Material PUMPKIN_HIGHLIGHT = new Material(LOCATION_BLOCKS, HauntedHarvest.res("block/pumpkin_highlight"));
    public static final Material PUMPKIN = new Material(LOCATION_BLOCKS, ResourceLocation.parse("block/pumpkin_side"));
    public static final Material CARVING_OUTLINE = new Material(LOCATION_BLOCKS, HauntedHarvest.res("block/carving_grid"));

    public static final ResourceLocation PAPER_BAG_OVERLAY =HauntedHarvest.res("textures/misc/paper_bag_overlay.png");

    public static final CoreShaderContainer BLUR_SHARED = new CoreShaderContainer(GameRenderer::getPositionTexColorShader);

    private static final Map<PumpkinType, Material[]> PUMPKIN_MATERIALS = Util.make(() -> {
        var l = new Object2ObjectOpenHashMap<PumpkinType, Material[]>();
        for (var t : PumpkinType.getTypes()) {
            Material shade = new Material(LOCATION_BLOCKS, HauntedHarvest.res("block/" + t.getName() + "_shade"));
            Material background = new Material(LOCATION_BLOCKS, HauntedHarvest.res("block/" + t.getName() + "_background"));
            l.put(t, new Material[]{ClientRegistry.PUMPKIN, shade, background, PUMPKIN_HIGHLIGHT});
        }
        return l;
    });

    private static final Map<PumpkinType, ResourceLocation> PUMPKIN_FRAMES = Util.make(() -> {
        var l = new Object2ObjectOpenHashMap<PumpkinType, ResourceLocation>();
        for (var t : PumpkinType.getTypes()) {
            l.put(t, HauntedHarvest.res("block/" + t.getName() + "_frame"));
        }
        return l;
    });

    public static Material getMaterial(PumpkinType type, int ordinal) {
        var m = PUMPKIN_MATERIALS.get(type);
        if (m != null) return m[ordinal];
        else {
            throw new NullPointerException();
        }
    }

    private static ModelLayerLocation loc(String name) {
        return new ModelLayerLocation(HauntedHarvest.res(name), name);
    }

    public static ResourceLocation getFrame(PumpkinType type) {
        return PUMPKIN_FRAMES.getOrDefault(type, PUMPKIN_FRAMES.get(PumpkinType.JACK));
    }

    public static void init() {
        ClientHelper.addEntityRenderersRegistration(ClientRegistry::registerEntityRenderers);
        ClientHelper.addParticleRegistration(ClientRegistry::registerParticles);
        ClientHelper.addModelLoaderRegistration(ClientRegistry::registerModelLoaders);
        ClientHelper.addBlockEntityRenderersRegistration(ClientRegistry::registerBlockEntityRenderers);
        ClientHelper.addTooltipComponentRegistration(ClientRegistry::registerTooltipComponent);
        ClientHelper.addSpecialModelRegistration(ClientRegistry::registerSpecialModels);
        ClientHelper.addModelLayerRegistration(ClientRegistry::registerModelLayers);
        ClientHelper.addShaderRegistration(ClientRegistry::registerShaders);

        ClientHelper.addClientSetup(ClientRegistry::setup);
    }

    public static void setup() {
        ClientHelper.registerRenderType(ModRegistry.CORN_BASE.get(), RenderType.cutout());
        ClientHelper.registerRenderType(ModRegistry.CORN_MIDDLE.get(), RenderType.cutout());
        ClientHelper.registerRenderType(ModRegistry.CORN_TOP.get(), RenderType.cutout());
        PumpkinType.getTypes().forEach(t -> ClientHelper.registerRenderType(t.getPumpkin(), RenderType.cutout()));
        ClientHelper.registerRenderType(Blocks.JACK_O_LANTERN, RenderType.cutout());
        ClientHelper.registerRenderType(ModRegistry.CORN_POT.get(), RenderType.cutout());
    }

    @EventCalled
    private static void registerModelLayers(ClientHelper.ModelLayerEvent event) {
        event.register(VILLAGER_MASK, HalloweenMaskLayer::createMesh);
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
        event.register(CarvingManager.Key.class, CarvingTooltipComponent::new);
    }

    @EventCalled
    private static void registerBlockEntityRenderers(ClientHelper.BlockEntityRendererEvent event) {
        event.register(ModRegistry.MOD_CARVED_PUMPKIN_TILE.get(), CarvedPumpkinTileRenderer::new);
    }

    @EventCalled
    private static void registerModelLoaders(ClientHelper.ModelLoaderEvent event) {
        event.register(HauntedHarvest.res("carved_pumpkin"), new NestedModelLoader("model", CarvedPumpkinBakedModel::new));
    }

    @EventCalled
    private static void registerSpecialModels(ClientHelper.SpecialModelEvent event) {
        for (var v : PUMPKIN_FRAMES.values()) {
            event.register(v);
        }
    }

    @EventCalled
    private static void registerShaders(ClientHelper.ShaderEvent event) {
        event.register(HauntedHarvest.res("blur"), DefaultVertexFormat.POSITION_TEX, BLUR_SHARED::assign);
    }

    public static ShaderInstance getBlur() {
        // blur.getUniform("Radius").set(8f);
        return BLUR_SHARED.get();
    }


}