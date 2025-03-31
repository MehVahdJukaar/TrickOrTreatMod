package net.mehvahdjukaar.hauntedharvest.neoforge;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.ItemAbilities;

public class HHPlatformStuffImpl {
    public static void setItemLifespan(ItemEntity item, int lifespan) {
        item.lifespan = lifespan;
    }

    public static boolean isTopCarver(ItemStack stack) {
        return stack.canPerformAction(ItemAbilities.SHEARS_CARVE);
    }

    public static float getGrowthSpeed(BlockState state, ServerLevel level, BlockPos pos) {
        return Access.callGetGrowthSpeed(state, level, pos);
    }


    private static abstract class Access extends CropBlock {

        public Access(Properties properties) {
            super(properties);
        }

        public static float callGetGrowthSpeed(BlockState state, ServerLevel level, BlockPos pos) {
            return getGrowthSpeed(state, level, pos);
        }
    }

}
