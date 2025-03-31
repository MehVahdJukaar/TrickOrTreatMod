package net.mehvahdjukaar.hauntedharvest.fabric;

import net.mehvahdjukaar.hauntedharvest.blocks.AbstractCornBlock;
import net.mehvahdjukaar.hauntedharvest.reg.ModTags;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ShearsItem;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.state.BlockState;

public class HHPlatformStuffImpl {
    public static void setItemLifespan(ItemEntity item, int lifespan) {
    }

    public static boolean isTopCarver(ItemStack stack) {
        return stack.getItem() instanceof ShearsItem || (!(stack.getItem() instanceof SwordItem) && stack.is(ModTags.CARVERS));
    }

    public static float getGrowthSpeed(BlockState state, ServerLevel level, BlockPos pos) {
        return Access.callGetGrowthSpeed(state.getBlock(), level, pos);
    }

    private static abstract class Access extends CropBlock {

        public Access(Properties properties) {
            super(properties);
        }

        public static float callGetGrowthSpeed(Block block, ServerLevel level, BlockPos pos) {
            return getGrowthSpeed(block, level, pos);
        }
    }


}
