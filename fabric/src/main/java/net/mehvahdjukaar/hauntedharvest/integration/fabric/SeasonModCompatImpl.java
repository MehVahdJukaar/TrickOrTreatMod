package net.mehvahdjukaar.hauntedharvest.integration.fabric;

import io.github.lucaargolo.seasons.FabricSeasons;
import io.github.lucaargolo.seasons.utils.Season;
import net.mehvahdjukaar.moonlight.api.platform.configs.ConfigBuilder;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

//fabric seasons
public class SeasonModCompatImpl {

    private static Supplier<Boolean> MOBS_WEAR_PUMPKINS = () -> false;
    private static Supplier<List<String>> HALLOWEEN_SEASON;
    private static List<Season> VALID_HALLOWEEN_SEASONS = new ArrayList<>();

    //if winder AI should be on
    public static boolean isAutumn(Level level) {
        return (VALID_HALLOWEEN_SEASONS.contains(FabricSeasons.getCurrentSeason(level)));
    }


    public static void addConfig(ConfigBuilder builder) {
        HALLOWEEN_SEASON = builder.comment("Season in which the mod villager AI behaviors will be active")
                .define("halloween_seasons",
                        List.of(Season.FALL.toString()),
                        s -> Arrays.stream(Season.values()).anyMatch(d -> d.toString().equals(s)));

        MOBS_WEAR_PUMPKINS = builder.comment("Allows mobs to wear pumpkins during here defined halloween season")
                .define("mobs_wear_pumpkins", false);
    }

    public static void refresh() {
        VALID_HALLOWEEN_SEASONS = HALLOWEEN_SEASON.get().stream().map(Season::valueOf).toList();
    }

    public static boolean shouldMobWearPumpkin(Level level) {
        return MOBS_WEAR_PUMPKINS.get() && isAutumn(level);
    }
}
