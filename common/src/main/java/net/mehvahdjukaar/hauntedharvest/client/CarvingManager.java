package net.mehvahdjukaar.hauntedharvest.client;


import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.mojang.blaze3d.systems.RenderSystem;
import net.mehvahdjukaar.hauntedharvest.blocks.PumpkinType;
import net.mehvahdjukaar.hauntedharvest.items.components.PumpkinCarvingData;
import net.mehvahdjukaar.moonlight.api.client.model.QuadBatch;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.core.Direction;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.EnumMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.function.BiFunction;

public class CarvingManager implements PreparableReloadListener {

    public static final CarvingManager INSTANCE = new CarvingManager();

    @Override
    public CompletableFuture<Void> reload(SharedState sharedState, Executor executor,
                                          PreparationBarrier preparationBarrier, Executor executor2) {
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

    public static CarvingVisuals getInstance(@NotNull PumpkinCarvingData key) {

        CarvingVisuals textureInstance = TEXTURE_CACHE.getIfPresent(key);
        if (textureInstance == null) {
            textureInstance = new CarvingVisuals(key.clonePixels(), key.getType().value());
            TEXTURE_CACHE.put(key, textureInstance);
        }
        return textureInstance;
    }

    public static class CarvingVisuals implements AutoCloseable {
        public static final int WIDTH = 16;

        //quads for each direction
        private final Map<Direction, QuadBatch> quadsCache = new EnumMap<>(Direction.class);
        private final boolean[][] pixels;
        private final PumpkinType type;
        //he is lazy
        @Nullable
        private DynamicTexture texture;
        @Nullable
        private RenderType renderType;
        @Nullable
        private Identifier textureLocation;

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
            this.texture = new DynamicTexture("pumpkin carving", WIDTH, WIDTH, false);
            PumpkinTextureGenerator.drawCarving(texture, this);
            //texture manager has its own internal id
            this.textureLocation = Minecraft.getInstance().getTextureManager().register("carving/", this.texture);
            this.renderType = RenderTypes.entitySolid(textureLocation);
        }

        public QuadBatch getOrCreateQuads(Direction dir, BiFunction<CarvingVisuals, Direction, QuadBatch> quadFactory) {
            return this.quadsCache.computeIfAbsent(dir, d -> quadFactory.apply(this, d));
        }

        public Identifier getTextureLocation() {
            if (textureLocation == null) {
                //I can only initialize it here since this is guaranteed to be on render thread
                this.initializeTexture();
            }
            return textureLocation;
        }

        @Nullable
        public Identifier getPumpkinBlur() {
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


    //TODO: blurred pumpkin overlay is off until the blur shader is ported to a RenderPipeline
    @Nullable
    public static Identifier getCachedBlurTexture(CarvingVisuals carving) {
        return null;
    }

    public static void onTextureReload() {
    }


}

