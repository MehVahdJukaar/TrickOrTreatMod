package net.mehvahdjukaar.hauntedharvest.reg;

import net.mehvahdjukaar.hauntedharvest.HauntedHarvest;
import net.mehvahdjukaar.hauntedharvest.ai.PumpkinPoiSensor;
import net.mehvahdjukaar.hauntedharvest.entity.SplatteredEggEntity;
import net.mehvahdjukaar.hauntedharvest.items.GrimAppleItem;
import net.mehvahdjukaar.hauntedharvest.loot.AddItemModifier;
import net.mehvahdjukaar.moonlight.api.platform.RegHelper;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.sensing.SensorType;
import net.minecraft.world.entity.schedule.Activity;
import net.minecraft.world.entity.schedule.Schedule;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;

import java.util.Optional;
import java.util.function.Supplier;

@SuppressWarnings({"unused"})
public class ModRegistry {

    public static void init(){}

    public static final DeferredRegister<GlobalLootModifierSerializer<?>> GLM = DeferredRegister.create(ForgeRegistries.Keys.LOOT_MODIFIER_SERIALIZERS, HauntedHarvest.MOD_ID);

    public static final RegistryObject<GlobalLootModifierSerializer<?>> ROTTEN_APPLE_GLM =
            GLM.register("add_item", AddItemModifier.Serializer::new);

    public static final Supplier<Activity> EAT_CANDY = regActivity("eat_candy");
    public static final Supplier<Activity> TRICK_OR_TREAT = regActivity("trick_or_treat");

    //do not use
    public static final Supplier<Schedule> HALLOWEEN_VILLAGER_BABY_SCHEDULE =
            RegHelper.registerSchedule(HauntedHarvest.res("villager_baby_halloween"), Schedule::new);

    private static Supplier<Activity> regActivity(String name) {
        return RegHelper.registerActivity(HauntedHarvest.res(name), () -> new Activity(name));
    }

    public static final Supplier<MemoryModuleType<GlobalPos>> PUMPKIN_POS =
            RegHelper.registerMemoryModule(HauntedHarvest.res("pumpkin_pos"), () ->
                    new MemoryModuleType<>(Optional.of(GlobalPos.CODEC)));

    public static final Supplier<MemoryModuleType<GlobalPos>> NEAREST_PUMPKIN =
            RegHelper.registerMemoryModule(HauntedHarvest.res("nearest_pumpkin"), () ->
                    new MemoryModuleType<>(Optional.empty()));


    public static final Supplier<SensorType<PumpkinPoiSensor>> PUMPKIN_POI_SENSOR =
            RegHelper.registerSensor(HauntedHarvest.res("pumpkin_poi"), () ->
                    new SensorType<>(PumpkinPoiSensor::new));


    public static final Supplier<EntityType<SplatteredEggEntity>> SPLATTERED_EGG_ENTITY = RegHelper.registerEntityType(
            HauntedHarvest.res("splattered_egg"), () -> (
                    EntityType.Builder.<SplatteredEggEntity>of(SplatteredEggEntity::new, MobCategory.MISC)
                            .sized(0.5F, 0.5F).clientTrackingRange(10).updateInterval(Integer.MAX_VALUE))
                    .build("splattered_egg"));


    public static final Supplier<SimpleParticleType> SPOOKED_PARTICLE = RegHelper.registerParticle(
            HauntedHarvest.res("spooked"));


    public static final FoodProperties ROTTEN_APPLE_FOOD = (new FoodProperties.Builder()).nutrition(2).saturationMod(0.1F)
            .effect(new MobEffectInstance(MobEffects.HUNGER, 600, 0), 0.2F).meat().build();

    public static final FoodProperties DEATH_APPLE_FOOD = (new FoodProperties.Builder()).nutrition(4).saturationMod(0.3F)
            .effect(new MobEffectInstance(MobEffects.HUNGER, 2400, 0), 1)
            .effect(new MobEffectInstance(MobEffects.CONFUSION, 200, 0), 1.0F)
            .effect(new MobEffectInstance(MobEffects.WITHER, 230, 1), 1.0F)
            .effect(new MobEffectInstance(MobEffects.POISON, 500, 0), 1.0F)
            .effect(new MobEffectInstance(MobEffects.WEAKNESS, 6000, 0), 1.0F)
            .meat().alwaysEat().build();

    public static final Supplier<Item> ROTTEN_APPLE = RegHelper.registerItem(
            HauntedHarvest.res("rotten_apple"), () ->
                    new Item(new Item.Properties().tab(CreativeModeTab.TAB_FOOD).food(ROTTEN_APPLE_FOOD)));

    public static final Supplier<Item> DEATH_APPLE = RegHelper.registerItem(
            HauntedHarvest.res("grim_apple"), () ->
                    new GrimAppleItem(new Item.Properties().tab(CreativeModeTab.TAB_FOOD).rarity(Rarity.RARE).food(DEATH_APPLE_FOOD)));
}
