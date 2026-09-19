package net.mehvahdjukaar.hauntedharvest.client.model;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.mehvahdjukaar.hauntedharvest.blocks.ModCarvedPumpkinBlock;
import net.mehvahdjukaar.hauntedharvest.blocks.ModCarvedPumpkinBlockTile;
import net.mehvahdjukaar.hauntedharvest.client.CarvingManager;
import net.mehvahdjukaar.hauntedharvest.client.PumpkinTextureGenerator;
import net.mehvahdjukaar.hauntedharvest.items.components.PumpkinCarvingData;
import net.mehvahdjukaar.moonlight.api.client.model.*;
import net.minecraft.client.renderer.block.BlockAndTintGetter;
import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.ResolvableModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;

public class CarvedPumpkinBlockModel implements CustomBlockModel {

    private final BlockStateModel back;

    public CarvedPumpkinBlockModel(BlockStateModel back) {
        this.back = back;
    }

    @Override
    public void emitQuads(QuadEmitter emitter, @Nullable BlockAndTintGetter level, @Nullable BlockPos pos,
                          @Nullable BlockState state, RandomSource random, ExtraModelData data) {
        emitter.emitAll(this.back, level, pos, state, random);

        if (state == null) return;
        PumpkinCarvingData key = data.get(ModCarvedPumpkinBlockTile.CARVING);
        if (key == null) return;
        var carving = CarvingManager.getInstance(key);
        emitter.emitAll(carving.getOrCreateQuads(state.getValue(ModCarvedPumpkinBlock.FACING),
                CarvedPumpkinBlockModel::generateQuads));
    }

    @Override
    public TextureAtlasSprite getParticle(ExtraModelData data) {
        return this.back.particleMaterial().sprite();
    }

    @Override
    public @Nullable Object geometryKey(BlockAndTintGetter level, BlockPos pos, BlockState state,
                                        RandomSource random, ExtraModelData data) {
        return new Key(data.get(ModCarvedPumpkinBlockTile.CARVING), state);
    }

    private record Key(@Nullable PumpkinCarvingData carving, BlockState state) {
    }

    //merges runs of same colored pixels into one quad per run
    private static QuadBatch generateQuads(CarvingManager.CarvingVisuals carving, Direction direction) {
        TextureAtlasSprite[][] pixels = PumpkinTextureGenerator.computePixelSpriteMap(carving.getPixels(), carving.getType());
        QuadBatch.Recorder recorder = QuadBatch.recorder();
        float yRot = direction.getOpposite().toYRot() * Mth.DEG_TO_RAD;
        recorder.transform(new Matrix4f().translate(0.5f, 0.5f, 0.5f).rotateY(-yRot).translate(-0.5f, -0.5f, -0.5f));

        for (int x = 0; x < pixels.length; x++) {
            int length = 0;
            int startY = 0;
            TextureAtlasSprite prevColor = pixels[0][x];
            for (int y = 0; y <= pixels[x].length; y++) {
                TextureAtlasSprite current = null;
                if (y < pixels[x].length) {
                    TextureAtlasSprite b = pixels[x][y];
                    if (prevColor == b) {
                        length++;
                        continue;
                    }
                    current = b;
                }
                emitPixelQuad(recorder, direction, (15 - x) / 16f, (16 - length - startY) / 16f,
                        1 / 16f, length / 16f, prevColor);
                startY = y;
                if (current != null) {
                    prevColor = current;
                }
                length = 1;
            }
        }
        return recorder.build();
    }

    private static void emitPixelQuad(QuadEmitter emitter, Direction facing, float x, float y, float width, float height,
                                      TextureAtlasSprite sprite) {
        float u0 = 1 - x;
        float v0 = 1 - y;
        float u1 = 1 - (x + width);
        float v1 = 1 - (y + height);

        emitter.sprite(sprite)
                .cullFace(facing)
                .direction(facing)
                .color(-1);
        putVertex(emitter, 0, x + width, y + height, u1, v1);
        putVertex(emitter, 1, x + width, y, u1, v0);
        putVertex(emitter, 2, x, y, u0, v0);
        putVertex(emitter, 3, x, y + height, u0, v1);
        emitter.emit();
    }

    private static void putVertex(QuadEmitter emitter, int vertex, float x, float y, float u, float v) {
        //round to the pixel grid, floats that are close to but not exactly 0 shade wrong
        emitter.pos(vertex, Math.round(x * 16) / 16f, Math.round(y * 16) / 16f, 0);
        emitter.uv(vertex, u, v);
        emitter.normal(vertex, 0, 0, -1);
    }

    public record Unbaked(BlockStateModel.Unbaked model) implements CustomUnbakedModel {

        public static final MapCodec<Unbaked> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
                BlockStateModel.Unbaked.CODEC.fieldOf("model").forGetter(Unbaked::model)
        ).apply(i, Unbaked::new));

        @Override
        public CustomBlockModel bake(ModelBaker baker) {
            return new CarvedPumpkinBlockModel(this.model.bake(baker));
        }

        @Override
        public MapCodec<? extends CustomUnbakedModel> codec() {
            return CODEC;
        }

        @Override
        public void resolveDependencies(ResolvableModel.Resolver resolver) {
            this.model.resolveDependencies(resolver);
        }
    }
}
