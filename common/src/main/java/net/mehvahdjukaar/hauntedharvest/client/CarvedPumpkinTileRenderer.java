package net.mehvahdjukaar.hauntedharvest.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.mehvahdjukaar.hauntedharvest.blocks.ModCarvedPumpkinBlock;
import net.mehvahdjukaar.hauntedharvest.blocks.ModCarvedPumpkinBlockTile;
import net.mehvahdjukaar.hauntedharvest.reg.ClientRegistry;
import net.mehvahdjukaar.moonlight.api.client.util.LOD;
import net.mehvahdjukaar.moonlight.api.client.util.RotHlpr;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector2i;


public class CarvedPumpkinTileRenderer implements BlockEntityRenderer<ModCarvedPumpkinBlockTile, CarvedPumpkinTileRenderer.State> {

    private static final int WIDTH = 6;


    public CarvedPumpkinTileRenderer(BlockEntityRendererProvider.Context context) {
    }

    public static class State extends BlockEntityRenderState {
        private boolean showsCarvingCursor;
        private Direction facing = Direction.NORTH;
        private int cursorX;
        private int cursorY;
        private int frontLight;
    }

    @Override
    public State createRenderState() {
        return new State();
    }

    @Override
    public int getViewDistance() {
        return 8;
    }

    @Override
    public void extractRenderState(ModCarvedPumpkinBlockTile tile, State state, float partialTicks, Vec3 cameraPos,
                                   ModelFeatureRenderer.@Nullable CrumblingOverlay breakProgress) {
        BlockEntityRenderer.super.extractRenderState(tile, state, partialTicks, cameraPos, breakProgress);
        state.showsCarvingCursor = false;

        if (!tile.getCarveMode().canManualDraw()) return;

        Direction dir = tile.getDirection();
        LOD lod = LOD.at(tile);
        if (lod.isPlaneCulled(dir, WIDTH / 16f)) return;

        Minecraft mc = Minecraft.getInstance();
        HitResult hit = mc.hitResult;
        if (hit != null && hit.getType() == HitResult.Type.BLOCK) {
            BlockHitResult blockHit = (BlockHitResult) hit;
            if (blockHit.getBlockPos().equals(tile.getBlockPos()) && dir == blockHit.getDirection()) {
                Player player = mc.player;
                if (player != null && tile.getLevel() != null && ModCarvedPumpkinBlock.isCarverItem(player.getMainHandItem())) {
                    Vector2i pixel = ModCarvedPumpkinBlock.getHitSubPixel(blockHit);
                    state.showsCarvingCursor = true;
                    state.facing = dir;
                    state.cursorX = pixel.x();
                    state.cursorY = pixel.y();
                    state.frontLight = LevelRenderer.getLightCoords(tile.getLevel(), tile.getBlockPos().relative(dir));
                }
            }
        }
    }

    @Override
    public void submit(State state, PoseStack poseStack, SubmitNodeCollector collector, CameraRenderState camera) {
        if (!state.showsCarvingCursor) return;

        poseStack.pushPose();
        poseStack.translate(0.5, 0.5, 0.5);
        //rot() already faces -Z towards dir, so we end up on the carved face with Y up
        poseStack.mulPose(RotHlpr.rot(state.facing));
        poseStack.translate(-0.5, -0.5, -0.5);

        float p = 1 / 16f;
        //same flip the baked model does, pixel 0 is on the opposite side of the local X axis
        float x = (15 - state.cursorX) * p;
        float y = state.cursorY * p;
        poseStack.translate(x, 1 - y - p, -0.001);

        TextureAtlasSprite outline = ClientRegistry.sprite(ClientRegistry.CARVING_OUTLINE);
        int light = state.frontLight;
        collector.submitCustomGeometry(poseStack, RenderTypes.entityCutoutCull(outline.atlasLocation()),
                (pose, buffer) -> addFlatQuad(pose, buffer, 0, 0, p, p,
                        outline.getU0(), outline.getV0(), outline.getU1(), outline.getV1(), light));

        poseStack.popPose();
    }

    public static void addFlatQuad(PoseStack.Pose pose, VertexConsumer buffer, float x0, float y0, float x1, float y1,
                                   float u0, float v0, float u1, float v1, int light) {
        addVertex(pose, buffer, x0, y1, u1, v0, light);
        addVertex(pose, buffer, x1, y1, u0, v0, light);
        addVertex(pose, buffer, x1, y0, u0, v1, light);
        addVertex(pose, buffer, x0, y0, u1, v1, light);
    }

    private static void addVertex(PoseStack.Pose pose, VertexConsumer buffer, float x, float y, float u, float v, int light) {
        buffer.addVertex(pose, x, y, 0).setColor(-1).setUv(u, v).setOverlay(OverlayTexture.NO_OVERLAY)
                .setLight(light).setNormal(pose, 0, 0, -1);
    }

}
