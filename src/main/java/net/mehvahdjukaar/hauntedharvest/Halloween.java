package net.mehvahdjukaar.hauntedharvest;

import net.mehvahdjukaar.hauntedharvest.init.ClientSetup;
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
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
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

    public static final boolean IS_HALLOWEEN_TIME;
    static{
        int month = Calendar.getInstance().get(Calendar.MONTH);
        int day = Calendar.getInstance().get(Calendar.DATE);
        IS_HALLOWEEN_TIME = (month == Calendar.OCTOBER && day > 20) || (month == Calendar.NOVEMBER && day < 10);
    }

    public Halloween() {
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        bus.addListener(Halloween::init);
        ModRegistry.init(bus);
        DistExecutor.runWhenOn(Dist.CLIENT, () -> () -> bus.addListener(ClientSetup::init));

        MinecraftForge.EVENT_BUS.addListener(Halloween::onTagLoad);


    }


    public static boolean isNight(Level level) {
        return level.getDayTime() % 24000 > 12000;
    }

    public static final Tags.IOptionalNamedTag<Item> SWEETS = ItemTags.createOptional(res("sweets"));
    public static final Tags.IOptionalNamedTag<Block> PUMPKIN_SUPPORT = BlockTags.createOptional(res("pumpkin_support"));
    public static final Set<Item> EATABLE = new HashSet<>();

    public static final Predicate<LivingEntity> IS_TRICK_OR_TREATING = e -> e.isBaby() && e.getMainHandItem().is(Items.BUNDLE);

    public static void init(final FMLCommonSetupEvent event) {
    }


    @SubscribeEvent
    public static void onTagLoad(TagsUpdatedEvent event) {
        EATABLE.clear();
        Set<Item> temp = new HashSet<>();
        temp.addAll(SWEETS.getValues());
        temp.add(ModRegistry.DEATH_APPLE.get());
        temp.add(ModRegistry.ROTTEN_APPLE.get());
        for (Item i : temp) {
            if (i != Items.AIR) {
                EATABLE.add(i);
            }
        }
    }

    /*
    //@SubscribeEvent
    public static void onLootLoad(LootTableLoadEvent event) {
        if(event.getName().toString().equals("minecraft:blocks/oak_leaves") || event.getName().toString().equals("minecraft:blocks/dark_oak_leaves")){
            LootPool pool = LootPool.lootPool().add(
                            LootTableReference.lootTableReference(Halloween.res("inject/oak_leaves")))
                    .name("rotten_apple").build();
            event.getTable().addPool(pool);
        }
    }*/

    private static Field SENSORS = null;

    //this might be bad
    public static void addSensorToVillagers(Brain<Villager> brain, SensorType<? extends Sensor<Villager>> newSensor){
        if(SENSORS == null) SENSORS = ObfuscationReflectionHelper.findField(Brain.class, "f_21844_");

        SENSORS.setAccessible(true);
        try {
            var sensors = (Map<SensorType<? extends Sensor<Villager>>, Sensor<Villager>>) SENSORS.get(brain);

            var sensorInstance = newSensor.create();
            sensors.put(newSensor, sensorInstance);

            var memories = brain.getMemories();

            for(MemoryModuleType<?> memoryModuleType : sensorInstance.requires()) {
                memories.put(memoryModuleType, Optional.empty());
            }
        }
        catch (Exception e){
            LOGGER.warn("failed to register pumpkin sensor type for villagers: "+e);
        }




    }

}
