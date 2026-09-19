package net.mehvahdjukaar.hauntedharvest.client.screens;

import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.mehvahdjukaar.hauntedharvest.blocks.ModCarvedPumpkinBlock;
import net.mehvahdjukaar.hauntedharvest.client.CarvedPumpkinSpecialRenderer;
import net.mehvahdjukaar.hauntedharvest.items.components.PumpkinCarvingData;
import net.mehvahdjukaar.moonlight.api.client.util.RenderUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.gui.render.pip.PictureInPictureRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.feature.FeatureRenderDispatcher;
import net.minecraft.client.renderer.state.gui.pip.PictureInPictureRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.LightCoordsUtil;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix3x2f;

public class PumpkinShowcaseRenderer extends PictureInPictureRenderer<PumpkinShowcaseRenderer.State> {

    public record State(ModCarvedPumpkinBlock block, PumpkinCarvingData carving, float yaw, float pitch,
                        float bob, float wobble, int x0, int y0, int x1, int y1, float scale,
                        Matrix3x2f pose, @Nullable ScreenRectangle scissorArea)
            implements PictureInPictureRenderState {

        @Nullable
        @Override
        public ScreenRectangle bounds() {
            return PictureInPictureRenderState.getBounds(x0, y0, x1, y1, scissorArea);
        }
    }

    private final RandomSource random = RandomSource.create();

    public PumpkinShowcaseRenderer(MultiBufferSource.BufferSource bufferSource) {
        super(bufferSource);
    }

    @Override
    public Class<State> getRenderStateClass() {
        return State.class;
    }

    @Override
    protected String getTextureLabel() {
        return "pumpkin showcase";
    }

    @Override
    protected float getTranslateY(int height, int guiScale) {
        return height / 2f;
    }

    @Override
    protected void renderToTexture(State state, PoseStack poseStack) {
        Minecraft mc = Minecraft.getInstance();
        // same chain an item goes through in a slot: the negative y cancels out the gui projection's own flip, so
        // the quads keep their winding and the culled block render types don't turn inside out
        poseStack.scale(1, -1, -1);
        // hover, applied before the aiming so it stays a straight up and down bob and a screen space tilt no
        // matter where the pumpkin is looking
        poseStack.translate(0, state.bob(), 0);
        poseStack.mulPose(Axis.ZP.rotationDegrees(state.wobble()));
        poseStack.mulPose(Axis.XP.rotationDegrees(state.pitch()));
        // the carved face is on the block's north side, so 180 turns it to us
        poseStack.mulPose(Axis.YP.rotationDegrees(180 + state.yaw()));
        poseStack.translate(-0.5f, -0.5f, -0.5f); // the block renderer starts from the block corner

        mc.gameRenderer.getLighting().setupFor(Lighting.Entry.ITEMS_3D);

        FeatureRenderDispatcher dispatcher = mc.gameRenderer.getFeatureRenderDispatcher();
        var collector = dispatcher.getSubmitNodeStorage();
        BlockState blockState = state.block().defaultBlockState();
        this.random.setSeed(42);
        RenderUtil.submitBlockModel(poseStack, collector, RenderUtil.getBlockModel(blockState), null, null,
                blockState, this.random, true, LightCoordsUtil.FULL_BRIGHT, OverlayTexture.NO_OVERLAY);
        CarvedPumpkinSpecialRenderer.submitCarvedFace(state.carving(), poseStack, collector, LightCoordsUtil.FULL_BRIGHT);
        dispatcher.renderAllFeatures();
    }
}
