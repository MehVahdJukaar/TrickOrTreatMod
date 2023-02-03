package net.mehvahdjukaar.hauntedharvest.configs;

import net.mehvahdjukaar.hauntedharvest.HauntedHarvest;
import net.mehvahdjukaar.hauntedharvest.blocks.ModCarvedPumpkinBlock;
import net.mehvahdjukaar.hauntedharvest.integration.SeasonModCompat;
import net.mehvahdjukaar.moonlight.api.platform.configs.ConfigBuilder;
import net.mehvahdjukaar.moonlight.api.platform.configs.ConfigSpec;
import net.mehvahdjukaar.moonlight.api.platform.configs.ConfigType;

import java.util.function.Supplier;

public class CommonConfigs {

    public static void init() {
    }

    public static final ConfigSpec SPEC;


    static {
        ConfigBuilder builder = ConfigBuilder.create(HauntedHarvest.res("common"), ConfigType.COMMON);


        builder.push("pumpkin_carving");
        CUSTOM_CARVINGS = builder.comment("Allows custom carved pumpkins to be placed by villagers and appear in abandoned farm structure")
                .define("custom_carvings",true);
        PUMPKIN_CARVE_MODE = builder.comment("Pumpkin carving mode")
                .define("pumpkin_carve_mode", ModCarvedPumpkinBlock.CarveMode.BOTH);
        JACK_O_LANTERN_CARVE_MODE = builder.comment("Jack o Lantern carving mode")
                .define("jack_o_lantern_carve_mode", ModCarvedPumpkinBlock.CarveMode.NONE);

        builder.pop();


        builder.push("halloween_season");
        START_MONTH = builder.comment("Day from which villagers will start placing pumpkins & trick or treating")
                .define("start_month", 10, 1, 12);
        START_DAY = builder.comment("Day from which villagers will start placing pumpkins & trick or treating")
                .define("start_day", 20, 1, 31);
        END_MONTH = builder.comment("Day from which villagers will start removing placed pumpkins")
                .define("end_month", 11, 1, 12);
        END_DAY = builder.comment("Day from which villagers will start removing placed pumpkins")
                .define("end_day", 10, 1, 31);
        builder.pop();

        builder.push("mob_pumpkins_season");
        WEAR_CHANCE = builder.comment("Chance for a mob to wear a pumpkin. All this does not affect vanilla halloween behavior")
                .define("wear_chance", 0.25, 0,1f);
        P_START_MONTH = builder.comment("Day from which zombies and skeletons can wear pumpkins")
                .define("start_month", 10, 1, 12);
        P_START_DAY = builder.comment("Day from which zombies and skeletons can wear pumpkins")
                .define("start_day", 30, 1, 31);
        P_END_MONTH = builder.comment("Day from which zombies and skeletons can wear pumpkins")
                .define("end_month", 10, 1, 12);
        P_END_DAY = builder.comment("Day from which zombies and skeletons can wear pumpkins")
                .define("end_day", 31, 1, 31);
        builder.pop();

        builder.push("trick_or_treating_time").comment("Nota that all these configs will not take effect until the game is reloaded");
        START_TIME = builder.comment("Time of day at which baby villagers will start trick-or-treating")
                .define("start_time", 12000, 0, 24000);
        END_TIME = builder.comment("Time of day at which baby villagers will stop trick-or-treating. Note that this will only properly work if it's at night since baby villagers can only trick or treat during their sleep schedule which is from 12000 to 0")
                .define("end_time", 0, 0, 24000);

        builder.pop();

        builder.push("season_mod_compat");

        SEASONS_MOD_COMPAT = builder.comment("Enables compatibility with Serene Seasons (Forge) or Fabric Seasons (Fabric). Only takes effect if the mod is installed. Will make halloween season only active during certain seasons. Note that this will override previous time window settings")
                .define("enabled", HauntedHarvest.SEASON_MOD_INSTALLED);
        if (HauntedHarvest.SEASON_MOD_INSTALLED) {
            SeasonModCompat.addConfig(builder);
        }
        builder.pop();


        builder.onChange(()->HauntedHarvest.getSeasonManager().refresh());

        SPEC = builder.buildAndRegister();
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


    public static boolean customCarvings() {
        return CUSTOM_CARVINGS.get() && RegistryConfigs.CARVED_PUMPKINS_ENABLED.get();
    }
}
