package net.mehvahdjukaar.hauntedharvest;

import net.mehvahdjukaar.hauntedharvest.compat.SereneSeasonsCompat;
import net.mehvahdjukaar.hauntedharvest.init.ClientSetup;
import net.mehvahdjukaar.hauntedharvest.init.Configs;
import net.mehvahdjukaar.hauntedharvest.init.ModRegistry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.sensing.Sensor;
import net.minecraft.world.entity.ai.sensing.SensorType;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.Tags;
import net.minecraftforge.event.TagsUpdatedEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.util.ObfuscationReflectionHelper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.Field;
import java.util.*;
import java.util.function.Predicate;

/**
 * Author: MehVahdJukaar
 */
@Mod(Halloween.MOD_ID)
public class Halloween {
    public static final String MOD_ID = "hauntedharvest";

    public static ResourceLocation res(String name) {
        return new ResourceLocation(MOD_ID, name);
    }

    private static final Logger LOGGER = LogManager.getLogger();

    public static boolean IS_HALLOWEEN_TIME;
    public static boolean IS_PUMPKIN_PLACEMENT_TIME;

    private static int TRICK_OR_TREAT_START;
    private static int TRICK_OR_TREAT_END;

    public static boolean SERENE_SEASONS = ModList.get().isLoaded("sereneseasons");

    public Halloween() {
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        //bus.addListener(Halloween::init);
        ModRegistry.init(bus);
        DistExecutor.runWhenOn(Dist.CLIENT, () -> () -> bus.addListener(ClientSetup::init));

        MinecraftForge.EVENT_BUS.addListener(Halloween::onTagLoad);

        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Configs.buildConfig());
    }

    //needs to be fired after configs are loaded
    public static void init() {
        if (SERENE_SEASONS) {
            SereneSeasonsCompat.init();
        }
        try {
            int startM = Configs.START_MONTH.get() - 1;
            int startD = Configs.START_DAY.get();

            int endM = Configs.END_MONTH.get() - 1;
            int endD = Configs.END_DAY.get();

            boolean inv = startM > endM;

            //pain
            Date start = new Date(0, startM, startD);
            Date end = new Date((inv ? 1 : 0), endM, endD);

            Date today = new Date(0, Calendar.getInstance().get(Calendar.MONTH), Calendar.getInstance().get(Calendar.DATE));

            IS_PUMPKIN_PLACEMENT_TIME = !today.before(start) && !today.after(end);
        } catch (Exception e) {
            LOGGER.warn("Failed to set event date. Defaulting to always on: " + e);
            IS_PUMPKIN_PLACEMENT_TIME = true;
        }
        //if seasonal use pumpkin placement time window
        IS_HALLOWEEN_TIME = !Configs.SEASONAL.get() || IS_PUMPKIN_PLACEMENT_TIME;
        TRICK_OR_TREAT_START = Configs.START_TIME.get();
        TRICK_OR_TREAT_END = Configs.END_TIME.get();
    }

    public static boolean isTrickOrTreatTime(Level level) {
        if (SERENE_SEASONS) return SereneSeasonsCompat.isAutumn(level);

        return IS_HALLOWEEN_TIME && isBetween(TRICK_OR_TREAT_START, TRICK_OR_TREAT_END, level.getDayTime() % 24000);
    }

    //TODO: maybe cache some of this
    private static boolean isBetween(float start, float end, float mid) {
        if (start < end) return mid >= start && mid <= end;
        else return mid <= end || mid >= start;
    }

    public static final Predicate<LivingEntity> IS_TRICK_OR_TREATING = e -> e.isBaby() && e.getMainHandItem().is(Items.BUNDLE);


    public static final Tags.IOptionalNamedTag<Item> SWEETS = ItemTags.createOptional(res("sweets"));
    public static final Tags.IOptionalNamedTag<Block> PUMPKIN_SUPPORT = BlockTags.createOptional(res("pumpkin_support"));
    public static final Set<Item> EATABLE = new HashSet<>();


    @SubscribeEvent
    public static void onTagLoad(TagsUpdatedEvent event) {
        EATABLE.clear();
        Set<Item> temp = new HashSet<>(SWEETS.getValues());
        temp.add(ModRegistry.DEATH_APPLE.get());
        temp.add(ModRegistry.ROTTEN_APPLE.get());
        for (Item i : temp) {
            if (i != Items.AIR) {
                EATABLE.add(i);
            }
        }

        //moved here so it's after configs
        init();
    }

    private static Field SENSORS = null;

    //this might be bad
    public static void addSensorToVillagers(Brain<Villager> brain, SensorType<? extends Sensor<Villager>> newSensor) {
        if (SENSORS == null) SENSORS = ObfuscationReflectionHelper.findField(Brain.class, "f_21844_");

        SENSORS.setAccessible(true);
        try {
            var sensors = (Map<SensorType<? extends Sensor<Villager>>, Sensor<Villager>>) SENSORS.get(brain);

            var sensorInstance = newSensor.create();
            sensors.put(newSensor, sensorInstance);

            var memories = brain.getMemories();

            for (MemoryModuleType<?> memoryModuleType : sensorInstance.requires()) {
                memories.put(memoryModuleType, Optional.empty());
            }
        } catch (Exception e) {
            LOGGER.warn("failed to register pumpkin sensor type for villagers: " + e);
        }
    }

    public static boolean isPlayerOnCooldown(LivingEntity self) {
        return false;
    }

    //TODO: give candy to players
    //TODO: fix when inventory is full
}
