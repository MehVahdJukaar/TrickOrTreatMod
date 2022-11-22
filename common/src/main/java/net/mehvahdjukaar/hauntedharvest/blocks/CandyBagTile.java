package net.mehvahdjukaar.hauntedharvest.blocks;

import net.mehvahdjukaar.hauntedharvest.reg.ModRegistry;
import net.mehvahdjukaar.moonlight.api.block.ItemDisplayTile;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.state.BlockState;

public class CandyBagTile extends ItemDisplayTile {

    public CandyBagTile(BlockPos blockPos, BlockState blockState) {
        super(ModRegistry.CANDY_BAG_TILE.get(), blockPos, blockState);
    }

    @Override
    public Component getDefaultName() {
        return Component.translatable("block.hauntedharvest.candy_bag");
    }

    @Override
    public boolean needsToUpdateClientWhenChanged() {
        return false;
    }
}
