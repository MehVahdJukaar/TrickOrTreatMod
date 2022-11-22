package net.mehvahdjukaar.hauntedharvest.blocks;

import net.mehvahdjukaar.hauntedharvest.reg.ModRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class CornBaseBlock extends AbstractCornBlock {

    public static final int MAX_AGE = 3;
    public static final IntegerProperty AGE = BlockStateProperties.AGE_3;
    private static final VoxelShape[] SHAPE_BY_AGE = new VoxelShape[]{
            Block.box(6, 0.0, 6, 10.0, 4.0, 10.0),
            Block.box(5.0, 0.0, 5.0, 11.0, 8.0, 11.0),
            Block.box(3.0, 0.0, 3.0, 13.0, 12.0, 13.0),
            Block.box(1.0, 0.0, 1.0, 15.0, 16.0, 15.0)};

    public CornBaseBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected Block getTopBlock() {
        return ModRegistry.CORN_MIDDLE.get();
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        if (getAge(state) == getMaxAge()) {
            if (!level.getBlockState(pos.above()).is(getTopBlock())){
                return false;
            }
        }
        return super.canSurvive(state, level, pos);
    }

    @Override
    public IntegerProperty getAgeProperty() {
        return AGE;
    }


    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE_BY_AGE[state.getValue(this.getAgeProperty())];
    }

    @Override
    public int getMaxAge() {
        return MAX_AGE;
    }





}
