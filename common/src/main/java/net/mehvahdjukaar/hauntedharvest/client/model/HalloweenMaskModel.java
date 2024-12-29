package net.mehvahdjukaar.hauntedharvest.client.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.HeadedModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.world.entity.npc.Villager;

public class HalloweenMaskModel<T extends Villager> extends EntityModel<T> implements HeadedModel {
    private final ModelPart mask;

    public HalloweenMaskModel(ModelPart modelPart) {
        this.mask = modelPart.getChild("head").getChild("hat");
        mask.getChild("hat_rim").visible = false;
    }


    @Override
    public void setupAnim(T pEntity, float pLimbSwing, float pLimbSwingAmount, float pAgeInTicks, float pNetHeadYaw, float pHeadPitch) {
    }

    @Override
    public ModelPart getHead() {
        return mask;
    }

    @Override
    public void renderToBuffer(PoseStack pPoseStack, VertexConsumer pBuffer, int pPackedLight, int pPackedOverlay, int color) {
        mask.render(pPoseStack, pBuffer, pPackedLight, pPackedOverlay, color);
    }

}
