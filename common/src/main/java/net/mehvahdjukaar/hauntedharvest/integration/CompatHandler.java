package net.mehvahdjukaar.hauntedharvest.integration;

import net.mehvahdjukaar.moonlight.api.platform.PlatHelper;

public class CompatHandler {
    public static final boolean SEASON_MOD_INSTALLED = PlatHelper.isModLoaded(PlatHelper.getPlatform().isForge() ? "sereneseasons" : "seasons");
    public static final boolean SUPP_INSTALLED = PlatHelper.isModLoaded("supplementaries");
    public static final boolean FD_INSTALLED = PlatHelper.isModLoaded("farmersdelight");
    public static final boolean QUARK_INSTALLED = PlatHelper.isModLoaded("quark");
    public static final boolean AUTUMNITY_INSTALLED = PlatHelper.isModLoaded("autumnity");

    public static void init() {
        if (FD_INSTALLED) FDCompat.init();
        if (QUARK_INSTALLED) QuarkCompat.init();
        if (AUTUMNITY_INSTALLED) AutumnityCompat.init();
    }

    public static void setup(){
        if (AUTUMNITY_INSTALLED) AutumnityCompat.setup();
    }
}
