package net.mehvahdjukaar.hauntedharvest.client;


import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.mojang.blaze3d.systems.RenderSystem;
import net.mehvahdjukaar.hauntedharvest.HauntedHarvest;
import net.mehvahdjukaar.hauntedharvest.blocks.ModCarvedPumpkinBlockTile;
import net.mehvahdjukaar.hauntedharvest.blocks.PumpkinType;
import net.mehvahdjukaar.moonlight.api.client.texture_renderer.FrameBufferBackedDynamicTexture;
import net.mehvahdjukaar.moonlight.api.client.texture_renderer.RenderedTexturesManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
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
        public static final int WIDTH = 16;

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
            PumpkinTextureGenerator.drawCarving(texture, this);
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

        @Nullable
        public ResourceLocation getPumpkinBlur() {
            return getCachedBlurTexture(this);
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


    @Nullable
    public static ResourceLocation getCachedBlurTexture(Carving carving) {
        if (pumpkinBlur == null) {
            RenderedTexturesManager.requestTexture(
                    HauntedHarvest.res("pumpkinblur"), 512,
                    t -> {
                        PumpkinTextureGenerator.drawBlur(t, carving);
                        pumpkinBlur = t;
                    }, false);
            return null;
        } else if (carving != currentCarvingBlur) {
            PumpkinTextureGenerator.drawBlur(pumpkinBlur, carving);
        }
        currentCarvingBlur = carving;
        return pumpkinBlur.getTextureLocation();
    }

    //no need to register a bunch of these just having one since theres only one player
    private static Carving currentCarvingBlur = null;
    private static FrameBufferBackedDynamicTexture pumpkinBlur = null;


}

