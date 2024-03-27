package net.mehvahdjukaar.hauntedharvest.integration.fabric;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.food.FoodProperties;

import static net.mehvahdjukaar.hauntedharvest.integration.FDCompat.NOURISHMENT;

public class FDCompatImpl {

    public static FoodProperties makeFood() {
        return new FoodProperties.Builder()
                .nutrition(12)
                .saturationMod(0.8F)
                .effect(new MobEffectInstance(NOURISHMENT.get(), 3600, 0), 1.0F)
                .build();
    }
}
