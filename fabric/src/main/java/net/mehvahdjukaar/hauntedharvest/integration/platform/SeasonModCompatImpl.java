package net.mehvahdjukaar.hauntedharvest.integration.platform;

import net.mehvahdjukaar.moonlight.api.platform.configs.ConfigBuilder;
import net.minecraft.world.level.Level;

//TODO: uncomment when season mods update
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
