package net.mehvahdjukaar.hauntedharvest.configs;

import net.mehvahdjukaar.hauntedharvest.HauntedHarvest;
import net.mehvahdjukaar.hauntedharvest.blocks.ModCarvedPumpkinBlock;
import net.mehvahdjukaar.hauntedharvest.integration.CompatHandler;
import net.mehvahdjukaar.hauntedharvest.integration.SeasonModCompat;
import net.mehvahdjukaar.hauntedharvest.reg.ModRegistry;
import net.mehvahdjukaar.moonlight.api.platform.configs.ConfigBuilder;
import net.mehvahdjukaar.moonlight.api.platform.configs.ConfigType;
import net.mehvahdjukaar.moonlight.api.platform.configs.ModConfigHolder;

import java.util.function.Supplier;

public class CommonConfigs {

    public static void init() {
    }

    public static final ModConfigHolder SPEC;

    static {
        ConfigBuilder builder = ConfigBuilder.create(HauntedHarvest.res("common"), ConfigType.COMMON_SYNCED);

        //vanilla pumpkins, not ours: ours draw through the carving item renderer and show up blank without carving data
        builder.icon("minecraft:carved_pumpkin").push("pumpkin_carving");
        CUSTOM_CARVINGS = builder.comment("Allows custom carved pumpkins to be placed by villagers and appear in abandoned farm structure")
                .define("custom_carvings", true);
        PUMPKIN_CARVE_MODE = builder.comment("Pumpkin carving mode")
                .define("pumpkin_carve_mode", ModCarvedPumpkinBlock.CarveMode.BOTH);
        JACK_O_LANTERN_CARVE_MODE = builder.comment("Jack o Lantern carving mode")
                .define("jack_o_lantern_carve_mode", ModCarvedPumpkinBlock.CarveMode.NONE);
        builder.pop();

        builder.icon("minecraft:jack_o_lantern").push("halloween_season");
        START_MONTH = builder.comment("Month from which villagers will start placing pumpkins & trick or treating")
                .define("start_month", 10, 1, 12);
        START_DAY = builder.comment("Day from which villagers will start placing pumpkins & trick or treating")
                .define("start_day", 20, 1, 31);
        END_MONTH = builder.comment("Month from which villagers will start removing placed pumpkins")
                .define("end_month", 11, 1, 12);
        END_DAY = builder.comment("Day from which villagers will start removing placed pumpkins")
                .define("end_day", 10, 1, 31);
        builder.pop();

        builder.icon("minecraft:zombie_head").push("mob_pumpkins_season");
        WEAR_CHANCE = builder.comment("Chance for a mob to wear a pumpkin. All this does not affect vanilla halloween behavior")
                .definePercentage("wear_chance", 0.25);
        P_START_MONTH = builder.comment("Day from which zombies and skeletons can wear pumpkins")
                .define("start_month", 10, 1, 12);
        P_START_DAY = builder.comment("Day from which zombies and skeletons can wear pumpkins")
                .define("start_day", 30, 1, 31);
        P_END_MONTH = builder.comment("Day from which zombies and skeletons can wear pumpkins")
                .define("end_month", 10, 1, 12);
        P_END_DAY = builder.comment("Day from which zombies and skeletons can wear pumpkins")
                .define("end_day", 31, 1, 31);
        builder.pop();

        //these get baked into the villagers brain schedule on setup
        builder.icon("minecraft:clock").push("trick_or_treating_time");
        START_TIME = builder.gameRestart()
                .comment("Time of day at which baby villagers will start trick-or-treating")
                .define("start_time", 12000, 0, 24000);
        END_TIME = builder.gameRestart()
                .comment("Time of day at which baby villagers will stop trick-or-treating. Note that this will only properly work if it's at night since baby villagers can only trick or treat during their sleep schedule which is from 12000 to 0")
                .define("end_time", 0, 0, 24000);
        builder.pop();

        //the category name isn't an item, so the gate row can't infer one
        builder.icon("minecraft:oak_leaves").push("season_mod_compat");
        builder.comment("Enables compatibility with Serene Seasons (Forge) or Fabric Seasons (Fabric). Only takes effect if the mod is installed. Will make halloween season only active during certain seasons. Note that this will override previous time window settings");
        SEASONS_MOD_COMPAT = builder.mainFeature(CompatHandler.SEASON_MOD_INSTALLED);
        if (CompatHandler.SEASON_MOD_INSTALLED) {
            SeasonModCompat.addConfig(builder);
        }
        builder.pop();

        builder.push("general");
        CREATIVE_TAB = builder.gameRestart().comment("Enable Creative Tab").define("creative_tab", false);
        builder.pop();

        builder.icon(ModRegistry.CANDY_CORN_NAME).push("features");

        builder.push("paper_bag");
        PAPER_BAG = builder.gameRestart().mainFeature();
        PAPER_BAG_NAME_TAG = builder.comment("Wearing a paper bag will hide the player's name tag")
                .define("hide_name_tag", true);
        PAPER_BAG_ENDERMAN = builder.comment("Endermen will not attack players wearing a paper bag")
                .define("hide_from_enderman", true);
        builder.pop();

        CORN_ENABLED = builder.gameRestart().feature(ModRegistry.CORN_NAME);
        GRIM_APPLE = builder.gameRestart().feature(ModRegistry.GRIM_APPLE_NAME);
        POPCORN_ENABLED = builder.gameRestart().feature(ModRegistry.POPCORN_NAME);
        CARVED_PUMPKINS_ENABLED = builder.gameRestart().feature(ModRegistry.CARVED_PUMPKIN_NAME);
        //entity only, so there's no item of that name to infer an icon from
        SPLATTERED_EGG_ENABLED = builder.gameRestart().icon("minecraft:egg").feature(ModRegistry.SPLATTERED_EGG_NAME);
        CANDY_CORN_ENABLED = builder.gameRestart().feature(ModRegistry.CANDY_CORN_NAME);
        builder.pop();

        builder.onChange(() -> HauntedHarvest.getSeasonManager().refresh());

        SPEC = builder.build();
        SPEC.forceLoad();        //load early
    }

    public static final Supplier<Integer> START_DAY;
    public static final Supplier<Integer> START_MONTH;
    public static final Supplier<Integer> END_DAY;
    public static final Supplier<Integer> END_MONTH;

    public static final Supplier<Integer> START_TIME;
    public static final Supplier<Integer> END_TIME;

    public static final Supplier<Integer> P_END_DAY;
    public static final Supplier<Integer> P_END_MONTH;
    public static final Supplier<Integer> P_START_DAY;
    public static final Supplier<Integer> P_START_MONTH;
    public static final Supplier<Double> WEAR_CHANCE;

    public static final Supplier<Boolean> CUSTOM_CARVINGS;
    public static final Supplier<ModCarvedPumpkinBlock.CarveMode> PUMPKIN_CARVE_MODE;
    public static final Supplier<ModCarvedPumpkinBlock.CarveMode> JACK_O_LANTERN_CARVE_MODE;


    public static final Supplier<Boolean> SEASONS_MOD_COMPAT;


    public static final Supplier<Boolean> CORN_ENABLED;
    public static final Supplier<Boolean> GRIM_APPLE;
    public static final Supplier<Boolean> PAPER_BAG;
    public static final Supplier<Boolean> PAPER_BAG_NAME_TAG;
    public static final Supplier<Boolean> PAPER_BAG_ENDERMAN;
    public static final Supplier<Boolean> POPCORN_ENABLED;
    public static final Supplier<Boolean> SPLATTERED_EGG_ENABLED;
    public static final Supplier<Boolean> CARVED_PUMPKINS_ENABLED;
    public static final Supplier<Boolean> CANDY_CORN_ENABLED;


    public static final Supplier<Boolean> CREATIVE_TAB;


    public static boolean customCarvings() {
        return CUSTOM_CARVINGS.get() && CARVED_PUMPKINS_ENABLED.get();
    }

    //unknown keys count as enabled
    public static boolean isEnabled(String key) {
        return SPEC.isFeatureEnabled(key);
    }

}
