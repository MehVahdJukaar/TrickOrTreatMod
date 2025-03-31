package net.mehvahdjukaar.hauntedharvest;

import dev.architectury.injectables.annotations.ExpectPlatform;
import net.mehvahdjukaar.hauntedharvest.blocks.AbstractCornBlock;
import net.mehvahdjukaar.hauntedharvest.blocks.ModCarvedPumpkinBlockTile;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.animal.SnowGolem;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Contract;

import java.util.List;

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
    public static float getGrowthSpeed(BlockState state, ServerLevel level, BlockPos pos) {
        throw new AssertionError();

    }
}
