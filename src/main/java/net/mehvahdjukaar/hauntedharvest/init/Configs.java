package net.mehvahdjukaar.hauntedharvest.init;

import net.minecraftforge.common.ForgeConfigSpec;

public class Configs {
    public static ForgeConfigSpec.IntValue START_DAY;
    public static ForgeConfigSpec.IntValue START_MONTH;

    public static ForgeConfigSpec.IntValue END_DAY;
    public static ForgeConfigSpec.IntValue END_MONTH;

    public static ForgeConfigSpec.IntValue START_TIME;
    public static ForgeConfigSpec.IntValue END_TIME;

    public static ForgeConfigSpec.BooleanValue SEASONAL;

    public static ForgeConfigSpec buildConfig(){
        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();
        builder.push("pumpkin_placement_start");
        START_MONTH = builder.comment("Day from which villagers will start placing pumpkins")
                .defineInRange("month", 10, 1, 12);
        START_DAY = builder.comment("Day from which villagers will start placing pumpkins")
                .defineInRange("day", 20, 1, 31);
        builder.pop();
        builder.push("pumpkin_placement_end");
        END_MONTH = builder.comment("Day from which villagers will start removing placed pumpkins")
                .defineInRange("month", 11, 1, 12);
        END_DAY = builder.comment("Day from which villagers will start removing placed pumpkins")
                .defineInRange("day", 10, 1, 31);
        builder.pop();
        SEASONAL = builder.comment("Use the dates used for pumpkin placement for the whole event. " +
                        "This means that villagers will only trick or treat during that period. " +
                        "Leave to false to have it active year round." +
                        "Note that the new behaviors might not kick in instantly after the start date and you might need to log out of the world")
                .define("seasonal", false);
        builder.push("trick_or_treating").comment("Nota that all these configs will not take effect until the game is reloaded");
        START_TIME = builder.comment("Time of day at which baby villagers will start trick-or-treating")
                .defineInRange("start_time", 12000, 0, 24000);
        END_TIME = builder.comment("Time of day at which baby villagers will stop trick-or-treating. Note that this will only properly work if it's at night since baby villagers can only trick or treat during their sleep schedule which is from 12000 to 0")
                .defineInRange("end_time", 0, 0, 24000);

        return builder.build();
    }
}
