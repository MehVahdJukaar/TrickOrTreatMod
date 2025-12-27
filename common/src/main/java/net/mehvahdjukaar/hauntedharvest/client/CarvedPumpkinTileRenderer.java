package net.mehvahdjukaar.hauntedharvest.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.mehvahdjukaar.hauntedharvest.blocks.ModCarvedPumpkinBlock;
import net.mehvahdjukaar.hauntedharvest.blocks.ModCarvedPumpkinBlockTile;
import net.mehvahdjukaar.hauntedharvest.reg.ClientRegistry;
import net.mehvahdjukaar.moonlight.api.client.util.LOD;
import net.mehvahdjukaar.moonlight.api.client.util.RotHlpr;
import net.mehvahdjukaar.moonlight.api.client.util.VertexUtil;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;


public class CarvedPumpkinTileRenderer implements BlockEntityRenderer<ModCarvedPumpkinBlockTile> {

    private static final int WIDTH = 6;


    public CarvedPumpkinTileRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public int getViewDistance() {
        return 8;
    }

    @Override
    public boolean shouldRender(ModCarvedPumpkinBlockTile blockEntity, Vec3 cameraPos) {
        return BlockEntityRenderer.super.shouldRender(blockEntity, cameraPos);
    }

    @Override
    public void render(ModCarvedPumpkinBlockTile tile, float partialTicks, PoseStack matrixStackIn, MultiBufferSource bufferIn, int combinedLightIn,
                       int combinedOverlayIn) {

        if (!tile.getCarveMode().canManualDraw()) return;

        Direction dir = tile.getDirection();
        Minecraft mc = Minecraft.getInstance();
        LOD lod = LOD.at(tile);
        BlockPos pos = tile.getBlockPos();
        if (lod.isPlaneCulled(dir, WIDTH / 16f)) return;


        HitResult hit = mc.hitResult;
        if (hit != null && hit.getType() == HitResult.Type.BLOCK) {
            BlockHitResult blockHit = (BlockHitResult) hit;
            if (blockHit.getBlockPos().equals(pos) && tile.getDirection() == blockHit.getDirection()) {
                Player player = mc.player;
                if (player != null && tile.getLevel() != null) {
                    if (ModCarvedPumpkinBlock.isCarverItem(player.getMainHandItem())) {

                        matrixStackIn.pushPose();
                        matrixStackIn.translate(0.5, 0.5, 0.5);
                        matrixStackIn.mulPose(RotHlpr.rot(dir));
                        matrixStackIn.mulPose(RotHlpr.XN90);
                        matrixStackIn.translate(-0.5, -0.5, 0.5);

                        int frontLight = LevelRenderer.getLightColor(tile.getLevel(), pos.relative(dir));

                        int lu = frontLight & '\uffff';
                        int lv = frontLight >> 16 & '\uffff';

                        var pair = ModCarvedPumpkinBlock.getHitSubPixel(blockHit);
                        float p = 1 / 16f;
                        float x = pair.x() * p;
                        float y = pair.y() * p;
                        VertexConsumer builder2 = ClientRegistry.CARVING_OUTLINE.buffer(bufferIn, RenderType::entityCutout);
                        matrixStackIn.pushPose();

                        matrixStackIn.translate(x, 1 - y - p, 0.001);
                        VertexUtil.addQuad(builder2, matrixStackIn, 0, 0,  p, p, lu, lv);

                        matrixStackIn.popPose();

                        matrixStackIn.popPose();
                    }
                }
            }
        }
    }

}