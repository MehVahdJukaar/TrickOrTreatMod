package net.mehvahdjukaar.hauntedharvest.reg;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.component.Consumable;
import net.minecraft.world.item.component.Consumables;
import net.minecraft.world.item.consume_effects.ApplyStatusEffectsConsumeEffect;

public class ModFoods {

    //vanilla's fast eat speed, from dried kelp
    private static final float FAST_SECONDS = 0.8F;

    public static final FoodProperties POPCORN = (new FoodProperties.Builder())
            .nutrition(2).saturationModifier(0.4f).alwaysEdible().build();

    public static final Consumable POPCORN_CONSUMABLE = fast().build();

    public static final FoodProperties CANDY_CORN = (new FoodProperties.Builder())
            .nutrition(2).saturationModifier(0.2f).alwaysEdible().build();

    public static final Consumable CANDY_CORN_CONSUMABLE = fast().build();

    public static final FoodProperties CORNBREAD = (new FoodProperties.Builder())
            .nutrition(4).saturationModifier(0.6f).build();

    public static final FoodProperties CORN_ON_THE_COB = (new FoodProperties.Builder())
            .nutrition(4).saturationModifier(0.9f).build();

    public static final FoodProperties ROTTEN_APPLE = (new FoodProperties.Builder()).nutrition(2).saturationModifier(0.1F).build();

    public static final Consumable ROTTEN_APPLE_CONSUMABLE = Consumables.defaultFood()
            .onConsume(new ApplyStatusEffectsConsumeEffect(new MobEffectInstance(MobEffects.HUNGER, 600, 0), 0.2F))
            .build();

    public static final FoodProperties DEATH_APPLE = (new FoodProperties.Builder()).nutrition(4).saturationModifier(0.3F)
            .alwaysEdible().build();

    public static final Consumable DEATH_APPLE_CONSUMABLE = Consumables.defaultFood()
            .onConsume(new ApplyStatusEffectsConsumeEffect(new MobEffectInstance(MobEffects.HUNGER, 2400, 0)))
            .onConsume(new ApplyStatusEffectsConsumeEffect(new MobEffectInstance(MobEffects.NAUSEA, 200, 0)))
            .onConsume(new ApplyStatusEffectsConsumeEffect(new MobEffectInstance(MobEffects.WITHER, 230, 1)))
            .onConsume(new ApplyStatusEffectsConsumeEffect(new MobEffectInstance(MobEffects.POISON, 500, 0)))
            .onConsume(new ApplyStatusEffectsConsumeEffect(new MobEffectInstance(MobEffects.WEAKNESS, 6000, 0)))
            .build();

    private static Consumable.Builder fast() {
        return Consumables.defaultFood().consumeSeconds(FAST_SECONDS);
    }
}
