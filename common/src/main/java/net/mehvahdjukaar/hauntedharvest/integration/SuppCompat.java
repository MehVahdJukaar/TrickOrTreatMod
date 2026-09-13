package net.mehvahdjukaar.hauntedharvest.integration;

import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.WorldGenLevel;

//TODO: bodies commented out until supplementaries has a 26.1.2 build
public class SuppCompat {

    public static void triggerSweetTooth(Level level, LivingEntity entity) {
        //CandyItem.increaseSweetTooth(level, entity, 8 * 20);
    }

    public static boolean isFlaxOn() {
        //return CommonConfigs.isEnabled("flax");
        return false;
    }

    public static boolean placeFlax(BlockPos.MutableBlockPos p, WorldGenLevel level, RandomSource random) {
        //if (level.getBlockState(p).isAir()) {
        //    int age = random.nextInt(8);
        //    if (age >= FlaxBlock.DOUBLE_AGE) {
        //        if (!level.getBlockState(p.above()).isAir()) return false;
        //        level.setBlock(p.above(), ModRegistry.FLAX.get().defaultBlockState()
        //                .setValue(FlaxBlock.HALF, DoubleBlockHalf.UPPER).setValue(CropBlock.AGE, age), 2);
        //    }
        //    level.setBlock(p, ModRegistry.FLAX.get().defaultBlockState()
        //            .setValue(CropBlock.AGE, age), 2);
        //    return true;
        //}
        return false;
    }
}
