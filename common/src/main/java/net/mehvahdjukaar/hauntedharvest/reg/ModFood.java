package net.mehvahdjukaar.hauntedharvest.reg;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.food.FoodProperties;

public class ModFood {

    public static final FoodProperties POPCORN = (new FoodProperties.Builder())
            .nutrition(2).saturationMod(0.4f).fast().alwaysEat().build();

    public static final FoodProperties CANDY_CORN = (new FoodProperties.Builder())
            .nutrition(2).saturationMod(0.2f).fast().alwaysEat().build();

    public static final FoodProperties CORNBREAD = (new FoodProperties.Builder())
            .nutrition(4).saturationMod(0.6f).build();

    public static final FoodProperties CORN_ON_THE_COB = (new FoodProperties.Builder())
            .nutrition(4).saturationMod(0.9f).build();

    public static final FoodProperties ROTTEN_APPLE = (new FoodProperties.Builder()).nutrition(2).saturationMod(0.1F)
            .effect(new MobEffectInstance(MobEffects.HUNGER, 600, 0), 0.2F).meat().build();

    public static final FoodProperties DEATH_APPLE = (new FoodProperties.Builder()).nutrition(4).saturationMod(0.3F)
            .effect(new MobEffectInstance(MobEffects.HUNGER, 2400, 0), 1)
            .effect(new MobEffectInstance(MobEffects.CONFUSION, 200, 0), 1.0F)
            .effect(new MobEffectInstance(MobEffects.WITHER, 230, 1), 1.0F)
            .effect(new MobEffectInstance(MobEffects.POISON, 500, 0), 1.0F)
            .effect(new MobEffectInstance(MobEffects.WEAKNESS, 6000, 0), 1.0F)
            .meat().alwaysEat().build();


}
