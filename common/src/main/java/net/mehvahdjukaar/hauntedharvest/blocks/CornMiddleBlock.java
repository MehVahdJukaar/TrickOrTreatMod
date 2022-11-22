package net.mehvahdjukaar.hauntedharvest.blocks;

import net.mehvahdjukaar.hauntedharvest.reg.ModRegistry;
import net.mehvahdjukaar.moonlight.api.platform.ForgeHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class CornMiddleBlock extends AbstractCornBlock {

    public static final int MAX_AGE = 2;
    public static final IntegerProperty AGE = BlockStateProperties.AGE_2;
    private static final VoxelShape[] SHAPE_BY_AGE = new VoxelShape[]{
            Block.box(1, 0.0, 1, 15.0, 5.0, 15.0),
            Block.box(1, 0.0, 1, 15.0, 11.0, 15.0),
            Block.box(1, 0.0, 1, 15.0, 16.0, 15.0)};


    public CornMiddleBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected Block getTopBlock() {
        return ModRegistry.CORN_TOP.get();
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        if (getAge(state) == getMaxAge()) {
            var top = getTopBlock();
            if (top != null && !level.getBlockState(pos.above()).is(top)) return false;
        }
        BlockState below = level.getBlockState(pos.below());
        if (!(below.getBlock() instanceof CornBaseBlock base) || !base.isMaxAge(below)) return false;
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

    @Override
    public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult rayTraceResult) {
        InteractionResult old = super.use(state, world, pos, player, hand, rayTraceResult);
        if (!old.consumesAction()) {
            var ev = ForgeHelper.onRightClickBlock(player, hand, pos.below(), rayTraceResult);
            if (ev != null) return ev;
        }
        return old;
    }

    @Override
    protected boolean mayPlaceOn(BlockState state, BlockGetter level, BlockPos pos) {
        return state.is(ModRegistry.CORN_BASE.get());
    }

}
