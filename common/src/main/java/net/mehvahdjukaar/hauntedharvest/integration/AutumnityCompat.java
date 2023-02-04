package net.mehvahdjukaar.hauntedharvest.integration;

import dev.architectury.injectables.annotations.ExpectPlatform;

public class AutumnityCompat {

    @ExpectPlatform
    public static void init(){
        throw new AssertionError();
    }

    @ExpectPlatform
    public static void setup() {
    }
}
