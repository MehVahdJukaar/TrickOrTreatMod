package net.mehvahdjukaar.hauntedharvest.client;


import net.mehvahdjukaar.moonlight.api.client.ItemRenderExtension;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;


public class PaperBagRenderExtension implements ItemRenderExtension {

    @Override
    public void renderHelmetOverlay(ItemStack stack, Player player, GuiGraphics guiGraphics, DeltaTracker deltaTracker) {
        BlurOverlay.renderPaperBag(stack, player, guiGraphics, deltaTracker);
    }


}