package net.mehvahdjukaar.hauntedharvest.init;

import net.mehvahdjukaar.hauntedharvest.Halloween;
import net.mehvahdjukaar.hauntedharvest.ai.PumpkinPoiSensor;
import net.mehvahdjukaar.hauntedharvest.entity.SplatteredEggEntity;
import net.mehvahdjukaar.hauntedharvest.items.GrimAppleItem;
import net.mehvahdjukaar.hauntedharvest.loot.AddItemModifier;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.sounds.SoundEvent;
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
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraftforge.common.loot.GlobalLootModifierSerializer;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.Optional;

@SuppressWarnings({"unused"})
@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModRegistry {


    public static final DeferredRegister<GlobalLootModifierSerializer<?>> GLM = DeferredRegister.create(ForgeRegistries.LOOT_MODIFIER_SERIALIZERS, Halloween.MOD_ID);
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, Halloween.MOD_ID);
    public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(ForgeRegistries.ENTITIES, Halloween.MOD_ID);
    public static final DeferredRegister<ParticleType<?>> PARTICLES = DeferredRegister.create(ForgeRegistries.PARTICLE_TYPES, Halloween.MOD_ID);
    public static final DeferredRegister<SoundEvent> SOUNDS = DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, Halloween.MOD_ID);
    public static final DeferredRegister<RecipeSerializer<?>> RECIPES = DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, Halloween.MOD_ID);
    public static final DeferredRegister<Activity> ACTIVITIES = DeferredRegister.create(ForgeRegistries.ACTIVITIES, Halloween.MOD_ID);
    public static final DeferredRegister<Schedule> SCHEDULES = DeferredRegister.create(ForgeRegistries.SCHEDULES, Halloween.MOD_ID);
    public static final DeferredRegister<MemoryModuleType<?>> MEMORY_MODULE_TYPES = DeferredRegister.create(ForgeRegistries.MEMORY_MODULE_TYPES, Halloween.MOD_ID);
    public static final DeferredRegister<SensorType<?>> POI_SENSORS = DeferredRegister.create(ForgeRegistries.SENSOR_TYPES, Halloween.MOD_ID);


    public static void init(IEventBus bus) {
        ITEMS.register(bus);
        ENTITIES.register(bus);
        PARTICLES.register(bus);
        SOUNDS.register(bus);
        RECIPES.register(bus);
        ACTIVITIES.register(bus);
        SCHEDULES.register(bus);
        GLM.register(bus);
        MEMORY_MODULE_TYPES.register(bus);
        POI_SENSORS.register(bus);
    }

    public static final RegistryObject<GlobalLootModifierSerializer<?>> ROTTEN_APPLE_GLM =
            GLM.register("add_item", AddItemModifier.Serializer::new);

    public static final RegistryObject<Activity> EAT_CANDY = regActivity("eat_candy");
    public static final RegistryObject<Activity> TRICK_OR_TREAT = regActivity("trick_or_treat");

    //do not use
    public static final RegistryObject<Schedule> HALLOWEEN_VILLAGER_BABY_SCHEDULE = SCHEDULES.register("villager_baby_halloween", Schedule::new);

    private static RegistryObject<Activity> regActivity(String name) {
        return ACTIVITIES.register(name, () -> new Activity(name));
    }

    public static final RegistryObject<MemoryModuleType<GlobalPos>> PUMPKIN_POS =
            MEMORY_MODULE_TYPES.register("pumpkin_pos", ()->new MemoryModuleType<>(Optional.of(GlobalPos.CODEC)));

    public static final RegistryObject<MemoryModuleType<GlobalPos>> NEAREST_PUMPKIN =
            MEMORY_MODULE_TYPES.register("nearest_pumpkin", ()->new MemoryModuleType<>(Optional.empty()));


    public static final RegistryObject<SensorType<PumpkinPoiSensor>> PUMPKIN_POI_SENSOR =
            POI_SENSORS.register("pumpkin_poi", ()->new SensorType<>(PumpkinPoiSensor::new));

    public static final String SPLATTERED_EGG_NAME = "splattered_egg";
    public static final RegistryObject<EntityType<SplatteredEggEntity>> SPLATTERED_EGG_ENTITY = ENTITIES.register(SPLATTERED_EGG_NAME, () -> (
            EntityType.Builder.<SplatteredEggEntity>of(SplatteredEggEntity::new, MobCategory.MISC)
                    .sized(0.5F, 0.5F).clientTrackingRange(10).updateInterval(Integer.MAX_VALUE))
            .build(SPLATTERED_EGG_NAME));


    public static final RegistryObject<SimpleParticleType> SPOOKED_PARTICLE = PARTICLES.register("spooked", () -> new SimpleParticleType(true));


    public static final FoodProperties ROTTEN_APPLE_FOOD = (new FoodProperties.Builder()).nutrition(2).saturationMod(0.1F)
            .effect(new MobEffectInstance(MobEffects.HUNGER, 600, 0), 0.2F).meat().build();

    public static final FoodProperties DEATH_APPLE_FOOD = (new FoodProperties.Builder()).nutrition(4).saturationMod(0.3F)
            .effect(() -> new MobEffectInstance(MobEffects.HUNGER, 2400, 0), 1)
            .effect(() -> new MobEffectInstance(MobEffects.CONFUSION, 200, 0), 1.0F)
            .effect(() -> new MobEffectInstance(MobEffects.WITHER, 230, 1), 1.0F)
            .effect(() -> new MobEffectInstance(MobEffects.POISON, 500, 0), 1.0F)
            .effect(() -> new MobEffectInstance(MobEffects.WEAKNESS, 6000, 0), 1.0F)
            .meat().alwaysEat().build();

    public static final RegistryObject<Item> ROTTEN_APPLE = ITEMS.register("rotten_apple", () ->
            new Item(new Item.Properties().tab(CreativeModeTab.TAB_FOOD).food(ROTTEN_APPLE_FOOD)));

    public static final RegistryObject<Item> DEATH_APPLE = ITEMS.register("grim_apple", () ->
            new GrimAppleItem(new Item.Properties().tab(CreativeModeTab.TAB_FOOD).rarity(Rarity.RARE).food(DEATH_APPLE_FOOD)));
}
