package net.mehvahdjukaar.hauntedharvest.client.gui;


import net.mehvahdjukaar.hauntedharvest.reg.ClientRegistry;
import net.mehvahdjukaar.moonlight.api.client.util.RenderUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.sounds.SoundEvents;


public class CarvingButton implements Renderable, GuiEventListener, NarratableEntry {
    public final int u;
    public final int v;
    public final int x;
    public final int y;
    public static final int SIZE = 6;
    protected boolean isHovered;
    protected boolean carved = false;
    protected boolean focused;

    private final IDraggable onDragged;
    private final IPressable onPress;
    private Material material;

    public CarvingButton(int centerX, int centerY, int u, int v, IPressable pressedAction,
                         IDraggable dragAction) {
        this.x = centerX - ((8 - u) * SIZE);
        this.y = centerY - ((-v) * SIZE);
        this.u = u;
        this.v = v;
        this.onPress = pressedAction;
        this.onDragged = dragAction;
    }

    public void setMaterial(Material material) {
        this.material = material;
    }

    public void setCarved(boolean carved) {
        this.carved = carved;
    }

    public boolean getCarved() {
        return carved;
    }

    @Override
    public void render(GuiGraphics matrixStack, int mouseX, int mouseY, float partialTicks) {
        this.isHovered = this.isMouseOver(mouseX, mouseY);
        //boolean wasHovered = this.isHovered();
        renderButton(matrixStack);
    }

    private void renderButton(GuiGraphics graphics) {
        TextureAtlasSprite sprite = material.sprite();
        RenderUtil.blitSpriteSection(graphics, x, y, SIZE, SIZE, u, v, 1, 1, sprite);
    }

    public void renderTooltip(GuiGraphics graphics) {
        TextureAtlasSprite sprite = ClientRegistry.CARVING_OUTLINE.sprite();
        RenderUtil.blitSpriteSection(graphics, x - 1, y - 1, SIZE + 2, SIZE + 2, 0, 0, 1, 1, sprite);

        this.renderButton(graphics);
    }

    //toggle
    public void onClick(double mouseX, double mouseY) {
        this.carved = !this.carved;
        this.onPress.onPress(this.u, this.v, this.carved);
    }

    public void onRelease(double mouseX, double mouseY) {
    }

    //set
    public void onDrag(double mouseX, double mouseY, boolean on) {
        this.carved = on;
        this.onPress.onPress(this.u, this.v, this.carved);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (this.isValidClickButton(button)) {
            boolean flag = this.isMouseOver(mouseX, mouseY);
            if (flag) {
                this.playDownSound(Minecraft.getInstance().getSoundManager());
                this.onClick(mouseX, mouseY);
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (this.isValidClickButton(button)) {
            this.onRelease(mouseX, mouseY);
            return true;
        } else {
            return false;
        }
    }

    protected boolean isValidClickButton(int button) {
        return button == 0;
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {

        if (this.isValidClickButton(button)) {
            this.onDragged.onDragged(mouseX, mouseY, this.carved);
            return true;
        } else {
            return false;
        }
    }

    public boolean isHovered() {
        return this.isHovered;
    }

    @Override
    public boolean isMouseOver(double mouseX, double mouseY) {
        return mouseX >= this.x && mouseY >= this.y && mouseX < (this.x + SIZE) && mouseY < (this.y + SIZE);
    }

    public void playDownSound(SoundManager handler) {
        handler.play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
    }

    @Override
    public NarrationPriority narrationPriority() {
        return NarrationPriority.NONE;
    }

    @Override
    public void updateNarration(NarrationElementOutput pNarrationElementOutput) {
    }

    public interface IPressable {
        void onPress(int x, int y, boolean on);
    }

    public interface IDraggable {
        void onDragged(double mouseX, double mouseY, boolean on);
    }

    @Override
    public boolean isFocused() {
        return focused;
    }

    @Override
    public void setFocused(boolean focused) {
        this.focused = focused;
    }
}

