package net.mehvahdjukaar.hauntedharvest.mixins;

import net.mehvahdjukaar.hauntedharvest.ai.IHalloweenVillager;
import net.mehvahdjukaar.hauntedharvest.client.HalloweenMaskLayer;
import net.minecraft.client.model.VillagerModel;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.VillagerRenderer;
import net.minecraft.world.entity.npc.Villager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(VillagerRenderer.class)
public abstract class VillagerRendererMixin extends MobRenderer<Villager, VillagerModel<Villager>> {

    protected VillagerRendererMixin(EntityRendererProvider.Context context, VillagerModel<Villager> villagerVillagerModel, float v) {
        super(context, villagerVillagerModel, v);
    }

    @Inject(method = "<init>", at = @At("TAIL"))
    public void init(EntityRendererProvider.Context context, CallbackInfo ci){
        this.addLayer(new HalloweenMaskLayer<>(this, context));
    }

    @Override
    public boolean isShaking(Villager villager) {
        return super.isShaking(villager) || (villager instanceof IHalloweenVillager v && v.isConverting());
    }
}