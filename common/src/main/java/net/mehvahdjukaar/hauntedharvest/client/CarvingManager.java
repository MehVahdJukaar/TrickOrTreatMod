package net.mehvahdjukaar.hauntedharvest.client;


import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.mojang.blaze3d.systems.RenderSystem;
import net.mehvahdjukaar.hauntedharvest.HauntedHarvest;
import net.mehvahdjukaar.hauntedharvest.blocks.ModCarvedPumpkinBlockTile;
import net.mehvahdjukaar.hauntedharvest.blocks.PumpkinType;
import net.mehvahdjukaar.hauntedharvest.items.components.PumpkinCarvingData;
import net.mehvahdjukaar.moonlight.api.client.texture_renderer.FrameBufferBackedDynamicTexture;
import net.mehvahdjukaar.moonlight.api.client.texture_renderer.RenderedTexturesManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.function.BiFunction;

public class CarvingManager implements PreparableReloadListener {

    public static final CarvingManager INSTANCE = new CarvingManager();

    @Override
    public CompletableFuture<Void> reload(PreparationBarrier preparationBarrier, ResourceManager resourceManager,
                                          ProfilerFiller profilerFiller, ProfilerFiller profilerFiller2,
                                          Executor executor, Executor executor2) {

        Objects.requireNonNull(preparationBarrier);
        return CompletableFuture.completedFuture(null)
                .thenCompose(preparationBarrier::wait)
                .thenAcceptAsync((preparation) -> {
                    onTextureReload();
                }, executor2);
    }

    private static final LoadingCache<PumpkinCarvingData, CarvingVisuals> TEXTURE_CACHE = CacheBuilder.newBuilder()
            .expireAfterAccess(2, TimeUnit.MINUTES)
            .removalListener(i -> {
                CarvingVisuals value = (CarvingVisuals) i.getValue();
                if (value != null) {
                    RenderSystem.recordRenderCall(value::close);
                }
            })
            .build(new CacheLoader<>() {
                @Override
                public CarvingVisuals load(PumpkinCarvingData key) {
                    return null;
                }
            });

    public static CarvingVisuals getInstance(PumpkinCarvingData key) {
        CarvingVisuals textureInstance = TEXTURE_CACHE.getIfPresent(key);
        if (textureInstance == null) {
            textureInstance = new CarvingVisuals(ModCarvedPumpkinBlockTile.unpackPixels(key.values), key.type);
            TEXTURE_CACHE.put(key, textureInstance);
        }
        return textureInstance;
    }

    public static class CarvingVisuals implements AutoCloseable {
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

        private CarvingVisuals(boolean[][] pixels, PumpkinType type) {
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

        public List<BakedQuad> getOrCreateModel(Direction dir, BiFunction<CarvingVisuals, Direction, List<BakedQuad>> modelFactory) {
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
    public static ResourceLocation getCachedBlurTexture(CarvingVisuals carving) {
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

    public static void onTextureReload() {
        //idk why its needed. probably due to some bigger underlying bug
        if (pumpkinBlur != null) {
            pumpkinBlur.close();
            pumpkinBlur = null;
        }
    }

    //no need to register a bunch of these just having one since theres only one player
    private static CarvingVisuals currentCarvingBlur = null;
    private static FrameBufferBackedDynamicTexture pumpkinBlur = null;


}

