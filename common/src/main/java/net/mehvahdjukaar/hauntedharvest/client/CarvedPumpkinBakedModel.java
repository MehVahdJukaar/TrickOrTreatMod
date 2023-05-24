package net.mehvahdjukaar.hauntedharvest.client;

import com.mojang.math.Transformation;
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
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import org.joml.Vector3f;

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
        if (data != ExtraModelData.EMPTY && state != null && side == state.getValue(ModCarvedPumpkinBlock.FACING)) {
            CarvingManager.Key key = data.get(ModCarvedPumpkinBlockTile.CARVING);
            if (key != null) {
                var carving = CarvingManager.getInstance(key);
                quads.addAll(carving.getOrCreateModel(side, this::generateQuads));
            }
        }

        return quads;
    }

    private List<BakedQuad> generateQuads(CarvingManager.Carving carving, Direction direction) {
        var px = carving.getPixels();
        var type = carving.getType();
        Material[][] pixels = PumpkinTextureGenerator.getTexturePerPixel(px, type);
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
                //draws prev quad
                int tint = -1;
                TextureAtlasSprite sprite = spriteGetter.apply(prevColor);

                quads.add(createPixelQuad((15 - x) / 16f, (16 - length - startY) / 16f, 0,
                        1 / 16f, length / 16f, sprite, tint, rotation, false, direction));
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
                                            boolean litUp, Direction dd) {
        var contents = sprite.contents();;
        float tu = (1 - (1 + contents.width() * width));
        float tv = (1 - (1 + contents.height() * height));
        float u0 = (1 - x) * 16;
        float v0 = (1 - y) * 16;

        //this just wraps around forge baked quad builder
        BakedQuadBuilder builder = BakedQuadBuilder.create();

        builder.setDirection(dd);
        Vector3f normal = new Vector3f(dd.getStepX(), dd.getStepY(), dd.getStepZ());

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
        //I hate this. Forge seems to have some rounding errors with numbers close to 0 that arent 0 resulting in incorrect shading
        posV.set(Math.round(posV.x() * 16) / 16f, Math.round(posV.y() * 16) / 16f, Math.round(posV.z() * 16) / 16f);

        builder.pos(posV);

        builder.color(color);

        builder.uv(sprite.getU(u), sprite.getV(v));

        builder.normal(normal.x(), normal.y(), normal.z());

        if (emissive) builder.lightEmission(15);

        builder.endVertex();
    }


}


