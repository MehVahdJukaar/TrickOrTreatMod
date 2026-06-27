package net.mehvahdjukaar.hauntedharvest.integration;

import net.mehvahdjukaar.candlelight.api.PlatformImpl;

public class QuarkCompat {

    @PlatformImpl
    public static void init(){
        throw new ArrayStoreException();
    }
}
