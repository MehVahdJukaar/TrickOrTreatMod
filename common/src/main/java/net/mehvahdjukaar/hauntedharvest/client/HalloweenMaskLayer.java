package net.mehvahdjukaar.hauntedharvest.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.mehvahdjukaar.hauntedharvest.HauntedHarvest;
import net.mehvahdjukaar.hauntedharvest.client.model.HalloweenMaskModel;
import net.mehvahdjukaar.hauntedharvest.configs.CommonConfigs;
import net.mehvahdjukaar.hauntedharvest.reg.ClientRegistry;
import net.minecraft.client.model.npc.VillagerModel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.state.VillagerRenderState;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.npc.villager.Villager;
import org.jetbrains.annotations.Nullable;

public class HalloweenMaskLayer extends RenderLayer<VillagerRenderState, VillagerModel> {

    private static final Identifier[] TEXTURES = {
            HauntedHarvest.res("textures/entity/villager/masks/pumpkin.png"),
            HauntedHarvest.res("textures/entity/villager/masks/zombie.png"),
            HauntedHarvest.res("textures/entity/villager/masks/skeleton.png"),
            HauntedHarvest.res("textures/entity/villager/masks/spider.png"),
            HauntedHarvest.res("textures/entity/villager/masks/enderman.png"),
            HauntedHarvest.res("textures/entity/villager/masks/creeper.png"),
            HauntedHarvest.res("textures/entity/villager/masks/vindicator.png"),
            HauntedHarvest.res("textures/entity/villager/masks/piglin.png"),
            HauntedHarvest.res("textures/entity/villager/masks/paper_bag.png")
    };

    private final HalloweenMaskModel maskModel;

    public HalloweenMaskLayer(RenderLayerParent<VillagerRenderState, VillagerModel> parent, EntityRendererProvider.Context context) {
        super(parent);
        this.maskModel = new HalloweenMaskModel(context.bakeLayer(ClientRegistry.VILLAGER_MASK));
    }

    @Nullable
    public static Identifier getMaskTexture(Villager villager) {
        if (!villager.isBaby() || !HauntedHarvest.isTrickOrTreatTime(villager.level())) return null;
        return TEXTURES[(int) Math.abs(villager.getUUID().getLeastSignificantBits() % (CommonConfigs.PAPER_BAG.get() ? 9 : 8))];
    }

    @Override
    public void submit(PoseStack poseStack, SubmitNodeCollector collector, int light, VillagerRenderState state, float yRot, float xRot) {
        Identifier mask = ((IHalloweenVillagerRenderState) state).hauntedharvest$getMaskTexture();
        if (mask != null) {
            coloredCutoutModelCopyLayerRender(this.maskModel, mask, poseStack, collector, light, state, -1, 1);
        }
    }
}
