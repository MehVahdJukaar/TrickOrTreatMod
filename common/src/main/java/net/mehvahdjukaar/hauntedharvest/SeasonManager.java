package net.mehvahdjukaar.hauntedharvest;

import net.mehvahdjukaar.hauntedharvest.configs.ModConfigs;
import net.mehvahdjukaar.hauntedharvest.integration.SeasonModCompat;
import net.minecraft.world.level.Level;

import java.util.Calendar;
import java.util.Date;

public class SeasonManager {

    private int trickOrTreatStart;
    private int trickOrTreatEnd;
    private boolean isHalloweenRealTime;
    private boolean useSeasonMod;

    public void onConfigReload() {

        //refresh date after configs are loaded
        int startM = ModConfigs.START_MONTH.get() - 1;
        int startD = ModConfigs.START_DAY.get();

        int endM = ModConfigs.END_MONTH.get() - 1;
        int endD = ModConfigs.END_DAY.get();

        boolean inv = startM > endM;

        Calendar calendar = Calendar.getInstance();

        //pain
        calendar.set(0, startM, startD);
        Date start = calendar.getTime();
        calendar.set((inv ? 1 : 0), endM, endD);
        Date end = calendar.getTime();

        Calendar todayCalendar = Calendar.getInstance();
        if (todayCalendar.before(start) && inv) {
            todayCalendar.set(1, todayCalendar.get(Calendar.MONTH), todayCalendar.get(Calendar.DATE));
        }

        //if seasonal use pumpkin placement time window
        isHalloweenRealTime = todayCalendar.after(start) && todayCalendar.before(end);

        trickOrTreatStart = ModConfigs.START_TIME.get();
        trickOrTreatEnd = ModConfigs.END_TIME.get();

        useSeasonMod = HauntedHarvest.SEASON_MOD_INSTALLED && ModConfigs.SEASONS_MOD_COMPAT.get();

        if (useSeasonMod) {
            SeasonModCompat.refresh();
        }
    }

    public boolean isHalloween(Level level) {
        if (this.useSeasonMod) return SeasonModCompat.isAutumn(level);
        return isHalloweenRealTime;
    }

    public boolean isTrickOrTreatTime(Level level) {
        return isHalloween(level) && isBetween(trickOrTreatStart, trickOrTreatEnd, level.getDayTime() % 24000);
    }

    private boolean isBetween(float start, float end, float mid) {
        if (start < end) return mid >= start && mid <= end;
        else return mid <= end || mid >= start;
    }

}
