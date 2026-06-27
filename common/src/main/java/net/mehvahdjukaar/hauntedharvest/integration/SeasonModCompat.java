package net.mehvahdjukaar.hauntedharvest.integration;

import net.mehvahdjukaar.candlelight.api.PlatformImpl;
import net.mehvahdjukaar.moonlight.api.platform.configs.ConfigBuilder;
import net.minecraft.world.level.Level;

public class SeasonModCompat {

    @PlatformImpl
    public static boolean isAutumn(Level level) {
        throw new AssertionError();
    }

    @PlatformImpl
    public static boolean shouldMobWearPumpkin(Level level) {
        throw new AssertionError();
    }

    @PlatformImpl
    public static void addConfig(ConfigBuilder builder) {
        throw new AssertionError();
    }

    @PlatformImpl
    public static void refresh() {
        throw new AssertionError();
    }
}
