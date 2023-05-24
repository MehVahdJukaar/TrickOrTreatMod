package net.mehvahdjukaar.hauntedharvest.client;


import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.mehvahdjukaar.hauntedharvest.HHPlatformStuff;
import net.mehvahdjukaar.hauntedharvest.HauntedHarvest;
import net.mehvahdjukaar.hauntedharvest.blocks.ModCarvedPumpkinBlockTile;
import net.mehvahdjukaar.hauntedharvest.blocks.PumpkinType;
import net.mehvahdjukaar.moonlight.api.client.texture_renderer.FrameBufferBackedDynamicTexture;
import net.mehvahdjukaar.moonlight.api.client.texture_renderer.RenderedTexturesManager;
import net.mehvahdjukaar.moonlight.api.platform.ClientHelper;
import net.mehvahdjukaar.moonlight.api.resources.textures.SpriteUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.resources.model.Material;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FastColor;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import org.jetbrains.annotations.Nullable;

import oshi.annotation.concurrent.Immutable;

import java.util.Arrays;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.BiFunction;

public class CarvingManager {

    private static final LoadingCache<Key, Carving> TEXTURE_CACHE = CacheBuilder.newBuilder()
            .expireAfterAccess(2, TimeUnit.MINUTES)
            .removalListener(i -> {
                Carving value = (Carving) i.getValue();
                if (value != null) {
                    RenderSystem.recordRenderCall(value::close);
                }
            })
            .build(new CacheLoader<>() {
                @Override
                public Carving load(Key key) {
                    return null;
                }
            });

    public static Carving getInstance(Key key) {
        Carving textureInstance = TEXTURE_CACHE.getIfPresent(key);
        if (textureInstance == null) {
            textureInstance = new Carving(ModCarvedPumpkinBlockTile.unpackPixels(key.values), key.type);
            TEXTURE_CACHE.put(key, textureInstance);
        }
        return textureInstance;
    }

    @Immutable
    public static class Key implements TooltipComponent {
        private final long[] values;
        private final PumpkinType type;

        Key(long[] packed, PumpkinType type) {
            this.values = packed;
            this.type = type;
        }

        public static Key of(long[] packPixels, PumpkinType glowing) {
            return new Key(packPixels, glowing);
        }

        public static Key of(long[] packPixels) {
            return new Key(packPixels, PumpkinType.NORMAL);
        }

        @Override
        public boolean equals(Object another) {
            if (another == this) {
                return true;
            }
            if (another == null) {
                return false;
            }
            if (another.getClass() != this.getClass()) {
                return false;
            }
            Key key = (Key) another;
            return Arrays.equals(this.values, key.values) && type == key.type;
        }

        @Override
        public int hashCode() {
            return Arrays.hashCode(this.values);
        }
    }


    public static class Carving implements AutoCloseable {
        private static final int WIDTH = 16;

        //models for each direction
        private final Map<Direction, List<BakedQuad>> quadsCache = new EnumMap<>(Direction.class);
        private final boolean[][] pixels;
        private final PumpkinType type;
        //he is lazy
        @Nullable
        private DynamicTexture texture;
        @Nullable
        private RenderType renderType;
        @Nullable
        private ResourceLocation textureLocation;

        private Carving(boolean[][] pixels, PumpkinType type) {
            this.pixels = pixels;
            this.type = type;
        }

        public boolean[][] getPixels() {
            return pixels;
        }

        public PumpkinType getType() {
            return type;
        }

        //cant initialize right away since this texture can be created from worked main tread during model bake since it needs getQuads

        private void initializeTexture() {
            this.texture = new DynamicTexture(WIDTH, WIDTH, false);

            Material[][] materials = PumpkinTextureGenerator.getTexturePerPixel(this.pixels, this.type);

            for (int y = 0; y < pixels.length && y < WIDTH; y++) {
                for (int x = 0; x < pixels[y].length && x < WIDTH; x++) { //getColoredPixel(BlackboardBlock.colorFromByte(pixels[x][y]),x,y)
                    int c = ClientHelper.getPixelRGBA(materials[x][y].sprite(), 0, x, y);

                    this.texture.getPixels().setPixelRGBA(x, y, c);
                }
            }
            this.texture.upload();

            //texture manager has its own internal id
            this.textureLocation = Minecraft.getInstance().getTextureManager().register("carving/", this.texture);
            this.renderType = RenderType.entitySolid(textureLocation);
        }


        public List<BakedQuad> getOrCreateModel(Direction dir, BiFunction<Carving, Direction, List<BakedQuad>> modelFactory) {
            return this.quadsCache.computeIfAbsent(dir, d -> modelFactory.apply(this, d));
        }

        public ResourceLocation getTextureLocation() {
            if (textureLocation == null) {
                //I can only initialize it here since this is guaranteed to be on render thread
                this.initializeTexture();
            }
            return textureLocation;
        }

        public ResourceLocation getPumpkinBlur() {
            return getBlurTexture(this);
        }

        public RenderType getRenderType() {
            if (renderType == null) {
                //I can only initialize it here since this is guaranteed to be on render thread
                this.initializeTexture();
            }
            return renderType;
        }

        //should be called when cache expires
        @Override
        public void close() {
            if (texture != null) this.texture.close();
            if (textureLocation != null) Minecraft.getInstance().getTextureManager().release(textureLocation);
        }
    }

    //no need to register a bunch of these just having one since theres only one player
    private static Carving currentCarvingBlur = null;
    private static FrameBufferBackedDynamicTexture pumpkinBlur = null;

    protected static ResourceLocation getBlurTexture(Carving carving) {
        if (pumpkinBlur == null) {
            pumpkinBlur = RenderedTexturesManager.requestTexture(HauntedHarvest.res("pumpkinblur"), 512,
                    t -> redrawBlur(carving.getPixels(), t), false);
        } else if (carving != currentCarvingBlur) {
            redrawBlur(carving.getPixels(), pumpkinBlur);
        }
        currentCarvingBlur = carving;
        return pumpkinBlur.getTextureLocation();
    }


    private static DynamicTexture dummy = null;
    private static ResourceLocation dummyLocation = null;

    private static void redrawBlur(boolean[][] pixels, FrameBufferBackedDynamicTexture t) {
        if (dummyLocation == null) {
            dummy = new DynamicTexture(18, 18, false);
            dummyLocation = Minecraft.getInstance().getTextureManager().register("carving/", dummy);
        }
        var p = dummy.getPixels();
        SpriteUtils.forEachPixel(p, (x, y) -> {
            int alpha = 0;
            if (x == 0 || x == 17 || y == 0 || y == 17 || !pixels[x - 1][y - 1]) {
                alpha = 255;
            }
            p.setPixelRGBA(x, y, FastColor.ABGR32.color(alpha, 0, 0, 0));
        });

        dummy.upload();
        dummy.setFilter(true, false);

        RenderedTexturesManager.drawAsInGUI(t, s -> {

            int width = 16;
            int height = 16;
            int blitOffset = 2000;

            float u0 = 1 / 18f;
            float u1 = 17 / 18f;

            RenderSystem.setShaderTexture(0, dummyLocation);

            var matrix = s.last().pose();

            RenderSystem.disableDepthTest();
            RenderSystem.depthMask(false);
            RenderSystem.disableBlend();

            RenderSystem.setShader(HHPlatformStuff::getBlur);
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1);

            BufferBuilder bufferBuilder = Tesselator.getInstance().getBuilder();
            bufferBuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
            bufferBuilder.vertex(matrix, 0.0f, height, blitOffset).uv(u0, u0).endVertex();
            bufferBuilder.vertex(matrix, width, height, blitOffset).uv(u1, u0).endVertex();
            bufferBuilder.vertex(matrix, width, 0.0f, blitOffset).uv(u1, u1).endVertex();
            bufferBuilder.vertex(matrix, 0.0f, 0.0f, blitOffset).uv(u0, u1).endVertex();
            BufferUploader.drawWithShader(bufferBuilder.end());
        });

        t.setFilter(true, false);
    }


}

