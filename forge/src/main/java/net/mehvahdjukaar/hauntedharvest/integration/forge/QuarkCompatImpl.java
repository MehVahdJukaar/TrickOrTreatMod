package net.mehvahdjukaar.hauntedharvest.integration.forge;

import net.mehvahdjukaar.hauntedharvest.blocks.AbstractCornBlock;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import vazkii.quark.api.event.SimpleHarvestEvent;

public class QuarkCompatImpl {
    
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
