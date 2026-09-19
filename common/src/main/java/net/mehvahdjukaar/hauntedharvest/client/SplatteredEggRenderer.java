package net.mehvahdjukaar.hauntedharvest.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.mehvahdjukaar.hauntedharvest.HauntedHarvest;
import net.mehvahdjukaar.hauntedharvest.entity.SplatteredEggEntity;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;

public class SplatteredEggRenderer extends EntityRenderer<SplatteredEggEntity, SplatteredEggRenderer.State> {

    public static final Identifier TEXTURE = HauntedHarvest.res("textures/entity/egg/splattered_egg.png");
    public static final Identifier TEXTURE_2 = HauntedHarvest.res("textures/entity/egg/splattered_egg_2.png");

    public SplatteredEggRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    public static class State extends EntityRenderState {
        private Identifier texture = TEXTURE;
        private Direction direction = Direction.NORTH;
        private float yRot;
        private int splatLight;
    }

    @Override
    public State createRenderState() {
        return new State();
    }

    @Override
    public void extractRenderState(SplatteredEggEntity entity, State state, float partialTicks) {
        super.extractRenderState(entity, state, partialTicks);
        state.texture = entity.altTexture ? TEXTURE_2 : TEXTURE;
        state.direction = entity.getDirection();
        state.yRot = entity.getYRot();

        int blockX = entity.getBlockX();
        int blockY = entity.getBlockY();
        int blockZ = entity.getBlockZ();
        switch (state.direction.getAxis()) {
            case X -> blockZ = Mth.floor(entity.getZ());
            case Z -> blockX = Mth.floor(entity.getX());
            case Y -> blockY = Mth.floor(entity.getY());
        }
        state.splatLight = LevelRenderer.getLightCoords(entity.level(), new BlockPos(blockX, blockY, blockZ));
    }

    @Override
    public void submit(State state, PoseStack poseStack, SubmitNodeCollector collector, CameraRenderState camera) {
        poseStack.pushPose();
        poseStack.mulPose(Axis.YP.rotationDegrees(180.0F - state.yRot));

        poseStack.scale(0.0625F, 0.0625F, 0.0625F);
        collector.submitCustomGeometry(poseStack, RenderTypes.entityCutout(state.texture),
                (pose, buffer) -> this.renderPainting(pose, buffer, state.direction, state.splatLight));
        poseStack.popPose();
        super.submit(state, poseStack, collector, camera);
    }

    private void renderPainting(PoseStack.Pose last, VertexConsumer consumer, Direction dir, int l1) {
        float p = 8;
        float n = -8;
        if (dir == Direction.DOWN) {
            this.vertex(last, consumer, p, -0.5f, 0, 1, n, 0, -1, 0, l1);
            this.vertex(last, consumer, n, -0.5f, 1, 1, n, 0, -1, 0, l1);
            this.vertex(last, consumer, n, -0.5f, 1, 0, p, 0, -1, 0, l1);
            this.vertex(last, consumer, p, -0.5f, 0, 0, p, 0, -1, 0, l1);
        } else if (dir == Direction.UP) {
            this.vertex(last, consumer, n, 0.5f, 0, 1, p, 0, 1, 0, l1);
            this.vertex(last, consumer, n, 0.5f, 1, 1, n, 0, 1, 0, l1);
            this.vertex(last, consumer, p, 0.5f, 1, 0, n, 0, 1, 0, l1);
            this.vertex(last, consumer, p, 0.5f, 0, 0, p, 0, 1, 0, l1);
        } else {
            this.vertex(last, consumer, p, n, 0, 1, -0.5F, 0, 0, 1, l1);
            this.vertex(last, consumer, n, n, 1, 1, -0.5F, 0, 0, 1, l1);
            this.vertex(last, consumer, n, p, 1, 0, -0.5F, 0, 0, 1, l1);
            this.vertex(last, consumer, p, p, 0, 0, -0.5F, 0, 0, 1, l1);
        }
    }

    private void vertex(PoseStack.Pose pose, VertexConsumer vertexConsumer, float x, float y,
                        float u, float v, float z, int nx, int ny, int nz, int light) {
        vertexConsumer.addVertex(pose, x, y, z).setColor(-1)
                .setUv(u, v).setOverlay(OverlayTexture.NO_OVERLAY)
                .setLight(light).setNormal(pose, nx, ny, nz);
    }
}