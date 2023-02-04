package net.mehvahdjukaar.hauntedharvest.client;


import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.mehvahdjukaar.hauntedharvest.blocks.ModCarvedPumpkinBlock;
import net.mehvahdjukaar.hauntedharvest.blocks.PumpkinType;
import net.mehvahdjukaar.hauntedharvest.reg.ClientRegistry;
import net.mehvahdjukaar.hauntedharvest.reg.ModRegistry;
import net.mehvahdjukaar.moonlight.api.client.ItemStackRenderer;
import net.mehvahdjukaar.moonlight.api.client.util.RotHlpr;
import net.mehvahdjukaar.moonlight.api.platform.ClientPlatformHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;


public class CarvedPumpkinItemRenderer extends ItemStackRenderer {

    @Override
    public void renderByItem(ItemStack stack, ItemTransforms.TransformType transformType, PoseStack matrixStackIn, MultiBufferSource bufferIn, int combinedLightIn, int combinedOverlayIn) {

        matrixStackIn.pushPose();

        BlockRenderDispatcher blockRenderer = Minecraft.getInstance().getBlockRenderer();

        CompoundTag com = stack.getTagElement("BlockEntityTag");
        long[] packed = new long[4];
        if (com != null){
            var p = com.getLongArray("Pixels");
            if(p != null) packed = p;
        }
        ModCarvedPumpkinBlock block = (ModCarvedPumpkinBlock)((BlockItem)stack.getItem()).getBlock();
        BlockState state = block.defaultBlockState();
        PumpkinType type = block.getType(state);
        ResourceLocation frame = ClientRegistry.getFrame(type);

        var model = ClientPlatformHelper.getModel(blockRenderer.getBlockModelShaper().getModelManager(), frame);
        blockRenderer.getModelRenderer().renderModel(matrixStackIn.last(), bufferIn.getBuffer(ItemBlockRenderTypes.getRenderType(state, false)),
                state, model, 1, 1, 1, combinedLightIn, combinedOverlayIn);

        var carving = CarvingManager.getInstance(CarvingManager.Key.of(packed, type));
        VertexConsumer builder = bufferIn.getBuffer(carving.getRenderType());

        int lu = combinedLightIn & '\uffff';
        int lv = combinedLightIn >> 16 & '\uffff';

        matrixStackIn.mulPose(RotHlpr.Y180);
        matrixStackIn.translate(-1, 0, 0);
        CarvedPumpkinTileRenderer.addQuadSide(builder, matrixStackIn, 0, 0, 0, 1, 1, 0, 0, 0, 1, 1, 1, 1, 1, 1, lu, lv, 0, 0, 1);

        matrixStackIn.popPose();
    }
}