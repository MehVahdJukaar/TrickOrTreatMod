package net.mehvahdjukaar.hauntedharvest.init;

import net.mehvahdjukaar.hauntedharvest.compat.SereneSeasonsCompat;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.ModList;

public class Configs {
    public static ForgeConfigSpec.IntValue START_DAY;
    public static ForgeConfigSpec.IntValue START_MONTH;

    public static ForgeConfigSpec.IntValue END_DAY;
    public static ForgeConfigSpec.IntValue END_MONTH;

    public static ForgeConfigSpec.IntValue START_TIME;
    public static ForgeConfigSpec.IntValue END_TIME;

    public static ForgeConfigSpec.BooleanValue SERENE_SEASONS_COMPAT;

    public static ForgeConfigSpec SERVER_SPEC;

    public static ForgeConfigSpec buildConfig() {
        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();
        builder.push("halloween_start");
        START_MONTH = builder.comment("Day from which villagers will start placing pumpkins & trick or treating")
                .defineInRange("month", 10, 1, 12);
        START_DAY = builder.comment("Day from which villagers will start placing pumpkins & trick or treating")
                .defineInRange("day", 20, 1, 31);
        builder.pop();
        builder.push("halloween_end");
        END_MONTH = builder.comment("Day from which villagers will start removing placed pumpkins")
                .defineInRange("month", 11, 1, 12);
        END_DAY = builder.comment("Day from which villagers will start removing placed pumpkins")
                .defineInRange("day", 10, 1, 31);
        builder.pop();

        builder.push("trick_or_treating").comment("Nota that all these configs will not take effect until the game is reloaded");
        START_TIME = builder.comment("Time of day at which baby villagers will start trick-or-treating")
                .defineInRange("start_time", 12000, 0, 24000);
        END_TIME = builder.comment("Time of day at which baby villagers will stop trick-or-treating. Note that this will only properly work if it's at night since baby villagers can only trick or treat during their sleep schedule which is from 12000 to 0")
                .defineInRange("end_time", 0, 0, 24000);

        builder.pop();

        builder.push("serene_seasons_compat");

        SERENE_SEASONS_COMPAT = builder.comment("Enables Serene Seasons compatibility. Only takes effect if the mod is installed. Will make trick or treating only active during certain seasons. Note that this will override previous time window settings")
                .define("enabled", ModList.get().isLoaded("sereneseasons"));
        if (ModList.get().isLoaded("sereneseasons")) SereneSeasonsCompat.addConfig(builder);
        builder.pop();

        SERVER_SPEC = builder.build();
        return SERVER_SPEC;
    }



}
