package net.mehvahdjukaar.hauntedharvest.client;


import net.mehvahdjukaar.moonlight.api.client.ItemRenderExtension;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;


public class PaperBagRenderExtension implements ItemRenderExtension {

    @Override
    public void renderHelmetOverlay(ItemStack stack, Player player, int width, int height, float partialTick) {
        BlurOverlay.renderPaperBag(stack, width, height);
    }


}