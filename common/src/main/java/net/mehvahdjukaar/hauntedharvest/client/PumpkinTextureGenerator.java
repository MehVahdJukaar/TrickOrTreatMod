package net.mehvahdjukaar.hauntedharvest.client;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.FilterMode;
import com.mojang.datafixers.util.Pair;
import net.mehvahdjukaar.hauntedharvest.HauntedHarvest;
import net.mehvahdjukaar.hauntedharvest.blocks.PumpkinType;
import net.mehvahdjukaar.hauntedharvest.reg.ClientRegistry;
import net.mehvahdjukaar.moonlight.api.client.texture_renderer.DynamicTextureRenderer;
import net.mehvahdjukaar.moonlight.api.client.texture_renderer.RenderableDynamicTexture;
import net.mehvahdjukaar.moonlight.api.platform.ClientHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ARGB;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

import static net.mehvahdjukaar.hauntedharvest.client.CarvingManager.CarvingVisuals.WIDTH;

public class PumpkinTextureGenerator {


    public static void drawCarving(DynamicTexture texture, CarvingManager.CarvingVisuals carving) {
        boolean[][] pixels = carving.getPixels();
        TextureAtlasSprite[][] sprites = PumpkinTextureGenerator.computePixelSpriteMap(pixels, carving.getType());

        for (int y = 0; y < pixels.length && y < WIDTH; y++) {
            for (int x = 0; x < pixels[y].length && x < WIDTH; x++) {
                int c = ClientHelper.getPixelABGR(sprites[x][y], 0, x, y);
                texture.getPixels().setPixelABGR(x, y, c);
            }
        }
        texture.upload();
    }

    /**
     * Turns carved pixels matrix into a usable color matrix for the carved section of a pumpkin
     * Rest of the texture is simply using vanilla texture
     */
    public static TextureAtlasSprite[][] computePixelSpriteMap(boolean[][] pixels, PumpkinType pumpkinType) {
        PixelType[][] colors = new PixelType[16][16];

        forEachPixel(colors, (j, i) -> {
            if (!pixels[j][i]) {
                colors[j][i] = PixelType.UNCARVED;
            } else {
                if (shouldShade(colors, j, i)) {
                    colors[j][i] = PixelType.SHADE;
                } else {
                    colors[j][i] = PixelType.BACKGROUND;
                }
            }
        });

        addExtraShade(colors);
        forEachPixel(colors, (j, i) -> addHighlight(colors, j, i));
        TextureAtlasSprite[][] sprites = new TextureAtlasSprite[16][16];
        forEachPixel(sprites, (j, i) -> sprites[j][i] =
                ClientRegistry.sprite(ClientRegistry.getSprite(pumpkinType, colors[j][i].ordinal())));
        return sprites;
    }

    private static void addExtraShade(PixelType[][] px) {
        List<Pair<Integer, Integer>> shades = new ArrayList<>();
        forEachPixel(px, (j, i) -> {
            if (!isUnCarved(px, j, i)) {
                //Up
                if (isShaded(px, j, i - 1)) {
                    //sides
                    if (isShaded(px, j - 1, i) && isShaded(px, j + 1, i)) {
                        if (isShaded(px, j - 1, i + 1) || isShaded(px, j + 1, i + 1)) {
                            shades.add(Pair.of(j, i));
                        }
                    }
                    if (isShaded(px, j, i + 1)) {
                        if (isShaded(px, j + 1, i - 1) && isShaded(px, j - 1, i) ||
                                isShaded(px, j - 1, i - 1) && isShaded(px, j + 1, i)) {
                            shades.add(Pair.of(j, i));
                        }
                    }
                }
            }
        });
        shades.forEach(p -> px[p.getFirst()][p.getSecond()] = PixelType.SHADE);
    }


    private static void addHighlight(PixelType[][] px, int j, int i) {
        if (isUnCarved(px, j, i)) {
            if (!isUnCarved(px, j - 1, i) || (!isUnCarved(px, j, i - 1))) {
                px[j][i] = PixelType.HIGHLIGHT;
            }
        }
    }

    private static boolean shouldShade(PixelType[][] px, int j, int i) {
        return (isUnCarved(px, j - 1, i) || isUnCarved(px, j, i - 1));
    }

    private static boolean isUnCarved(PixelType[][] px, int j, int i) {
        if (j < 0 || i < 0 || j > 15 || i > 15) return true;
        var t = px[j][i];
        return t == PixelType.UNCARVED || t == PixelType.HIGHLIGHT;
    }

    private static boolean isShaded(PixelType[][] px, int j, int i) {
        if (j < 0 || i < 0 || j > 15 || i > 15) return true;
        return px[j][i] == PixelType.SHADE;
    }

    public static void forEachPixel(Object[][] px, BiConsumer<Integer, Integer> function) {
        for (int j = 0; j < px.length; j++) {
            for (int i = 0; i < px[j].length; i++) {
                function.accept(j, i);
            }
        }
    }

    private enum PixelType {
        UNCARVED,
        SHADE,
        BACKGROUND,
        HIGHLIGHT
    }

    //blur
    private static DynamicTexture dummy;
    private static Identifier dummyLocation;

    public static void drawBlur(RenderableDynamicTexture t, @Nullable CarvingManager.CarvingVisuals carving) {
        if (carving == null) return;
        var pixels = carving.getPixels();
        if (dummyLocation == null) {
            dummy = new DynamicTexture("pumpkin blur mask", 18, 18, false) {
                {
                    this.sampler = RenderSystem.getSamplerCache().getClampToEdge(FilterMode.LINEAR);
                }
            };
            dummyLocation = HauntedHarvest.res("carving/blur_mask");
            Minecraft.getInstance().getTextureManager().register(dummyLocation, dummy);
        }
        var p = dummy.getPixels();
        for (int x = 0; x < 18; x++) {
            for (int y = 0; y < 18; y++) {
                int alpha = 0;
                if (x == 0 || x == 17 || y == 0 || y == 17 || !pixels[x - 1][y - 1]) {
                    alpha = 255;
                }
                p.setPixelABGR(x, y, ARGB.color(alpha, 0, 0, 0));
            }
        }
        dummy.upload();

        DynamicTextureRenderer.drawAsInGUI(t, (pose, collector) -> {
            float u0 = 1 / 18f;
            float u1 = 17 / 18f;

            collector.submitCustomGeometry(pose, ClientRegistry.BLUR.apply(dummyLocation), (matrix, buffer) -> {
                buffer.addVertex(matrix, 0.0f, 16, 0).setUv(u0, u0);
                buffer.addVertex(matrix, 16, 16, 0).setUv(u1, u0);
                buffer.addVertex(matrix, 16, 0.0f, 0).setUv(u1, u1);
                buffer.addVertex(matrix, 0.0f, 0.0f, 0).setUv(u0, u1);
            });
        });
    }
}
