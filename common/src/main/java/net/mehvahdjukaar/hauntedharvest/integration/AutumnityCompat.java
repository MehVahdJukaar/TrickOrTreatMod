package net.mehvahdjukaar.hauntedharvest.integration;

import net.mehvahdjukaar.candlelight.api.PlatformImpl;

public class AutumnityCompat {

    @PlatformImpl
    public static void init(){
        throw new AssertionError();
    }
}
