package net.mehvahdjukaar.hauntedharvest;

import net.mehvahdjukaar.hauntedharvest.configs.CommonConfigs;
import net.mehvahdjukaar.hauntedharvest.integration.CompatHandler;
import net.mehvahdjukaar.hauntedharvest.integration.SeasonModCompat;
import net.minecraft.world.level.Level;

import java.time.LocalDate;
import java.time.temporal.ChronoField;
import java.util.Calendar;
import java.util.Date;

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

    public void refresh() {
        //refresh date after configs are loaded

        isHalloweenRealTime = isDayInBetween(CommonConfigs.START_MONTH.get(), CommonConfigs.START_DAY.get(),
                CommonConfigs.END_MONTH.get(), CommonConfigs.END_DAY.get());

        isPumpkinWearTime = isDayInBetween(CommonConfigs.P_START_MONTH.get(), CommonConfigs.P_START_DAY.get(),
                CommonConfigs.P_END_MONTH.get(), CommonConfigs.P_END_DAY.get());

        LocalDate localdate = LocalDate.now();
        isPreciselyHalloweenRealTime = localdate.get(ChronoField.DAY_OF_MONTH) == 31 &&
                localdate.get(ChronoField.MONTH_OF_YEAR) == 10;

        trickOrTreatStart = CommonConfigs.START_TIME.get();
        trickOrTreatEnd = CommonConfigs.END_TIME.get();

        useSeasonMod = CompatHandler.SEASON_MOD_INSTALLED && CommonConfigs.SEASONS_MOD_COMPAT.get();

        if (useSeasonMod) {
            SeasonModCompat.refresh();
        }
    }

    private boolean isDayInBetween(int startM, int startD, int endM, int endD) {
        startM = startM - 1;
        endM = endM - 1;

        boolean inv = startM > endM;

        Calendar calendar = Calendar.getInstance();

        //pain
        calendar.set(0, startM, startD);
        Date start = calendar.getTime();
        calendar.set((inv ? 1 : 0), endM, endD);
        Date end = calendar.getTime();

        Calendar todayCalendar = Calendar.getInstance();
        int ii = (todayCalendar.getTime().before(start) && inv) ? 1 : 0;
        todayCalendar.set(ii, todayCalendar.get(Calendar.MONTH), todayCalendar.get(Calendar.DATE));


        //if seasonal use pumpkin placement time window
        return todayCalendar.getTime().after(start) && todayCalendar.getTime().before(end);
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
        return isHalloween(level) && isBetween(trickOrTreatStart, trickOrTreatEnd, level.getDayTime() % 24000);
    }

    private boolean isBetween(float start, float end, float mid) {
        if (start < end) return mid >= start && mid <= end;
        else return mid <= end || mid >= start;
    }

}
