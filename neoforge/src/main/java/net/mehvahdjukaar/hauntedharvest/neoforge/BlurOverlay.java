package net.mehvahdjukaar.hauntedharvest.neoforge;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.mehvahdjukaar.hauntedharvest.blocks.ModCarvedPumpkinBlock;
import net.mehvahdjukaar.hauntedharvest.client.CarvingManager;
import net.mehvahdjukaar.hauntedharvest.reg.ClientRegistry;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;

public class BlurOverlay {

    public static void renderPaperBag(ItemStack itemstack, int width, int height) {
        renderScreenOverlay(width, height, ClientRegistry.PAPER_BAG_OVERLAY);
    }

    public static void renderPumpkin(ItemStack itemstack, int width, int height) {
        CompoundTag com = itemstack.getTagElement("BlockEntityTag");
        long[] packed = new long[4];
        if (com != null && com.contains("Pixels")) {
            packed = com.getLongArray("Pixels");
        }
        ModCarvedPumpkinBlock block = (ModCarvedPumpkinBlock) ((BlockItem) itemstack.getItem()).getBlock();
        var carving = CarvingManager.getInstance(CarvingManager.Key.of(packed, block.getType(block.defaultBlockState())));
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
        BufferBuilder bufferbuilder = tesselator.getBuilder();
        bufferbuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
        bufferbuilder.vertex(0.0, height, -90.0).uv(0.0F, 1.0F).endVertex();
        bufferbuilder.vertex(width, height, -90.0).uv(1.0F, 1.0F).endVertex();
        bufferbuilder.vertex(width, 0.0, -90.0).uv(1.0F, 0.0F).endVertex();
        bufferbuilder.vertex(0.0, 0.0, -90.0).uv(0.0F, 0.0F).endVertex();
        tesselator.end();
        RenderSystem.depthMask(true);
        RenderSystem.enableDepthTest();
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
    }

}
