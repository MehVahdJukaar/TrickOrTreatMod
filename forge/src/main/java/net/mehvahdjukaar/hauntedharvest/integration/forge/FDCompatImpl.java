package net.mehvahdjukaar.hauntedharvest.integration.forge;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.food.FoodProperties;
import vectorwing.farmersdelight.common.registry.ModEffects;

public class FDCompatImpl {
    public static FoodProperties makeFood() {
        return new FoodProperties.Builder()
                .nutrition(12)
                .saturationMod(0.8F)
                .effect(() -> new MobEffectInstance(ModEffects.NOURISHMENT.get(), 3600, 0), 1.0F)
                .build();
    }
}
