package net.mehvahdjukaar.hauntedharvest.integration;

import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;

public class FDCompat {

    @ExpectPlatform
    public static void init() {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static BlockState getTomato(RandomSource randomSource) {
        throw new AssertionError();
    }
}
