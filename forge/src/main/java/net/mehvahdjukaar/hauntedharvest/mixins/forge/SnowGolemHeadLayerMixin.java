package net.mehvahdjukaar.hauntedharvest.mixins.forge;

import com.mojang.blaze3d.vertex.PoseStack;
import net.mehvahdjukaar.hauntedharvest.forge.ICustomPumpkinHolder;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.layers.SnowGolemHeadLayer;
import net.minecraft.world.entity.animal.SnowGolem;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SnowGolemHeadLayer.class)
public abstract class SnowGolemHeadLayerMixin {

    @Shadow
    @Final
    private ItemRenderer itemRenderer;


    @Inject(method = "render(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;ILnet/minecraft/world/entity/animal/SnowGolem;FFFFFF)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/entity/ItemRenderer;renderStatic(Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/item/ItemDisplayContext;ZLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;Lnet/minecraft/world/level/Level;III)V",
                    shift = At.Shift.BEFORE),
            require = 1, cancellable = true
    )
    private void renderCustomPumpkin(PoseStack matrixStack, MultiBufferSource buffer, int packedLight,
                                     SnowGolem snowGolem, float limbSwing, float limbSwingAmount,
                                     float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, CallbackInfo ci) {
        if (snowGolem instanceof ICustomPumpkinHolder cp) {
            ItemStack stack = cp.hauntedharvest$getCustomPumpkin();
            if (!stack.isEmpty()) {
                this.itemRenderer
                        .renderStatic(
                                snowGolem,
                                stack,
                                ItemDisplayContext.HEAD,
                                false,
                                matrixStack,
                                buffer,
                                snowGolem.level(),
                                packedLight,
                                LivingEntityRenderer.getOverlayCoords(snowGolem, 0.0F),
                                snowGolem.getId()
                        );
                matrixStack.popPose();
                ci.cancel();
            }
        }
    }

}
