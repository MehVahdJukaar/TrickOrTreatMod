package net.mehvahdjukaar.hauntedharvest.blocks;

import net.mehvahdjukaar.hauntedharvest.reg.ModRegistry;
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

public class CornTopBlock extends AbstractCornBlock {

    public static final int MAX_AGE = 1;
    public static final IntegerProperty AGE = BlockStateProperties.AGE_1;
    private static final VoxelShape[] SHAPE_BY_AGE = new VoxelShape[]{
            Block.box(1, 0.0, 1, 15, 5.0, 15),
            Block.box(1, 0.0, 1, 15, 14.0, 15)};

    public CornTopBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected Block getTopBlock() {
        return null;
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        BlockState below = level.getBlockState(pos.below());
        if (!(below.getBlock() instanceof CornMiddleBlock base) || !base.isMaxAge(below)){
            return false;
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

    @Override
    public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult rayTraceResult) {
        InteractionResult old = super.use(state, world, pos, player, hand, rayTraceResult);
        if (!old.consumesAction()) {
            BlockPos below = pos.below(getHeight());
            BlockState belowBlock = world.getBlockState(below);
            if (belowBlock.getBlock() instanceof CornBaseBlock b) {
               // return b.use(belowBlock, world, below, player, hand, rayTraceResult);
                //var ev = ForgeHelper.onRightClickBlock(player, hand, pos.below(2), rayTraceResult);
                //if (ev != null) return ev;
            }
        }
        return old;
    }

    @Override
    protected boolean mayPlaceOn(BlockState state, BlockGetter level, BlockPos pos) {
        return state.is(ModRegistry.CORN_MIDDLE.get());
    }

    @Override
    public int getHeight() {
        return 2;
    }
}
