package net.mehvahdjukaar.hauntedharvest.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RedstoneTorchBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;

import org.jetbrains.annotations.Nullable;

public class RedstoneCarvedPumpkinBlock extends ModCarvedPumpkinBlock {
    public static final BooleanProperty LIT = RedstoneTorchBlock.LIT;

    public RedstoneCarvedPumpkinBlock(Properties properties, PumpkinType type) {
        super(properties.lightLevel(s -> s.getValue(BlockStateProperties.LIT) ? 7 : 0), type);
        this.registerDefaultState(this.defaultBlockState().setValue(LIT, false));
    }

    @Override
    public PumpkinType getType(BlockState state) {
        return state.getValue(LIT) ? super.getType(state) : PumpkinType.NORMAL;
    }

    @Nullable
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return super.getStateForPlacement(context)
                .setValue(LIT, context.getLevel().hasNeighborSignal(context.getClickedPos()));
    }

    @Override
    public void neighborChanged(BlockState state, Level worldIn, BlockPos pos, Block blockIn, BlockPos fromPos, boolean isMoving) {
        if (!worldIn.isClientSide) {
            boolean flag = state.getValue(LIT);
            if (flag != worldIn.hasNeighborSignal(pos)) {
                worldIn.setBlock(pos, state.cycle(LIT), 2);
                if(worldIn.getBlockEntity(pos) instanceof ModCarvedPumpkinBlockTile tile){
                    tile.refreshTextureKey();
                }
            }
        }
    }

    @Override
    public void tick(BlockState state, ServerLevel worldIn, BlockPos pos, RandomSource rand) {
        if (state.getValue(LIT) && !worldIn.hasNeighborSignal(pos)) {
            worldIn.setBlock(pos, state.cycle(LIT), 2);
            if(worldIn.getBlockEntity(pos) instanceof ModCarvedPumpkinBlockTile tile){
                tile.refreshTextureKey();
            }
        }
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(LIT);
    }
}
