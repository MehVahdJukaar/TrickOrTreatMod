package net.mehvahdjukaar.hauntedharvest.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.serialization.MapCodec;
import net.mehvahdjukaar.hauntedharvest.items.components.PumpkinCarvingData;
import net.mehvahdjukaar.hauntedharvest.reg.ModRegistry;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.special.SpecialModelRenderer;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;
import org.joml.Vector3fc;

import java.util.function.Consumer;

public class CarvedPumpkinSpecialRenderer implements SpecialModelRenderer<PumpkinCarvingData> {

    @Override
    public void submit(@Nullable PumpkinCarvingData carving, PoseStack poseStack, SubmitNodeCollector collector,
                       int lightCoords, int overlayCoords, boolean hasFoil, int outlineColor) {
        if (carving != null) submitCarvedFace(carving, poseStack, collector, lightCoords);
    }

    public static void submitCarvedFace(PumpkinCarvingData carving, PoseStack poseStack, SubmitNodeCollector collector,
                                        int light) {
        var visuals = CarvingManager.getInstance(carving);
        collector.submitCustomGeometry(poseStack, visuals.getRenderType(),
                (pose, buffer) -> CarvedPumpkinTileRenderer.addFlatQuad(pose, buffer, 0, 0, 1, 1, 0, 0, 1, 1, light));
    }

    @Override
    public void getExtents(Consumer<Vector3fc> output) {
        output.accept(new Vector3f(0, 0, 0));
        output.accept(new Vector3f(1, 0, 0));
        output.accept(new Vector3f(0, 1, 0));
        output.accept(new Vector3f(1, 1, 0));
    }

    @Override
    public @Nullable PumpkinCarvingData extractArgument(ItemStack stack) {
        return stack.get(ModRegistry.PUMPKIN_CARVING.get());
    }

    public record Unbaked() implements SpecialModelRenderer.Unbaked<PumpkinCarvingData> {

        public static final MapCodec<Unbaked> CODEC = MapCodec.unit(new Unbaked());

        @Override
        public SpecialModelRenderer<PumpkinCarvingData> bake(BakingContext context) {
            return new CarvedPumpkinSpecialRenderer();
        }

        @Override
        public MapCodec<Unbaked> type() {
            return CODEC;
        }
    }
}
