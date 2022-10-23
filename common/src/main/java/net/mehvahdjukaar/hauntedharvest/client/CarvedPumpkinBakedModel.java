package net.mehvahdjukaar.hauntedharvest.client;

import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.math.Transformation;
import com.mojang.math.Vector3f;
import net.mehvahdjukaar.hauntedharvest.blocks.ModCarvedPumpkinBlock;
import net.mehvahdjukaar.hauntedharvest.blocks.ModCarvedPumpkinBlockTile;
import net.mehvahdjukaar.moonlight.api.client.model.BakedQuadBuilder;
import net.mehvahdjukaar.moonlight.api.client.model.CustomBakedModel;
import net.mehvahdjukaar.moonlight.api.client.model.ExtraModelData;
import net.mehvahdjukaar.moonlight.api.client.util.RotHlpr;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class CarvedPumpkinBakedModel implements CustomBakedModel {

    private final Function<Material, TextureAtlasSprite> spriteGetter;
    private final ModelState modelTransform;

    private final BakedModel back;
    private final BlockModel owner;

    public CarvedPumpkinBakedModel(BlockModel owner, BakedModel baked, Function<Material, TextureAtlasSprite> spriteGetter,
                                   ModelState modelTransform) {
        this.back = baked;
        this.spriteGetter = spriteGetter;
        this.modelTransform = modelTransform;
        this.owner = owner;
    }

    @Override
    public boolean useAmbientOcclusion() {
        return true;
    }

    @Override
    public boolean isGui3d() {
        return false;
    }

    @Override
    public boolean usesBlockLight() {
        return false;
    }

    @Override
    public boolean isCustomRenderer() {
        return false;
    }

    @Override
    public TextureAtlasSprite getBlockParticle(ExtraModelData data) {
        return back.getParticleIcon();
    }

    @Override
    public ItemOverrides getOverrides() {
        return ItemOverrides.EMPTY;
    }

    @Override
    public ItemTransforms getTransforms() {
        return ItemTransforms.NO_TRANSFORMS;
    }

    @Override
    public List<BakedQuad> getBlockQuads(BlockState state, Direction side, RandomSource rand, RenderType renderType,
                                         ExtraModelData data) {
        List<BakedQuad> quads = new ArrayList<>(back.getQuads(state, side, rand));
        if (data != ExtraModelData.EMPTY && state != null && side == null) {
            Direction dir = state.getValue(ModCarvedPumpkinBlock.FACING);
            CarvingManager.CarvingKey key = data.get(ModCarvedPumpkinBlockTile.CARVING);
            if (key != null) {
                var textureInstance = CarvingManager.getCarvingInstance(key);
                quads.addAll(textureInstance.getOrCreateModel(dir, b -> generateQuads(b, this.modelTransform)));
            }
        }

        return quads;
    }

    private List<BakedQuad> generateQuads(boolean[][] px, ModelState modelTransform) {
        Material[][] pixels = PumpkinTextureGenerator.getTexturePerPixel(px);
        List<BakedQuad> quads;
        quads = new ArrayList<>();
        var rotation = modelTransform.getRotation();

        for (int x = 0; x < pixels.length; x++) {
            int length = 0;
            int startY = 0;
            Material prevColor = pixels[0][x];
            for (int y = 0; y <= pixels[x].length; y++) {
                Material current = null;
                if (y < pixels[x].length) {
                    Material b = pixels[x][y];
                    if (prevColor == b) {
                        length++;
                        continue;
                    }
                    current = b;
                }
                //block uv is incorrectly flipped on both axis... too bad
                //draws prev quad
                int tint = NativeImage.combine(255, 255, 255, 255);//255 << 24;
                TextureAtlasSprite sprite = spriteGetter.apply(prevColor);
                quads.add(createPixelQuad((15 - x) / 16f, (16 - length - startY) / 16f, 0,
                        1 / 16f, length / 16f, sprite, tint, rotation, false));
                startY = y;
                if (current != null) {
                    prevColor = current;
                }
                length = 1;
            }
        }
        return quads;
    }


    public static BakedQuad createPixelQuad(float x, float y, float z, float width, float height,
                                            TextureAtlasSprite sprite, int color, Transformation transform,
                                            boolean litUp) {
        Vector3f normal = new Vector3f(0, 0, 1);

        BakedQuadBuilder builder = BakedQuadBuilder.create();

        float tu = (1-(1+sprite.getWidth() * width));
        float tv = (1-(1+sprite.getHeight() * height));
        float u0 = (1-x) * 16;
        float v0 = (1-y) * 16;

        builder.setDirection(Direction.getNearest(normal.x(), normal.y(), normal.z()));
        builder.setSprite(sprite);

        putVertex(builder, normal, x + width, y + height, z,
                u0 + tu, v0 + tv, sprite, color, transform, litUp);
        putVertex(builder, normal, x + width, y, z,
                u0 + tu, v0, sprite, color, transform, litUp);
        putVertex(builder, normal, x, y, z,
                u0, v0, sprite, color, transform, litUp);
        putVertex(builder, normal, x, y + height, z,
                u0, v0 + tv, sprite, color, transform, litUp);

        return builder.build();
    }

    private static void putVertex(BakedQuadBuilder builder, Vector3f normal,
                                  float x, float y, float z, float u, float v,
                                  TextureAtlasSprite sprite, int color,
                                  Transformation transformation, boolean emissive) {

        Vector3f posV = RotHlpr.rotateVertexOnCenterBy(x, y, z, transformation.getMatrix());

        builder.pos(posV);

        builder.color(color);

        builder.uv(sprite.getU(u), sprite.getV(v));

        builder.normal(normal.x(), normal.y(), normal.z());

        if (emissive) builder.lightEmission(15);

        builder.endVertex();
    }


}


