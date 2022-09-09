package net.mehvahdjukaar.hauntedharvest;

import net.mehvahdjukaar.hauntedharvest.ai.HalloweenVillagerAI;
import net.mehvahdjukaar.hauntedharvest.compat.SereneSeasonsCompat;
import net.mehvahdjukaar.hauntedharvest.init.ClientSetup;
import net.mehvahdjukaar.hauntedharvest.init.Configs;
import net.mehvahdjukaar.hauntedharvest.init.ModRegistry;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TagsUpdatedEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Calendar;
import java.util.Date;
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

    public static final Logger LOGGER = LogManager.getLogger();


    public static boolean IS_HALLOWEEN_REAL_TIME;

    public static int TRICK_OR_TREAT_START;
    public static int TRICK_OR_TREAT_END;

    public static boolean SERENE_SEASONS;

    public Halloween() {
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        bus.addListener(Halloween::init);
        bus.addListener(Halloween::reloadConfigsEvent);

        ModRegistry.init(bus);
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> bus.addListener(ClientSetup::init));

        MinecraftForge.EVENT_BUS.addListener(Halloween::onTagLoad);

        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Configs.buildConfig());

    }

    //needs to be fired after configs are loaded
    public static void init(final FMLCommonSetupEvent event) {
        event.enqueueWork(HalloweenVillagerAI::init);
    }

    public static final Predicate<LivingEntity> IS_TRICK_OR_TREATING = e -> e.isBaby() && e.getMainHandItem().is(Items.BUNDLE);

    public static final TagKey<Item> SWEETS = ItemTags.create(res("sweets"));
    public static final TagKey<Block> PUMPKIN_SUPPORT = BlockTags.create(res("pumpkin_support"));
    public static final Set<Item> EATABLE = new HashSet<>();


    //refresh configs and tag stuff
    public static void onTagLoad(TagsUpdatedEvent event) {
        EATABLE.clear();
        Set<Item> temp = new HashSet<>();
        for (var p : Registry.ITEM.getTagOrEmpty(SWEETS)) {
            temp.add(p.value());
        }
        temp.add(ModRegistry.DEATH_APPLE.get());
        temp.add(ModRegistry.ROTTEN_APPLE.get());
        for (Item i : temp) {
            if (i != Items.AIR) {
                EATABLE.add(i);
            }
        }
    }

    public static void reloadConfigsEvent(ModConfigEvent event) {
        if (event.getConfig().getSpec() == Configs.SERVER_SPEC) {
            //refresh date after configs are loaded
            int startM = Configs.START_MONTH.get() - 1;
            int startD = Configs.START_DAY.get();

            int endM = Configs.END_MONTH.get() - 1;
            int endD = Configs.END_DAY.get();

            boolean inv = startM > endM;

            //pain
            Date start = new Date(0, startM, startD);
            Date end = new Date((inv ? 1 : 0), endM, endD);

            Date today = new Date(0, Calendar.getInstance().get(Calendar.MONTH), Calendar.getInstance().get(Calendar.DATE));

            //if seasonal use pumpkin placement time window
            IS_HALLOWEEN_REAL_TIME = today.after(start) && today.before(end);
            TRICK_OR_TREAT_START = Configs.START_TIME.get();
            TRICK_OR_TREAT_END = Configs.END_TIME.get();
            SERENE_SEASONS = ModList.get().isLoaded("sereneseasons") && Configs.SERENE_SEASONS_COMPAT.get();

            if (SERENE_SEASONS) {
                SereneSeasonsCompat.refresh();
            }
        }
    }

    //TODO: maybe cache some of this
    private static boolean isBetween(float start, float end, float mid) {
        if (start < end) return mid >= start && mid <= end;
        else return mid <= end || mid >= start;
    }

    public static boolean isPlayerOnCooldown(LivingEntity self) {
        return false;
    }

    public static boolean isHalloweenSeason(Level level) {
        if (SERENE_SEASONS) return SereneSeasonsCompat.isAutumn(level);
        return IS_HALLOWEEN_REAL_TIME;
    }

    public static boolean isTrickOrTreatTime(Level level) {
        return isHalloweenSeason(level) && isBetween(TRICK_OR_TREAT_START, TRICK_OR_TREAT_END, level.getDayTime() % 24000);
    }

    //TODO: add witches to villages using structure modifiers

    //TODO: give candy to players
    //TODO: fix when inventory is full
}
