package net.mehvahdjukaar.hauntedharvest.mixins;

import net.mehvahdjukaar.hauntedharvest.client.ICustomPumpkinRenderState;
import net.mehvahdjukaar.hauntedharvest.entity.ICustomPumpkinHolder;
import net.minecraft.client.model.animal.golem.SnowGolemModel;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.SnowGolemRenderer;
import net.minecraft.client.renderer.entity.state.SnowGolemRenderState;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.world.entity.animal.golem.SnowGolem;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SnowGolemRenderer.class)
public abstract class SnowGolemRendererMixin extends MobRenderer<SnowGolem, SnowGolemRenderState, SnowGolemModel> {

    protected SnowGolemRendererMixin(EntityRendererProvider.Context context, SnowGolemModel model, float shadow) {
        super(context, model, shadow);
    }

    @Inject(method = "extractRenderState(Lnet/minecraft/world/entity/animal/golem/SnowGolem;Lnet/minecraft/client/renderer/entity/state/SnowGolemRenderState;F)V",
            at = @At("TAIL"))
    public void extractCustomPumpkin(SnowGolem golem, SnowGolemRenderState state, float partialTicks, CallbackInfo ci) {
        ItemStackRenderState customPumpkinState = ((ICustomPumpkinRenderState) state).hauntedharvest$getCustomPumpkin();
        customPumpkinState.clear();
        if (golem instanceof ICustomPumpkinHolder cp) {
            ItemStack stack = cp.hauntedharvest$getCustomPumpkin();
            if (!stack.isEmpty()) {
                this.itemModelResolver.updateForLiving(customPumpkinState, stack, ItemDisplayContext.HEAD, golem);
            }
        }
    }
}
