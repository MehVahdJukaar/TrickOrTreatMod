package net.mehvahdjukaar.hauntedharvest.reg;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.mehvahdjukaar.hauntedharvest.HauntedHarvest;
import net.mehvahdjukaar.hauntedharvest.blocks.PumpkinType;
import net.mehvahdjukaar.hauntedharvest.client.*;
import net.mehvahdjukaar.hauntedharvest.client.model.CarvedPumpkinBakedModel;
import net.mehvahdjukaar.hauntedharvest.client.screens.CarvingTooltipComponent;
import net.mehvahdjukaar.hauntedharvest.items.components.PumpkinCarvingData;
import net.mehvahdjukaar.moonlight.api.client.CoreShaderContainer;
import net.mehvahdjukaar.moonlight.api.client.ItemRenderExtension;
import net.mehvahdjukaar.moonlight.api.client.model.NestedModelLoader;
import net.mehvahdjukaar.moonlight.api.client.util.RenderUtil;
import net.mehvahdjukaar.moonlight.api.misc.EventCalled;
import net.mehvahdjukaar.moonlight.api.platform.ClientHelper;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.particle.HeartParticle;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Blocks;

import java.util.Map;

public class ClientRegistry {

    public static final ResourceLocation LOCATION_BLOCKS = ResourceLocation.parse("textures/atlas/blocks.png");

    public static final ModelLayerLocation VILLAGER_MASK = loc("villager_mask");

    public static final Material PUMPKIN_HIGHLIGHT = new Material(LOCATION_BLOCKS, HauntedHarvest.res("block/pumpkin_highlight"));
    public static final Material PUMPKIN = new Material(LOCATION_BLOCKS, ResourceLocation.parse("block/pumpkin_side"));
    public static final Material CARVING_OUTLINE = new Material(LOCATION_BLOCKS, HauntedHarvest.res("block/carving_grid"));

    public static final ResourceLocation PAPER_BAG_OVERLAY = HauntedHarvest.res("textures/misc/paper_bag_overlay.png");

    public static final ResourceLocation OUTLINE_SPRITE = HauntedHarvest.res("outline");

    public static final CoreShaderContainer BLUR_SHARED = new CoreShaderContainer(GameRenderer::getPositionTexColorShader);

    private static final Map<PumpkinType, Material[]> PUMPKIN_MATERIALS = new Object2ObjectOpenHashMap<>();
    private static final Map<PumpkinType, ModelResourceLocation> PUMPKIN_FRAMES = new Object2ObjectOpenHashMap<>();

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

    public static ModelResourceLocation getPumpkinFrame(PumpkinType type) {
        return PUMPKIN_FRAMES.getOrDefault(type, PUMPKIN_FRAMES.get(PumpkinType.JACK.get()));
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
        ClientHelper.addItemRenderersRegistration(ClientRegistry::registerItemRenderers);
        ClientHelper.addClientSetup(ClientRegistry::setup);
        SeasonConfigOverlay.register();
    }


    public static void setup() {
        ClientHelper.registerRenderType(ModRegistry.CORN_BASE.get(), RenderType.cutout());
        ClientHelper.registerRenderType(ModRegistry.CORN_MIDDLE.get(), RenderType.cutout());
        ClientHelper.registerRenderType(ModRegistry.CORN_TOP.get(), RenderType.cutout());
        ClientHelper.registerRenderType(Blocks.JACK_O_LANTERN, RenderType.cutout());
        ClientHelper.registerRenderType(ModRegistry.CORN_POT.get(), RenderType.cutout());

        for (var t : PumpkinType.REGISTRY) {
            ClientHelper.registerRenderType(t.getPumpkin(), RenderType.cutout());

            Material shade = new Material(LOCATION_BLOCKS, HauntedHarvest.res("block/" + t.getTextureKey() + "_shade"));
            Material background = new Material(LOCATION_BLOCKS, HauntedHarvest.res("block/" + t.getTextureKey() + "_background"));
            PUMPKIN_MATERIALS.put(t, new Material[]{ClientRegistry.PUMPKIN, shade, background, PUMPKIN_HIGHLIGHT});

            PUMPKIN_FRAMES.put(t, RenderUtil.getStandaloneModelLocation(
                    HauntedHarvest.res("block/" + t.getTextureKey() + "_frame")));
        }
    }


    @EventCalled
    private static void registerItemRenderers(ClientHelper.ItemRendererEvent event) {
        CarvedPumpkinItemRenderer renderer = new CarvedPumpkinItemRenderer();
        event.register(ModRegistry.CARVED_PUMPKIN.get(), (ItemRenderExtension) renderer);
        event.register(ModRegistry.JACK_O_LANTERN.get(), (ItemRenderExtension) renderer);
        event.register(ModRegistry.PAPER_BAG.get(), new PaperBagRenderExtension());
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
        event.register(PumpkinCarvingData.class, CarvingTooltipComponent::new);
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