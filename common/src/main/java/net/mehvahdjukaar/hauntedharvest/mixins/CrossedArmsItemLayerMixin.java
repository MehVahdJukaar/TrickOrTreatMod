package net.mehvahdjukaar.hauntedharvest.mixins;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.model.VillagerLikeModel;
import net.minecraft.client.model.npc.BabyVillagerModel;
import net.minecraft.client.renderer.entity.layers.CrossedArmsItemLayer;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(CrossedArmsItemLayer.class)
public abstract class CrossedArmsItemLayerMixin {

    @WrapOperation(method = "applyTranslation",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/client/model/VillagerLikeModel;translateToArms(Lnet/minecraft/client/renderer/entity/state/EntityRenderState;Lcom/mojang/blaze3d/vertex/PoseStack;)V"))
    private void hauntedharvest$babyVillagerArms(VillagerLikeModel<?> model, EntityRenderState state, PoseStack poseStack, Operation<Void> original) {
        original.call(model, state, poseStack);
        if (model instanceof BabyVillagerModel) {
            //fixes baby held itme ofset
            poseStack.translate(0, 0.9f / 16f, -1.8f / 16f);
            poseStack.scale(0.5f, 0.5f, 0.5f);
            poseStack.mulPose(Axis.XP.rotation(-0.75f));
            poseStack.translate(0, -4 / 16f, 0);
        }
    }
}
