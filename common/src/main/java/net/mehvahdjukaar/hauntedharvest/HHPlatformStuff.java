package net.mehvahdjukaar.hauntedharvest;

import dev.architectury.injectables.annotations.ExpectPlatform;
import net.mehvahdjukaar.hauntedharvest.blocks.ModCarvedPumpkinBlockTile;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.world.entity.animal.SnowGolem;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Contract;

public class HHPlatformStuff {

    @ExpectPlatform
    public static void setItemLifespan(ItemEntity item, int lifespan){
        throw new AssertionError();
    }

    @Contract
    @ExpectPlatform
    public static boolean isTopCarver(ItemStack stack) {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static void addPumpkinData(ModCarvedPumpkinBlockTile tile, SnowGolem snowGolem) {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static ShaderInstance getBlur() {
        throw new AssertionError();
    }
}
