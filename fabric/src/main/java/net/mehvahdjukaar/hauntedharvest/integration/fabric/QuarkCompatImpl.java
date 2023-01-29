package net.mehvahdjukaar.hauntedharvest.integration.fabric;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class QuarkCompatImpl {
    public static InteractionResult triggerSimpleHarvest(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand) {
        return InteractionResult.PASS;
    }

    public static void init() {
    }
}
