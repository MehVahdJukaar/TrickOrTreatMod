package net.mehvahdjukaar.hauntedharvest.integration;

import dev.architectury.injectables.annotations.ExpectPlatform;

public class QuarkCompat {

    @ExpectPlatform
    public static void init(){
        throw new ArrayStoreException();
    }
}
