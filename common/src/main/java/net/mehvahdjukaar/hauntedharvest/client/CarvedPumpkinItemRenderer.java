package net.mehvahdjukaar.hauntedharvest.client;


import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.mehvahdjukaar.hauntedharvest.blocks.ModCarvedPumpkinBlock;
import net.mehvahdjukaar.hauntedharvest.blocks.PumpkinType;
import net.mehvahdjukaar.hauntedharvest.items.components.PumpkinCarvingData;
import net.mehvahdjukaar.hauntedharvest.reg.ClientRegistry;
import net.mehvahdjukaar.hauntedharvest.reg.ModRegistry;
import net.mehvahdjukaar.moonlight.api.client.ItemRenderExtension;
import net.mehvahdjukaar.moonlight.api.client.ItemStackRenderer;
import net.mehvahdjukaar.moonlight.api.client.util.RotHlpr;
import net.mehvahdjukaar.moonlight.api.client.util.VertexUtil;
import net.mehvahdjukaar.moonlight.api.platform.ClientHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;


public class CarvedPumpkinItemRenderer extends ItemStackRenderer implements ItemRenderExtension {

    @Override
    public ItemStackRenderer getItemRenderer() {
        return this;
    }

    @Override
    public void renderByItem(ItemStack stack, ItemDisplayContext context, PoseStack matrixStackIn, MultiBufferSource bufferIn, int combinedLightIn, int combinedOverlayIn) {

        matrixStackIn.pushPose();

        BlockRenderDispatcher blockRenderer = Minecraft.getInstance().getBlockRenderer();

        PumpkinCarvingData carvingData = stack.get(ModRegistry.PUMPKIN_CARVING.get());
        var visuals = CarvingManager.getInstance(carvingData);

        ModCarvedPumpkinBlock block = (ModCarvedPumpkinBlock) ((BlockItem) stack.getItem()).getBlock();
        BlockState state = block.defaultBlockState();
        PumpkinType type = block.getType(state);
        ModelResourceLocation frame = ClientRegistry.getPumpkinFrame(type);

        BakedModel model = ClientHelper.getModel(blockRenderer.getBlockModelShaper().getModelManager(), frame);
        blockRenderer.getModelRenderer().renderModel(matrixStackIn.last(), bufferIn.getBuffer(ItemBlockRenderTypes.getRenderType(state, false)),
                state, model, 1, 1, 1, combinedLightIn, combinedOverlayIn);

        VertexConsumer builder = bufferIn.getBuffer(visuals.getRenderType());

        int lu = combinedLightIn & '\uffff';
        int lv = combinedLightIn >> 16 & '\uffff';

        matrixStackIn.mulPose(RotHlpr.Y180);
        matrixStackIn.translate(-1, 0, 0);
        VertexUtil.addQuad(builder, matrixStackIn, 0, 0, 1, 1, lu, lv);

        matrixStackIn.popPose();
    }

    public void renderHelmetOverlay(ItemStack stack, Player player, int width, int height, float partialTick) {
        BlurOverlay.renderPumpkin(stack, width, height);
    }


}