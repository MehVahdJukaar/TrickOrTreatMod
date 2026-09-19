package net.mehvahdjukaar.hauntedharvest.mixins;

import com.mojang.blaze3d.vertex.PoseStack;
import net.mehvahdjukaar.hauntedharvest.client.ICustomPumpkinRenderState;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.layers.SnowGolemHeadLayer;
import net.minecraft.client.renderer.entity.state.SnowGolemRenderState;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SnowGolemHeadLayer.class)
public abstract class SnowGolemHeadLayerMixin {

    @Inject(method = "submit(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;ILnet/minecraft/client/renderer/entity/state/SnowGolemRenderState;FF)V",
            at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/vertex/PoseStack;translate(FFF)V", ordinal = 1),
            require = 1, cancellable = true
    )
    private void renderCustomPumpkin(PoseStack poseStack, SubmitNodeCollector collector, int light,
                                     SnowGolemRenderState state, float yRot, float xRot, CallbackInfo ci) {
        ItemStackRenderState customPumpkin = ((ICustomPumpkinRenderState) state).hauntedharvest$getCustomPumpkin();
        if (!customPumpkin.isEmpty()) {
            customPumpkin.submit(poseStack, collector, light, LivingEntityRenderer.getOverlayCoords(state, 0.0F), state.outlineColor);
            poseStack.popPose();
            ci.cancel();
        }
    }

}
