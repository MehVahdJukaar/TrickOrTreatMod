package net.mehvahdjukaar.hauntedharvest.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Matrix3f;
import com.mojang.math.Matrix4f;
import com.mojang.math.Vector3f;
import net.mehvahdjukaar.hauntedharvest.HauntedHarvest;
import net.mehvahdjukaar.hauntedharvest.entity.SplatteredEggEntity;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public class SplatteredEggRenderer extends EntityRenderer<SplatteredEggEntity> {

    public static final ResourceLocation TEXTURE = HauntedHarvest.res("textures/entity/egg/splattered_egg.png");
    public static final ResourceLocation TEXTURE_2 = HauntedHarvest.res("textures/entity/egg/splattered_egg_2.png");

    public SplatteredEggRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public void render(SplatteredEggEntity pEntity, float pEntityYaw, float pPartialTicks, PoseStack pMatrixStack, MultiBufferSource pBuffer, int pPackedLight) {
        pMatrixStack.pushPose();
        pMatrixStack.mulPose(Vector3f.YP.rotationDegrees(180.0F - pEntityYaw));

        pMatrixStack.scale(0.0625F, 0.0625F, 0.0625F);
        VertexConsumer vertexconsumer = pBuffer.getBuffer(RenderType.entityCutoutNoCull(this.getTextureLocation(pEntity)));

        this.renderPainting(pMatrixStack, vertexconsumer, pEntity);
        pMatrixStack.popPose();
        super.render(pEntity, pEntityYaw, pPartialTicks, pMatrixStack, pBuffer, pPackedLight);
    }

    /**
     * Returns the location of an entity's texture.
     */
    @Override
    public ResourceLocation getTextureLocation(SplatteredEggEntity pEntity) {
        return pEntity.altTexture ? TEXTURE_2 : TEXTURE;
    }

    private void renderPainting(PoseStack poseStack, VertexConsumer consumer, SplatteredEggEntity entity) {

        PoseStack.Pose last = poseStack.last();
        Matrix4f matrix4f = last.pose();
        Matrix3f matrix3f = last.normal();

        float p = 8;
        float n = -8;
        int blockX = entity.getBlockX();
        int blockY = entity.getBlockY();
        int blockZ = entity.getBlockZ();
        Direction dir = entity.getDirection();
        switch (dir.getAxis()) {
            case X -> blockZ = Mth.floor(entity.getZ());
            case Z -> blockX = Mth.floor(entity.getX());
            case Y -> blockY = Mth.floor(entity.getY());
        }

        int l1 = LevelRenderer.getLightColor(entity.level, new BlockPos(blockX, blockY, blockZ));

        if (dir == Direction.DOWN) {
            this.vertex(matrix4f, matrix3f, consumer, p, -0.5f, 0, 1, n, 0, 0, -1, l1);
            this.vertex(matrix4f, matrix3f, consumer, n, -0.5f, 1, 1, n, 0, 0, -1, l1);
            this.vertex(matrix4f, matrix3f, consumer, n, -0.5f, 1, 0, p, 0, 0, -1, l1);
            this.vertex(matrix4f, matrix3f, consumer, p, -0.5f, 0, 0, p, 0, 0, -1, l1);
        } else if (dir == Direction.UP) {
            this.vertex(matrix4f, matrix3f, consumer, n, 0.5f, 0, 1, p, 0, 0, -1, l1);
            this.vertex(matrix4f, matrix3f, consumer, n, 0.5f, 1, 1, n, 0, 0, -1, l1);
            this.vertex(matrix4f, matrix3f, consumer, p, 0.5f, 1, 0, n, 0, 0, -1, l1);
            this.vertex(matrix4f, matrix3f, consumer, p, 0.5f, 0, 0, p, 0, 0, -1, l1);
        } else {
            this.vertex(matrix4f, matrix3f, consumer, p, n, 0, 1, -0.5F, 0, 0, -1, l1);
            this.vertex(matrix4f, matrix3f, consumer, n, n, 1, 1, -0.5F, 0, 0, -1, l1);
            this.vertex(matrix4f, matrix3f, consumer, n, p, 1, 0, -0.5F, 0, 0, -1, l1);
            this.vertex(matrix4f, matrix3f, consumer, p, p, 0, 0, -0.5F, 0, 0, -1, l1);
        }
    }

    private void vertex(Matrix4f matrix4f, Matrix3f matrix3f, VertexConsumer vertexConsumer, float x, float y,
                        float u, float v, float z, int nx, int ny, int nz, int light) {
        vertexConsumer.vertex(matrix4f, x, y, z).color(255, 255, 255, 255)
                .uv(u, v).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(matrix3f, (float) nx, (float) ny, (float) nz).endVertex();
    }
}