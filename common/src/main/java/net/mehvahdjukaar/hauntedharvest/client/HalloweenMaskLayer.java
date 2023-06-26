package net.mehvahdjukaar.hauntedharvest.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.mehvahdjukaar.hauntedharvest.HauntedHarvest;
import net.mehvahdjukaar.hauntedharvest.configs.CommonConfigs;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.HeadedModel;
import net.minecraft.client.model.VillagerModel;
import net.minecraft.client.model.geom.LayerDefinitions;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.npc.VillagerDataHolder;

public class HalloweenMaskLayer<T extends Villager & VillagerDataHolder, M extends EntityModel<T> & HeadedModel> extends RenderLayer<T, M> {

    private static final ResourceLocation[] TEXTURES = {
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

    private final HalloweenMaskModel<T> headModel;

    public HalloweenMaskLayer(RenderLayerParent<T, M> parent, EntityRendererProvider.Context context) {
        super(parent);
        this.headModel = new HalloweenMaskModel<>(context.bakeLayer(ModelLayers.VILLAGER));
    }

    @Override
    protected ResourceLocation getTextureLocation(T entity) {
        return TEXTURES[(int) Math.abs(entity.getUUID().getLeastSignificantBits() % (CommonConfigs.PAPER_BAG.get() ? 9 : 8))];
    }

    @Override
    public void render(PoseStack pMatrixStack, MultiBufferSource pBuffer, int pPackedLight, T pLivingEntity, float pLimbSwing, float pLimbSwingAmount, float pPartialTicks, float pAgeInTicks, float pNetHeadYaw, float pHeadPitch) {
        if (pLivingEntity.isBaby() && HauntedHarvest.isTrickOrTreatTime(pLivingEntity.level())) {
            headModel.prepareMobModel(pLivingEntity, pLimbSwing, pLimbSwingAmount, pPartialTicks);
            this.getParentModel().copyPropertiesTo(headModel);
            headModel.getHead().copyFrom(this.getParentModel().getHead());
            VertexConsumer vertexconsumer = pBuffer.getBuffer(RenderType.entityCutoutNoCull(this.getTextureLocation(pLivingEntity)));
            headModel.setupAnim(pLivingEntity, pLimbSwing, pLimbSwingAmount, pAgeInTicks, pNetHeadYaw, pHeadPitch);
            headModel.renderToBuffer(pMatrixStack, vertexconsumer, pPackedLight, OverlayTexture.NO_OVERLAY, 1, 1, 1, 1.0F);
        }
    }

    public static LayerDefinition createMesh(){
        var m = VillagerModel.createBodyModel();
        return LayerDefinition.create(m, 64,64);
    }
}
