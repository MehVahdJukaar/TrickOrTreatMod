package net.mehvahdjukaar.hauntedharvest.client;


import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
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
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;


public class CarvedPumpkinItemRenderer extends ItemStackRenderer {

    private static final BlockState LANTERN_STATE = ModRegistry.MOD_JACK_O_LANTERN.get().defaultBlockState();
    private static final BlockState PUMPKIN_STATE = ModRegistry.MOD_CARVED_PUMPKIN.get().defaultBlockState();


    @Override
    public void renderByItem(ItemStack stack, ItemTransforms.TransformType transformType, PoseStack matrixStackIn, MultiBufferSource bufferIn, int combinedLightIn, int combinedOverlayIn) {

        matrixStackIn.pushPose();

        BlockRenderDispatcher blockRenderer = Minecraft.getInstance().getBlockRenderer();
        boolean lit;
        if (stack.getItem() == ModRegistry.MOD_JACK_O_LANTERN_ITEM.get()) {
            lit = true;
            var model = ClientPlatformHelper.getModel(blockRenderer.getBlockModelShaper().getModelManager(),
                    ClientRegistry.JACK_O_LANTERN_FRAME);
            blockRenderer.getModelRenderer().renderModel(matrixStackIn.last(), bufferIn.getBuffer(ItemBlockRenderTypes.getRenderType(LANTERN_STATE, false)),
                    LANTERN_STATE, model, 1, 1, 1, combinedLightIn, combinedOverlayIn);
        } else {
            lit = false;
            var model = ClientPlatformHelper.getModel(blockRenderer.getBlockModelShaper().getModelManager(),
                    ClientRegistry.PUMPKIN_FRAME);
            blockRenderer.getModelRenderer().renderModel(matrixStackIn.last(), bufferIn.getBuffer(ItemBlockRenderTypes.getRenderType(PUMPKIN_STATE, false)),
                    PUMPKIN_STATE, model, 1, 1, 1, combinedLightIn, combinedOverlayIn);
        }

        CompoundTag com = stack.getTagElement("BlockEntityTag");
        long[] packed = new long[4];
        if (com != null && com.contains("Pixels")) {
            packed = com.getLongArray("Pixels");
        }
        var carving = CarvingManager.getInstance(CarvingManager.Key.of(packed, lit));
        VertexConsumer builder = bufferIn.getBuffer(carving.getRenderType());

        int lu = combinedLightIn & '\uffff';
        int lv = combinedLightIn >> 16 & '\uffff';

        matrixStackIn.mulPose(RotHlpr.Y180);
        matrixStackIn.translate(-1, 0, 0);
        CarvedPumpkinTileRenderer.addQuadSide(builder, matrixStackIn, 0, 0, 0, 1, 1, 0, 0, 0, 1, 1, 1, 1, 1, 1, lu, lv, 0, 0, 1);

        matrixStackIn.popPose();
    }
}