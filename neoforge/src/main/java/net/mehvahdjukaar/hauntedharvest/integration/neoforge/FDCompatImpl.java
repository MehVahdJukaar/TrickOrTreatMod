package net.mehvahdjukaar.hauntedharvest.integration.neoforge;

import net.mehvahdjukaar.hauntedharvest.integration.FDCompat;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.food.FoodProperties;
import vectorwing.farmersdelight.common.registry.ModEffects;

public class FDCompatImpl {
    public static FoodProperties makeFood() {
        return new FoodProperties.Builder()
                .nutrition(12)
                .saturationModifier(0.8F)
                .effect(() -> new MobEffectInstance(FDCompat.NOURISHMENT.get(), 3600, 0), 1.0F)
                .build();
    }
}
