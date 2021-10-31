package net.mehvahdjukaar.hauntedharvest.mixins;

import com.mojang.blaze3d.vertex.PoseStack;
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


    public VillagerRendererMixin(EntityRendererProvider.Context context, VillagerModel<Villager> villagerVillagerModel, float v) {
        super(context, villagerVillagerModel, v);

    }

    @Inject(method = "<init>", at = @At("TAIL"))
    public void init(EntityRendererProvider.Context context, CallbackInfo ci){
        this.addLayer(new HalloweenMaskLayer<>(this, context));
    }

    @Override
    public void setupRotations(Villager pEntityLiving, PoseStack pMatrixStack, float pAgeInTicks, float pRotationYaw, float pPartialTicks) {
        if (this.isShaking(pEntityLiving)) {
            pRotationYaw += (float)(Math.cos((double)pEntityLiving.tickCount * 3.25D) * Math.PI * (double)0.4F);
        }
        super.setupRotations(pEntityLiving, pMatrixStack, pAgeInTicks, pRotationYaw, pPartialTicks);

    }

    @Override
    public boolean isShaking(Villager villager) {
        return (villager instanceof IHalloweenVillager v && v.isConverting());
    }
}