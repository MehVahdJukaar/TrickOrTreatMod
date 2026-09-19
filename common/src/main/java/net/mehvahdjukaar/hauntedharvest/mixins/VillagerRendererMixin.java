package net.mehvahdjukaar.hauntedharvest.mixins;

import net.mehvahdjukaar.hauntedharvest.HauntedHarvest;
import net.mehvahdjukaar.hauntedharvest.ai.IHalloweenVillager;
import net.mehvahdjukaar.hauntedharvest.client.HalloweenMaskLayer;
import net.mehvahdjukaar.hauntedharvest.client.IHalloweenVillagerRenderState;
import net.mehvahdjukaar.moonlight.api.platform.PlatHelper;
import net.minecraft.client.model.npc.VillagerModel;
import net.minecraft.client.renderer.entity.AgeableMobRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.VillagerRenderer;
import net.minecraft.client.renderer.entity.state.VillagerRenderState;
import net.minecraft.world.entity.npc.villager.Villager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(VillagerRenderer.class)
public abstract class VillagerRendererMixin extends AgeableMobRenderer<Villager, VillagerRenderState, VillagerModel> {

    protected VillagerRendererMixin(EntityRendererProvider.Context context, VillagerModel adultModel, VillagerModel babyModel, float shadow) {
        super(context, adultModel, babyModel, shadow);
    }

    @Inject(method = "<init>", at = @At("TAIL"))
    public void init(EntityRendererProvider.Context context, CallbackInfo ci){
        try {
            if(PlatHelper.isModLoadingValid()) {
                this.addLayer(new HalloweenMaskLayer(this, context));
            }
        }catch (Exception e){
            HauntedHarvest.LOGGER.error("Failed to add villager mask layers. This might be due to failed mod loading");
        }
    }

    @Inject(method = "extractRenderState(Lnet/minecraft/world/entity/npc/villager/Villager;Lnet/minecraft/client/renderer/entity/state/VillagerRenderState;F)V",
            at = @At("TAIL"))
    public void extractHalloweenState(Villager villager, VillagerRenderState state, float partialTicks, CallbackInfo ci) {
        var halloweenState = (IHalloweenVillagerRenderState) state;
        halloweenState.hauntedharvest$setMaskTexture(HalloweenMaskLayer.getMaskTexture(villager));
        halloweenState.hauntedharvest$setConverting(villager instanceof IHalloweenVillager v && v.hauntedharvest$isConverting());
    }

    @Override
    protected boolean isShaking(VillagerRenderState state) {
        return super.isShaking(state) || ((IHalloweenVillagerRenderState) state).hauntedharvest$isConverting();
    }
}
