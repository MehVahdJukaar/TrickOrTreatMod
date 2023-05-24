package net.mehvahdjukaar.hauntedharvest.blocks;

import net.mehvahdjukaar.hauntedharvest.reg.ModRegistry;
import net.mehvahdjukaar.moonlight.api.block.IBeeGrowable;
import net.mehvahdjukaar.moonlight.api.platform.ForgeHelper;
import net.mehvahdjukaar.moonlight.api.platform.PlatHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;

import org.jetbrains.annotations.Nullable;

public abstract class AbstractCornBlock extends CropBlock implements IBeeGrowable {
    protected AbstractCornBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(this.getAgeProperty());
    }

    @Override
    public boolean isRandomlyTicking(BlockState state) {
        return !isMaxAge(state);
    }

    @Override
    public void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (!PlatHelper.isAreaLoaded(level, pos, 1))
            return; // Forge: prevent loading unloaded chunks when checking neighbor's light
        if (level.getRawBrightness(pos, 0) >= 9 && level.random.nextFloat() < 0.6) {
            if (this.isValidBonemealTarget(level, pos, state, level.isClientSide)) {

                float f = getGrowthSpeed(this, level, pos);
                if (ForgeHelper.onCropsGrowPre(level, pos, state, random.nextInt((int) (30.0F / f) + 1) == 0)) {
                    this.growCropBy(level, pos, state, 1);
                    ForgeHelper.onCropsGrowPost(level, pos, state);
                }
            }
        }
    }

    public void growCropBy(Level level, BlockPos pos, BlockState state, int increment) {
        if (increment <= 0) return;
        int newAge = this.getAge(state) + increment;
        int maxAge = this.getMaxAge();
        if (newAge > maxAge) {
            BlockPos above = pos.above();
            BlockState aboveState = level.getBlockState(above);
            if (aboveState.getBlock() instanceof AbstractCornBlock cm) {
                cm.growCropBy(level, above, aboveState, increment);
            }
        } else {
            Block top = this.getTopBlock();
            if (newAge == maxAge && top != null) {
                level.setBlock(pos.above(), top.defaultBlockState(), 2);
            }
            level.setBlock(pos, getStateForAge(newAge), 2);
        }
    }

    @Override
    public void growCrops(Level level, BlockPos pos, BlockState state) {
        growCropBy(level, pos, state, this.getBonemealAgeIncrease(level));
    }

    @Override
    public boolean getPollinated(Level level, BlockPos pos, BlockState state) {
        growCropBy(level, pos, state, 1);
        return true;
    }

    @Override
    protected ItemLike getBaseSeedId() {
        return ModRegistry.KERNELS.get();
    }

    @Override
    public boolean isValidBonemealTarget(LevelReader level, BlockPos pos, BlockState state, boolean isClient) {
        int age = this.getAge(state);
        int maxAge = this.getMaxAge();
        if (age + 1 < maxAge) { //isn't max age or about to grow second stage
            return true;
        } else {
            //if it can grow or place block above
            BlockPos above = pos.above();
            BlockState aboveState = level.getBlockState(above);
            if (age == maxAge) { //needs to grow
                return aboveState.getBlock() instanceof AbstractCornBlock cb && cb.isValidBonemealTarget(level, above, aboveState, false);
            } else {
                //place top
                return this.getTopBlock() == null || aboveState.getMaterial().isReplaceable();
            }
        }
    }

    @Override
    protected int getBonemealAgeIncrease(Level level) {
        return super.getBonemealAgeIncrease(level) / 3;
    }

    @Nullable
    protected abstract Block getTopBlock();

    public abstract int getHeight();

    public boolean isPlantFullyGrown(BlockState state, BlockPos pos, Level level) {
        while (state.getBlock() instanceof AbstractCornBlock cb) {
            if (!cb.isMaxAge(state)) return false;
            if (cb.getTopBlock() == null) return true;
            pos = pos.above();
            state = level.getBlockState(pos);
        }
        return false;
    }

    public static boolean spawn(BlockPos pos, LevelAccessor level, int age) {

        if (level.getBlockState(pos).isAir()) {
            boolean top = false;
            if (age > 2) {
                if (!level.getBlockState(pos.above()).isAir()) return false;
                BlockPos above1 = pos.above();
                if (age > 4) {
                    BlockPos above = pos.above(2);
                    if (!level.getBlockState(above).isAir()) return false;
                    top = true;
                    level.setBlock(above, ModRegistry.CORN_TOP.get().defaultBlockState().setValue(CornTopBlock.AGE, Math.min(age - 5, 1)), 2);
                }
                level.setBlock(above1, ModRegistry.CORN_MIDDLE.get().defaultBlockState().setValue(CornMiddleBlock.AGE, Math.min(age - 3, 2)), 2);
            }
            level.setBlock(pos, ModRegistry.CORN_BASE.get().defaultBlockState().setValue(CornBaseBlock.AGE, Math.min(age, 3)), 2);
            if(top && level.getBlockState(pos).isAir()){
                int aa = 1;
            }
            return true;
        }
        return false;
    }
}
