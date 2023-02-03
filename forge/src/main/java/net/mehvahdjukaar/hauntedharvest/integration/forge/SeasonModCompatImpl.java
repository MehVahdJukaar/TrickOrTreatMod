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

    private static final List<Season.SubSeason> VALID_HALLOWEEN_SEASONS = new ArrayList<>();
    private static final List<Season.SubSeason> VALID_MOB_WEAR_PUMPKINS_SEASONS = new ArrayList<>();
    private static Supplier<List<String>> HALLOWEEN_SEASONS;
    private static Supplier<List<String>> MOBS_WEAR_PUMPKINS_SEASONS;


    //if winder AI should be on
    public static boolean isAutumn(Level level) {
        return (VALID_HALLOWEEN_SEASONS.contains(SeasonHelper.getSeasonState(level).getSubSeason()));
    }

    public static boolean shouldMobWearPumpkin(Level level){
        return (VALID_MOB_WEAR_PUMPKINS_SEASONS.contains(SeasonHelper.getSeasonState(level).getSubSeason()));
    }


    public static void addConfig(ConfigBuilder builder) {
        HALLOWEEN_SEASONS = builder.comment("Season in which the mod villager AI behaviors will be active")
                .define("halloween_seasons",
                        List.of(Season.SubSeason.MID_AUTUMN.toString(), Season.SubSeason.LATE_AUTUMN.toString()),
                        s -> Arrays.stream(Season.SubSeason.values()).anyMatch(d -> d.toString().equals(s)));
        MOBS_WEAR_PUMPKINS_SEASONS = builder.comment("Adds custom times in which mobs can wear pumpkins. Leave empty to ignore")
                .define("mob_wear_pumpkins_seasons",
                List.of(Season.SubSeason.LATE_AUTUMN.toString()),
                s -> Arrays.stream(Season.SubSeason.values()).anyMatch(d -> d.toString().equals(s)));
    }

    public static void refresh() {
        VALID_HALLOWEEN_SEASONS.clear();
        VALID_HALLOWEEN_SEASONS.addAll(HALLOWEEN_SEASONS.get().stream().map(Season.SubSeason::valueOf).toList());

        VALID_MOB_WEAR_PUMPKINS_SEASONS.clear();
        VALID_MOB_WEAR_PUMPKINS_SEASONS.addAll(MOBS_WEAR_PUMPKINS_SEASONS.get().stream().map(Season.SubSeason::valueOf).toList());
    }
}
