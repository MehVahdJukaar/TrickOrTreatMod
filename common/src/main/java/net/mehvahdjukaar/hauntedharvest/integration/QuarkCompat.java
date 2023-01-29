package net.mehvahdjukaar.hauntedharvest.integration;

import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class QuarkCompat {

    @ExpectPlatform
    public static void init(){
        throw new ArrayStoreException();
    }

    @ExpectPlatform
    public static InteractionResult triggerSimpleHarvest(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand) {
        throw new ArrayStoreException();
    }
}
