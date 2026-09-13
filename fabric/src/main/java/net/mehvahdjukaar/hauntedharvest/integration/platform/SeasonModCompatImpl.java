package net.mehvahdjukaar.hauntedharvest.integration.platform;

import net.mehvahdjukaar.moonlight.api.platform.configs.ConfigBuilder;
import net.minecraft.world.level.Level;

//TODO: bodies commented out until the season mod has a 26.1.2 build. see git history for the real impl
public class SeasonModCompatImpl {

    public static boolean isAutumn(Level level) {
        return false;
    }

    public static boolean shouldMobWearPumpkin(Level level) {
        return false;
    }

    public static void addConfig(ConfigBuilder builder) {
    }

    public static void refresh() {
    }
}
