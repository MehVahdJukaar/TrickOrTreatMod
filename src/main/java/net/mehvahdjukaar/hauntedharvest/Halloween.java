package net.mehvahdjukaar.hauntedharvest;

import net.mehvahdjukaar.hauntedharvest.init.ClientSetup;
import net.mehvahdjukaar.hauntedharvest.init.ModRegistry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
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
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashSet;
import java.util.Set;
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
        for(Item i : temp){
            if(i!=Items.AIR){
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


}
