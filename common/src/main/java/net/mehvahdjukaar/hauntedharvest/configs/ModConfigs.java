package net.mehvahdjukaar.hauntedharvest.configs;

import net.mehvahdjukaar.hauntedharvest.HauntedHarvest;
import net.mehvahdjukaar.hauntedharvest.blocks.ModCarvedPumpkinBlock;
import net.mehvahdjukaar.hauntedharvest.integration.SeasonModCompat;
import net.mehvahdjukaar.moonlight.api.platform.configs.ConfigBuilder;
import net.mehvahdjukaar.moonlight.api.platform.configs.ConfigSpec;
import net.mehvahdjukaar.moonlight.api.platform.configs.ConfigType;

import java.util.function.Supplier;

public class ModConfigs {

    public static Supplier<Boolean> MOD_TAB;

    public static Supplier<Integer> START_DAY;
    public static Supplier<Integer> START_MONTH;

    public static Supplier<Integer> END_DAY;
    public static Supplier<Integer> END_MONTH;

    public static Supplier<Integer> START_TIME;
    public static Supplier<Integer> END_TIME;

    public static Supplier<ModCarvedPumpkinBlock.CarveMode> PUMPKIN_CARVE_MODE;
    public static Supplier<ModCarvedPumpkinBlock.CarveMode> JACK_O_LANTERN_CARVE_MODE;


    public static Supplier<Boolean> SEASONS_MOD_COMPAT;

    public static ConfigSpec SPEC;

    public static void earlyLoad() {
        ConfigBuilder builder = ConfigBuilder.create(HauntedHarvest.res("common"), ConfigType.COMMON);

        builder.push("general");
        MOD_TAB = builder.comment("Enable mod creative tab").define("creative_tab", false);
        builder.pop();

        builder.push("pumpkin_carving");
        PUMPKIN_CARVE_MODE = builder.comment("Pumpkin carving mode")
                .define("pumpkin_carve_mode", ModCarvedPumpkinBlock.CarveMode.BOTH);
        JACK_O_LANTERN_CARVE_MODE = builder.comment("Jack o Lantern carving mode")
                .define("jack_o_lantern_carve_mode", ModCarvedPumpkinBlock.CarveMode.NONE);

        builder.pop();

        builder.push("halloween_start");
        START_MONTH = builder.comment("Day from which villagers will start placing pumpkins & trick or treating")
                .define("month", 10, 1, 12);
        START_DAY = builder.comment("Day from which villagers will start placing pumpkins & trick or treating")
                .define("day", 20, 1, 31);
        builder.pop();
        builder.push("halloween_end");
        END_MONTH = builder.comment("Day from which villagers will start removing placed pumpkins")
                .define("month", 11, 1, 12);
        END_DAY = builder.comment("Day from which villagers will start removing placed pumpkins")
                .define("day", 10, 1, 31);
        builder.pop();

        builder.push("trick_or_treating").comment("Nota that all these configs will not take effect until the game is reloaded");
        START_TIME = builder.comment("Time of day at which baby villagers will start trick-or-treating")
                .define("start_time", 12000, 0, 24000);
        END_TIME = builder.comment("Time of day at which baby villagers will stop trick-or-treating. Note that this will only properly work if it's at night since baby villagers can only trick or treat during their sleep schedule which is from 12000 to 0")
                .define("end_time", 0, 0, 24000);

        builder.pop();

        builder.push("season_mod_compat");

        SEASONS_MOD_COMPAT = builder.comment("Enables compatibility with Serene Seasons (Forge) or Fabric Seasons (Fabric). Only takes effect if the mod is installed. Will make autumn season only active during certain seasons. Note that this will override previous time window settings")
                .define("enabled", HauntedHarvest.SEASON_MOD_INSTALLED);
        if (HauntedHarvest.SEASON_MOD_INSTALLED) {
            SeasonModCompat.addConfig(builder);
        }
        builder.pop();


        builder.onChange(HauntedHarvest.SEASON_MANAGER::onConfigReload);

        SPEC = builder.build();

        //load early
        SPEC.loadFromFile();
    }


}
