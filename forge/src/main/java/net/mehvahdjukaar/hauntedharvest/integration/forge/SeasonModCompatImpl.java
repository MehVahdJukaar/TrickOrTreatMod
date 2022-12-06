package net.mehvahdjukaar.hauntedharvest.integration.forge;

import net.mehvahdjukaar.moonlight.api.platform.configs.ConfigBuilder;
import net.minecraft.world.level.Level;
import sereneseasons.api.season.Season;
import sereneseasons.api.season.SeasonHelper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

//serene seasons
public class SeasonModCompatImpl {

    private static final List<Season.SubSeason> VALID_WINTER_SEASONS = new ArrayList<>();
    private static Supplier<List<String>> SEASONS_CONFIG;


    //if winder AI should be on
    public static boolean isAutumn(Level level) {
        return (VALID_WINTER_SEASONS.contains(SeasonHelper.getSeasonState(level).getSubSeason()));
    }


    public static void addConfig(ConfigBuilder builder) {
        SEASONS_CONFIG = builder.comment("Season in which the mod villager AI behaviors will be active")
                .define("winter_season_sub_seasons",
                        List.of(Season.SubSeason.MID_AUTUMN.toString(), Season.SubSeason.LATE_AUTUMN.toString()),
                        s -> Arrays.stream(Season.SubSeason.values()).anyMatch(d -> d.toString().equals(s)));
    }

    public static void refresh() {
        VALID_WINTER_SEASONS.clear();
        VALID_WINTER_SEASONS.addAll(SEASONS_CONFIG.get().stream().map(Season.SubSeason::valueOf).toList());
    }
}
