package net.mehvahdjukaar.hauntedharvest.client.gui;

import net.mehvahdjukaar.hauntedharvest.client.CarvingManager;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.resources.ResourceLocation;

public class CarvingTooltipComponent implements ClientTooltipComponent {

    private static final int SIZE = 80;
    private final ResourceLocation texture;

    public CarvingTooltipComponent(CarvingManager.Key key) {
        this.texture = CarvingManager.getInstance(key).getTextureLocation();
    }

    @Override
    public int getHeight() {
        return SIZE + 2;
    }

    @Override
    public int getWidth(Font pFont) {
        return SIZE;
    }

    @Override
    public void renderImage(Font font, int x, int y, GuiGraphics graphics) {
        graphics.blit(texture, x, y, 0, 0, 0, SIZE, SIZE, SIZE, SIZE);
    }
}