package net.mehvahdjukaar.hauntedharvest.client;

import com.mojang.math.Transformation;
import net.mehvahdjukaar.hauntedharvest.blocks.ModCarvedPumpkinBlock;
import net.mehvahdjukaar.hauntedharvest.blocks.ModCarvedPumpkinBlockTile;
import net.mehvahdjukaar.moonlight.api.client.model.BakedQuadBuilder;
import net.mehvahdjukaar.moonlight.api.client.model.CustomBakedModel;
import net.mehvahdjukaar.moonlight.api.client.model.ExtraModelData;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
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

public class CarvedPumpkinBakedModel implements CustomBakedModel {

    private final ModelState modelTransform;
    private final BakedModel back;

    public CarvedPumpkinBakedModel(BakedModel back, ModelState modelTransform) {
        this.back = back;
        this.modelTransform = modelTransform;
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
        Material[][] pixels = PumpkinTextureGenerator.computePixelMaterialMap(px, type);
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
                TextureAtlasSprite sprite = prevColor.sprite();

                quads.add(createPixelQuad((15 - x) / 16f, (16 - length - startY) / 16f, 0,
                        1 / 16f, length / 16f, sprite, rotation));
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
                                            TextureAtlasSprite sprite, Transformation transform) {

        float u0 = 1 - x;
        float v0 = 1 - y;
        float u1 = 1 - (x + width);
        float v1 = 1 - (y + height);

        BakedQuadBuilder builder = BakedQuadBuilder.create(sprite, transform);
        builder.setAutoDirection();

        putVertex(builder, x + width, y + height, z, u1, v1);
        putVertex(builder, x + width, y, z, u1, v0);
        putVertex(builder, x, y, z, u0, v0);
        putVertex(builder, x, y + height, z, u0, v1);

        return builder.build();
    }


    private static void putVertex(BakedQuadBuilder builder, float x, float y, float z, float u, float v) {

        Vector3f posV = new Vector3f(x, y, z);
        //I hate this. Forge seems to have some rounding errors with numbers close to 0 that arent 0 resulting in incorrect shading
        posV.set(Math.round(posV.x() * 16) / 16f, Math.round(posV.y() * 16) / 16f, Math.round(posV.z() * 16) / 16f);
        builder.vertex(posV.x, posV.y, posV.z);
        builder.color(-1);
        builder.uv(u, v);
        builder.normal(0, 0, -1);
        builder.endVertex();
    }


}


