package net.mehvahdjukaar.hauntedharvest.client.screens;


import net.mehvahdjukaar.hauntedharvest.reg.ClientRegistry;
import net.mehvahdjukaar.moonlight.api.client.util.RenderUtil;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;


public class CarvingButton extends BaseCarvingButton {

    public static final int SIZE = 6;

    private final int u;
    private final int v;
    private TextureAtlasSprite sprite;

    public CarvingButton(CarvingScreen screen, int centerX, int centerY, int u, int v, boolean carved) {
        super(screen, centerX - ((8 - u) * SIZE), centerY - ((-v) * SIZE), carved, SIZE,
                ClientRegistry.OUTLINE_SPRITE);
        this.u = u;
        this.v = v;
    }

    public void setCarved(boolean carved) {
        this.parent.addHistory(this.u, this.v, this.carved);
        this.carved = carved;
        this.parent.updateBlackboard(this.u, this.v, carved);
    }

    public void setSprite(TextureAtlasSprite sprite) {
        this.sprite = sprite;
    }

    @Override
    protected void onClick() {
        setCarved(!this.carved);
    }


    @Override
    public boolean mouseDragged(MouseButtonEvent event, double dragX, double dragY) {
        if (this.isValidClickButton(event.buttonInfo())) {
            this.parent.onButtonDragged(event.x(), event.y(), this.carved);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean mouseReleased(MouseButtonEvent event) {
        if (this.isValidClickButton(event.buttonInfo())) {
            this.parent.saveHistoryStep();
            return true;
        } else {
            return false;
        }
    }

    @Override
    protected void extractButton(GuiGraphicsExtractor graphics) {
        RenderUtil.blitSpriteSection(graphics, this.x, this.y, SIZE, SIZE, u / 16f, v / 16f, 1, 1, sprite);
    }


}

