package net.mehvahdjukaar.hauntedharvest.client.screens;

import net.mehvahdjukaar.hauntedharvest.client.CarvingManager;
import net.mehvahdjukaar.hauntedharvest.items.components.PumpkinCarvingData;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;

public class CarvingTooltipComponent implements ClientTooltipComponent {

    private static final int SIZE = 80;
    private final Identifier texture;

    public CarvingTooltipComponent(PumpkinCarvingData key) {
        this.texture = CarvingManager.getInstance(key).getTextureLocation();
    }

    @Override
    public int getHeight(Font font) {
        return SIZE + 2;
    }

    @Override
    public int getWidth(Font pFont) {
        return SIZE;
    }

    @Override
    public void extractImage(Font font, int x, int y, int w, int h, GuiGraphicsExtractor graphics) {
        graphics.blit(RenderPipelines.GUI_TEXTURED, texture, x, y, 0, 0, SIZE, SIZE, SIZE, SIZE);
    }
}