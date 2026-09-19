package net.mehvahdjukaar.hauntedharvest;

import net.mehvahdjukaar.hauntedharvest.configs.CommonConfigs;
import net.mehvahdjukaar.hauntedharvest.integration.CompatHandler;
import net.mehvahdjukaar.hauntedharvest.integration.SeasonModCompat;
import net.minecraft.world.level.Level;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.MonthDay;

public class SeasonManager {

    private int trickOrTreatStart;
    private int trickOrTreatEnd;
    private boolean isHalloweenRealTime;
    private boolean isPumpkinWearTime;
    private boolean isPreciselyHalloweenRealTime;
    private boolean useSeasonMod;

    public int getTrickOrTreatEnd() {
        return trickOrTreatEnd;
    }

    public int getTrickOrTreatStart() {
        return trickOrTreatStart;
    }

    public boolean usesSeasonMod() {
        return useSeasonMod;
    }

    public void refresh() {
        //refresh date after configs are loaded

        MonthDay today = MonthDay.from(LocalDate.now());

        isHalloweenRealTime = isDayInBetween(today, CommonConfigs.SEASON_START.get(), CommonConfigs.SEASON_END.get());
        isPumpkinWearTime = isDayInBetween(today, CommonConfigs.WEAR_START.get(), CommonConfigs.WEAR_END.get());
        isPreciselyHalloweenRealTime = today.equals(MonthDay.of(10, 31));

        trickOrTreatStart = toDayTime(CommonConfigs.START_TIME.get());
        trickOrTreatEnd = toDayTime(CommonConfigs.END_TIME.get());

        useSeasonMod = CompatHandler.SEASON_MOD_INSTALLED && CommonConfigs.SEASONS_MOD_COMPAT.get();

        if (useSeasonMod) {
            SeasonModCompat.refresh();
        }
    }

    //configs use a normal clock, the world counts ticks from 6 am
    private static int toDayTime(LocalTime time) {
        return Math.floorMod((time.getHour() - 6) * 1000 + time.getMinute() * 1000 / 60, 24000);
    }

    private static boolean isDayInBetween(MonthDay today, MonthDay start, MonthDay end) {
        //window wraps over new year
        if (start.isAfter(end)) return !today.isBefore(start) || !today.isAfter(end);
        return !today.isBefore(start) && !today.isAfter(end);
    }

    public boolean isHalloween(Level level) {
        if (this.useSeasonMod) return SeasonModCompat.isAutumn(level);
        return isHalloweenRealTime;
    }

    public boolean shouldWearCustomPumpkin(Level level) {
        if (isPreciselyHalloweenRealTime) return false;
        if (this.useSeasonMod) return SeasonModCompat.shouldMobWearPumpkin(level);
        return isPumpkinWearTime;
    }

    public boolean isTrickOrTreatTime(Level level) {
        return isHalloween(level) && isBetween(trickOrTreatStart, trickOrTreatEnd, level.getOverworldClockTime() % 24000);
    }

    private boolean isBetween(float start, float end, float mid) {
        if (start < end) return mid >= start && mid <= end;
        else return mid <= end || mid >= start;
    }

}
