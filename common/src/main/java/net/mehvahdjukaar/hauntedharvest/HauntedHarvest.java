package net.mehvahdjukaar.hauntedharvest;

import net.mehvahdjukaar.hauntedharvest.ai.HalloweenVillagerAI;
import net.mehvahdjukaar.hauntedharvest.integration.FDCompat;
import net.mehvahdjukaar.hauntedharvest.integration.SeasonModCompat;
import net.mehvahdjukaar.hauntedharvest.network.NetworkHandler;
import net.mehvahdjukaar.hauntedharvest.configs.ModConfigs;
import net.mehvahdjukaar.hauntedharvest.reg.ModRegistry;
import net.mehvahdjukaar.hauntedharvest.reg.ModTags;
import net.mehvahdjukaar.moonlight.api.platform.PlatformHelper;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.ComposterBlock;
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
public class HauntedHarvest {

    public static final String MOD_ID = "hauntedharvest";

    public static ResourceLocation res(String name) {
        return new ResourceLocation(MOD_ID, name);
    }

    public static final Logger LOGGER = LogManager.getLogger();

    public static final boolean SEASON_MOD_INSTALLED = PlatformHelper.isModLoaded(PlatformHelper.getPlatform().isForge() ? "sereneseasons" : "seasons");
    public static final boolean SUPP_INSTALLED = PlatformHelper.isModLoaded("supplementaries");
    public static final boolean FD_INSTALLED = PlatformHelper.isModLoaded("farmersdelight");

    public static void commonInit() {
        ModConfigs.earlyLoad();

        ModRegistry.init();
        if (FD_INSTALLED) FDCompat.init();
        NetworkHandler.registerMessages();
    }

    //TODO: add witches to villages using structure modifiers

    //TODO: give candy to players
    //TODO: fix when inventory is full

    //needs to be fired after configs are loaded
    public static void commonSetup() {
        HalloweenVillagerAI.setup();
        ComposterBlock.COMPOSTABLES.put(ModRegistry.MOD_CARVED_PUMPKIN.get().asItem(), 0.65F);
        ComposterBlock.COMPOSTABLES.put(ModRegistry.CORN_SEEDS.get().asItem(), 0.3F);
        ComposterBlock.COMPOSTABLES.put(ModRegistry.COB_ITEM.get().asItem(), 0.5F);
    }

    public static final Predicate<LivingEntity> IS_TRICK_OR_TREATING = e -> e.isBaby() && e.getMainHandItem().is(Items.BUNDLE);

   public static final Set<Item> EATABLE = new HashSet<>();


    //refresh configs and tag stuff
    public static void onTagLoad() {
        EATABLE.clear();
        Set<Item> temp = new HashSet<>();
        for (var p : Registry.ITEM.getTagOrEmpty(ModTags.SWEETS)) {
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


    public static boolean isPlayerOnCooldown(LivingEntity self) {
        return false;
    }




    public static int TRICK_OR_TREAT_START;
    public static int TRICK_OR_TREAT_END;


    public static boolean IS_HALLOWEEN_REAL_TIME;

    public static boolean USES_SEASON_MOD;

    public static void onConfigReload() {

        //refresh date after configs are loaded
        int startM = ModConfigs.START_MONTH.get() - 1;
        int startD = ModConfigs.START_DAY.get();

        int endM = ModConfigs.END_MONTH.get() - 1;
        int endD = ModConfigs.END_DAY.get();

        boolean inv = startM > endM;

        //pain
        Date start = new Date(0, startM, startD);
        Date end = new Date((inv ? 1 : 0), endM, endD);

        Date today = new Date(0, Calendar.getInstance().get(Calendar.MONTH), Calendar.getInstance().get(Calendar.DATE));
        if (today.before(start) && inv) today = new Date(1, today.getMonth(), today.getDate());
        //TODO: rewrite properly
        //if seasonal use pumpkin placement time window
        IS_HALLOWEEN_REAL_TIME = today.after(start) && today.before(end);

        TRICK_OR_TREAT_START = ModConfigs.START_TIME.get();
        TRICK_OR_TREAT_END = ModConfigs.END_TIME.get();

        USES_SEASON_MOD = SEASON_MOD_INSTALLED && ModConfigs.SEASONS_MOD_COMPAT.get();

        if (USES_SEASON_MOD) {
            SeasonModCompat.refresh();
        }

    }


    public static boolean isHalloweenSeason(Level level) {
        if (USES_SEASON_MOD) return SeasonModCompat.isAutumn(level);
        return IS_HALLOWEEN_REAL_TIME;
    }

    public static boolean isTrickOrTreatTime(Level level) {
        return isHalloweenSeason(level) && isBetween(TRICK_OR_TREAT_START, TRICK_OR_TREAT_END, level.getDayTime() % 24000);
    }

    //TODO: maybe cache some of this
    private static boolean isBetween(float start, float end, float mid) {
        if (start < end) return mid >= start && mid <= end;
        else return mid <= end || mid >= start;
    }

}
