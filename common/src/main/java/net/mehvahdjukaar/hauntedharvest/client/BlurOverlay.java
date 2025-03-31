package net.mehvahdjukaar.hauntedharvest.client;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.mehvahdjukaar.hauntedharvest.items.components.PumpkinCarvingData;
import net.mehvahdjukaar.hauntedharvest.reg.ClientRegistry;
import net.mehvahdjukaar.hauntedharvest.reg.ModRegistry;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

public class BlurOverlay {

    public static void renderPaperBag(ItemStack itemstack, int width, int height) {
        renderScreenOverlay(width, height, ClientRegistry.PAPER_BAG_OVERLAY);
    }

    public static void renderPumpkin(ItemStack itemstack, int width, int height) {
        PumpkinCarvingData data = itemstack.get(ModRegistry.PUMPKIN_CARVING.get());
        if (data == null) return;
        var carving = CarvingManager.getInstance(data);
        ResourceLocation textureLocation = carving.getPumpkinBlur();

        if (textureLocation == null) return;

        renderScreenOverlay(width, height, textureLocation);
    }

    private static void renderScreenOverlay(int width, int height, ResourceLocation textureLocation) {
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
    }

}
