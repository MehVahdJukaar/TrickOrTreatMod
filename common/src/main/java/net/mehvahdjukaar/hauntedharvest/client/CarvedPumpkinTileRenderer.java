package net.mehvahdjukaar.hauntedharvest.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.datafixers.util.Pair;
import net.mehvahdjukaar.hauntedharvest.blocks.ModCarvedPumpkinBlock;
import net.mehvahdjukaar.hauntedharvest.blocks.ModCarvedPumpkinBlockTile;
import net.mehvahdjukaar.hauntedharvest.reg.ClientRegistry;
import net.mehvahdjukaar.hauntedharvest.reg.ModConfigs;
import net.mehvahdjukaar.moonlight.api.client.util.LOD;
import net.mehvahdjukaar.moonlight.api.client.util.RotHlpr;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
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

    private final Minecraft mc;
    private final Camera camera;

    public CarvedPumpkinTileRenderer(BlockEntityRendererProvider.Context context) {
        this.mc = Minecraft.getInstance();
        this.camera = this.mc.gameRenderer.getMainCamera();
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

        if (!ModConfigs.CARVE_MODE.get().canManualDraw()) return;

        Direction dir = tile.getDirection();
        float yaw = -dir.toYRot();

        Vec3 cameraPos = camera.getPosition();
        BlockPos pos = tile.getBlockPos();
        if (LOD.isOutOfFocus(cameraPos, pos, yaw, 0, dir, WIDTH / 16f)) return;

        matrixStackIn.pushPose();
        matrixStackIn.translate(0.5, 0.5, 0.5);
        matrixStackIn.mulPose(RotHlpr.rot(dir));
        matrixStackIn.mulPose(RotHlpr.XN90);
        matrixStackIn.translate(-0.5, -0.5, -0.1875);


        int lu = combinedLightIn & '\uffff';
        int lv = combinedLightIn >> 16 & '\uffff';

        HitResult hit = mc.hitResult;
        if (hit != null && hit.getType() == HitResult.Type.BLOCK) {
            BlockHitResult blockHit = (BlockHitResult) hit;
            if (blockHit.getBlockPos().equals(pos) && tile.getDirection() == blockHit.getDirection()) {
                Player player = mc.player;
                if (player != null) {
                    if (ModCarvedPumpkinBlock.isCarverItem(player.getMainHandItem())) {
                        Pair<Integer, Integer> pair = ModCarvedPumpkinBlock.getHitSubPixel(blockHit);
                        float p = 1 / 16f;
                        float x = pair.getFirst() * p;
                        float y = pair.getSecond() * p;
                        VertexConsumer builder2 = ClientRegistry.CARVING_OUTLINE.buffer(bufferIn, RenderType::entityCutout);
                        matrixStackIn.pushPose();

                        matrixStackIn.translate(x, 1 - y - p, 0.001);
                        addQuadSide(builder2, matrixStackIn, 0, 0, 0, p, p, 0, 0, 0, 1, 1,
                                1, 1, 1, 1, lu, lv, 0, 0, 1, ClientRegistry.CARVING_OUTLINE.sprite());
                        matrixStackIn.popPose();
                    }
                }
            }
        }
        // VertexConsumer builder = bufferIn.getBuffer(BlackboardTextureManager.INSTANCE.getBlackboardInstance(tile).getRenderType());
        // RendererUtil.addQuadSide(builder, matrixStackIn, 0, 0, 0, 1, 1, 0, 0, 0, 1, 1, 1, 1, 1, 1, lu, lv, 0, 0, 1);

        matrixStackIn.popPose();

    }


    public static void addQuadSide(VertexConsumer builder, PoseStack matrixStackIn, float x0, float y0, float z0, float x1, float y1, float z1, float u0, float v0, float u1, float v1, float r, float g,
                                   float b, float a, int lu, int lv, float nx, float ny, float nz, TextureAtlasSprite sprite) {

        u0 = getRelativeU(sprite, u0);
        u1 = getRelativeU(sprite, u1);
        v0 = getRelativeV(sprite, v0);
        v1 = getRelativeV(sprite, v1);

        addVert(builder, matrixStackIn, x0, y0, z0, u0, v1, r, g, b, a, lu, lv, nx, ny, nz);
        addVert(builder, matrixStackIn, x1, y0, z1, u1, v1, r, g, b, a, lu, lv, nx, ny, nz);
        addVert(builder, matrixStackIn, x1, y1, z1, u1, v0, r, g, b, a, lu, lv, nx, ny, nz);
        addVert(builder, matrixStackIn, x0, y1, z0, u0, v0, r, g, b, a, lu, lv, nx, ny, nz);
    }

    public static void addVert(VertexConsumer builder, PoseStack matrixStackIn, float x, float y, float z, float u, float v, float r, float g,
                               float b, float a, int lu, int lv, float nx, float ny, float nz) {
        builder.vertex(matrixStackIn.last().pose(), x, y, z).color(r, g, b, a).uv(u, v).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(lu, lv)
                .normal(matrixStackIn.last().normal(), nx, ny, nz).endVertex();
    }

    public static float getRelativeU(TextureAtlasSprite sprite, float u) {
        float f = sprite.getU1() - sprite.getU0();
        return sprite.getU0() + f * u;
    }

    public static float getRelativeV(TextureAtlasSprite sprite, float v) {
        float f = sprite.getV1() - sprite.getV0();
        return sprite.getV0() + f * v;
    }


}