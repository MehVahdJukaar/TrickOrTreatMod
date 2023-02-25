package net.mehvahdjukaar.hauntedharvest.integration;

import net.mehvahdjukaar.supplementaries.common.block.blocks.FlaxBlock;
import net.mehvahdjukaar.supplementaries.common.items.CandyItem;
import net.mehvahdjukaar.supplementaries.configs.CommonConfigs;
import net.mehvahdjukaar.supplementaries.reg.ModRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;

public class SuppCompat {

    public static void triggerSweetTooth(Level level, LivingEntity entity) {
        CandyItem.increaseSweetTooth(level, entity, 8 * 20);
    }

    public static boolean isFlaxOn() {
        return CommonConfigs.isEnabled("flax");
    }

    public static boolean placeFlax(BlockPos.MutableBlockPos p, WorldGenLevel level, RandomSource random) {
        if (level.getBlockState(p).isAir()) {
            int age = random.nextInt(8);
            if (age >= FlaxBlock.DOUBLE_AGE) {
                if (!level.getBlockState(p.above()).isAir()) return false;
                level.setBlock(p.above(), ModRegistry.FLAX.get().defaultBlockState()
                        .setValue(FlaxBlock.HALF, DoubleBlockHalf.UPPER).setValue(FlaxBlock.AGE, age), 2);
            }
            level.setBlock(p, ModRegistry.FLAX.get().defaultBlockState()
                    .setValue(FlaxBlock.AGE, age), 2);
            return true;
        }
        return false;
    }
}
