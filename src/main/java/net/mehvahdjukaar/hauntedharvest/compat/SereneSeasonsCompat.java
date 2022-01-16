package net.mehvahdjukaar.hauntedharvest.compat;

import net.minecraft.world.level.Level;
import net.minecraftforge.common.ForgeConfigSpec;
import sereneseasons.api.season.Season;
import sereneseasons.api.season.SeasonHelper;

import java.util.List;

public class SereneSeasonsCompat {

    private static ForgeConfigSpec.ConfigValue<List<Season.SubSeason>> ALLOWED_SUB_SEASONS;
    private static List<Season.SubSeason> CACHED_SEASONS;


    public static boolean isAutumn(Level level){
        return (CACHED_SEASONS.contains(SeasonHelper.getSeasonState(level).getSubSeason()));
    }


    public static void addConfig(ForgeConfigSpec.Builder builder) {
        ALLOWED_SUB_SEASONS = builder.comment("Sub Seasons in which the mod will be active")
                .define("trick_or_treating_sub_seasons",
                List.of(Season.SubSeason.MID_AUTUMN, Season.SubSeason.LATE_AUTUMN));
    }

    public static void init() {
        CACHED_SEASONS = ALLOWED_SUB_SEASONS.get();
    }
}
