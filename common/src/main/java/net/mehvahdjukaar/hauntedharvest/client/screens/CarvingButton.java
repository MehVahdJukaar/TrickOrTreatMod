package net.mehvahdjukaar.hauntedharvest.client.screens;


import net.mehvahdjukaar.hauntedharvest.reg.ClientRegistry;
import net.mehvahdjukaar.moonlight.api.client.util.RenderUtil;
import net.mehvahdjukaar.supplementaries.reg.ModTextures;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.Material;


public class CarvingButton extends BaseCarvingButton {

    public static final int SIZE = 6;

    private final int u;
    private final int v;
    private Material material;

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

    public void setMaterial(Material material) {
        this.material = material;
    }

    @Override
    protected void onClick() {
        setCarved(!this.carved);
    }


    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        if (this.isValidClickButton(button)) {
            this.parent.onButtonDragged(mouseX, mouseY, this.carved);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (this.isValidClickButton(button)) {
            this.parent.saveHistoryStep();
            return true;
        } else {
            return false;
        }
    }

    @Override
    protected void renderButton(GuiGraphics graphics) {
        /*
        int rgb = BlackboardBlock.colorFromByte(this.carved);
        float b = FastColor.ARGB32.blue(rgb) / 255f;
        float g = FastColor.ARGB32.green(rgb) / 255f;
        float r = FastColor.ARGB32.red(rgb) / 255f;

        RenderSystem.setShaderColor(r, g, b, 1.0F);
        int offset = this.carved > 0 ? 16 : 0;
        graphics.blit(ModTextures.BLACKBOARD_GUI_TEXTURE,
                this.x, this.y,
                (float) (this.u + offset) * size, (float) this.v * size,
                size, size, 32 * size, 16 * size);
*/
        TextureAtlasSprite sprite = material.sprite();
        RenderUtil.blitSpriteSection(graphics, x, y, SIZE, SIZE, u, v, 1, 1, sprite);
    }


}

