package net.mehvahdjukaar.hauntedharvest.integration.forge;

import net.mehvahdjukaar.hauntedharvest.blocks.AbstractCornBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.data.worldgen.ProcessorLists;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.templatesystem.RuleTest;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import vazkii.quark.api.event.SimpleHarvestEvent;
import vazkii.quark.content.tweaks.module.SimpleHarvestModule;

public class QuarkCompatImpl {
    public static InteractionResult triggerSimpleHarvest(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand) {
        return InteractionResult.PASS;
    }

    public static void init() {
        MinecraftForge.EVENT_BUS.register(QuarkCompatImpl.class);
    }

    @SubscribeEvent
    public static void onSimpleHarvest(SimpleHarvestEvent event) {
        Block b = event.blockState.getBlock();
        if (b instanceof AbstractCornBlock c) {
            if (!c.isPlantFullyGrown(event.blockState, event.pos, event.player.level)) {
                event.setCanceled(true);
            } else event.setTargetPos(event.pos.below(c.getHeight()));
        }
    }
}
