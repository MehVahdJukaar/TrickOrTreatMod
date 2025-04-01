package net.mehvahdjukaar.hauntedharvest.client;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.mehvahdjukaar.hauntedharvest.items.components.PumpkinCarvingData;
import net.mehvahdjukaar.hauntedharvest.reg.ClientRegistry;
import net.mehvahdjukaar.hauntedharvest.reg.ModRegistry;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.LayeredDraw;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class BlurOverlay {

    public static void renderPaperBag(ItemStack stack, Player player, GuiGraphics guiGraphics, DeltaTracker deltaTracker) {
        renderScreenOverlay(stack, player,guiGraphics, deltaTracker, ClientRegistry.PAPER_BAG_OVERLAY);
    }


    public static void renderPumpkin(ItemStack stack, Player player, GuiGraphics guiGraphics, DeltaTracker deltaTracker) {

        PumpkinCarvingData data = stack.get(ModRegistry.PUMPKIN_CARVING.get());
        if (data == null) return;
        var carving = CarvingManager.getInstance(data);
        ResourceLocation textureLocation = carving.getPumpkinBlur();

        if (textureLocation == null) return;

        renderScreenOverlay(stack, player, guiGraphics, deltaTracker, textureLocation);
    }

    private static void renderScreenOverlay(ItemStack stack, Player player, GuiGraphics graphics, DeltaTracker deltaTracker, ResourceLocation textureLocation) {
       /*
        RenderSystem.disableDepthTest();
        RenderSystem.depthMask(false);
        RenderSystem.defaultBlendFunc();

        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1);
        RenderSystem.setShaderTexture(0, textureLocation);
        Tesselator tesselator = Tesselator.getInstance();
        BufferBuilder bufferbuilder = tesselator.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
        bufferbuilder.addVertex(0, height, -90.0f).setUv(0.0F, 1.0F);
        bufferbuilder.addVertex(width, height, -90.0f).setUv(1.0F, 1.0F);
        bufferbuilder.addVertex(width, 0, -90.0f).setUv(1.0F, 0.0F);
        bufferbuilder.addVertex(0, 0, -90.0f).setUv(0.0F, 0.0F);
        RenderSystem.depthMask(true);
        RenderSystem.enableDepthTest();
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
*/

        RenderSystem.disableDepthTest();
        RenderSystem.depthMask(false);
        RenderSystem.enableBlend();
        graphics.setColor(1.0F, 1.0F, 1.0F, 1);
        graphics.blit(textureLocation, 0, 0, -90, 0.0F, 0.0F,
                graphics.guiWidth(), graphics.guiHeight(), graphics.guiWidth(), graphics.guiHeight());
        RenderSystem.disableBlend();
        RenderSystem.depthMask(true);
        RenderSystem.enableDepthTest();
        graphics.setColor(1.0F, 1.0F, 1.0F, 1.0F);
    }

}
