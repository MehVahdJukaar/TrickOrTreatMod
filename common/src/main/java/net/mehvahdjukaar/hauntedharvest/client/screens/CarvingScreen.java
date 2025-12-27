package net.mehvahdjukaar.hauntedharvest.client.screens;


import net.mehvahdjukaar.hauntedharvest.blocks.ModCarvedPumpkinBlockTile;
import net.mehvahdjukaar.hauntedharvest.client.PumpkinTextureGenerator;
import net.mehvahdjukaar.hauntedharvest.network.ServerBoundCarvePumpkinPacket;
import net.mehvahdjukaar.moonlight.api.misc.CircularList;
import net.mehvahdjukaar.moonlight.api.platform.network.NetworkHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

public class CarvingScreen extends Screen {

    private static final MutableComponent UNDO = Component.translatable("gui.hauntedharvest.carving.undo");
    private static final MutableComponent CLEAR = Component.translatable("gui.hauntedharvest.carving.clear");
    private static final MutableComponent EDIT = Component.translatable("gui.hauntedharvest.carving.edit");


    private final ModCarvedPumpkinBlockTile tile;

    private final CarvingButton[][] buttons = new CarvingButton[16][16];

    private final Deque<List<Entry>> history = new CircularList<>(20);
    private final Direction clickedFace;
    private List<Entry> currentHistoryStep = new ArrayList<>();
    private Button historyButton;

    private record Entry(int x, int y, boolean carved) {
    }

    private CarvingScreen(ModCarvedPumpkinBlockTile teBoard, Direction dir) {
        super(EDIT);
        this.tile = teBoard;
        this.clickedFace = dir;
    }

    public static void open(ModCarvedPumpkinBlockTile tile, Direction dir) {
        Minecraft.getInstance().setScreen(new CarvingScreen(tile, dir));
    }

    public void recomputeMaterials() {
        var materials = PumpkinTextureGenerator.computePixelMaterialMap(computePixelMatrix(),
                tile.getPumpkinType().value());
        //re-assign to buttons
        for (int xx = 0; xx < 16; xx++) {
            for (int yy = 0; yy < 16; yy++) {
                this.buttons[xx][yy].setMaterial(materials[xx][yy]);
            }
        }
    }

    private boolean[][] computePixelMatrix() {
        boolean[][] pixels = new boolean[16][16];
        for (int xx = 0; xx < 16; xx++) {
            for (int yy = 0; yy < 16; yy++) {
                pixels[xx][yy] = (this.buttons[xx][yy].carved);
            }
        }
        return pixels;
    }

    @Override
    public void tick() {
        if (!isValid()) {
            this.onClose();
        } else {
            if (!(this.getFocused() instanceof CarvingButton)) {
                setFocused(null); //dont focus clear buttons
            }
        }
    }

    private boolean isValid() {
        return this.minecraft != null && this.minecraft.player != null && !this.tile.isRemoved() &&
                this.tile.canBeUsedBy(this.tile.getBlockPos(), this.minecraft.player);
    }

    @Override
    public void onClose() {
        this.tile.setChanged();
        super.onClose();
    }

    @Override
    public void removed() {
        // send new image to the server
        boolean[][] pixels = new boolean[16][16];
        for (int xx = 0; xx < 16; xx++) {
            for (int yy = 0; yy < 16; yy++) {
                pixels[xx][yy] = (this.buttons[xx][yy].getCarved());
            }
        }
        NetworkHelper.sendToServer(new ServerBoundCarvePumpkinPacket(this.tile.getBlockPos(), pixels, clickedFace));
    }

    //dynamic refreshTextures for client
    public void updateBlackboard(int x, int y, boolean newColor) {
        this.tile.setPixel(x, y, newColor);
        recomputeMaterials();
    }

    public void addHistory(int x, int y, boolean oldColor) {
        this.currentHistoryStep.add(new Entry(x, y, oldColor));
    }

    public void saveHistoryStep() {
        if (!currentHistoryStep.isEmpty()) {
            this.history.add(currentHistoryStep);
            this.currentHistoryStep = new ArrayList<>();
            this.historyButton.active = true;
        }
    }

    //calls drag for other buttons
    public void onButtonDragged(double mx, double my, boolean buttonValue) {
        for (int xx = 0; xx < 16; xx++) {
            for (int yy = 0; yy < 16; yy++) {
                CarvingButton b = this.buttons[xx][yy];
                if (b.isMouseOver(mx, my) && b.getCarved() != buttonValue)
                    b.setCarved(buttonValue);
            }
        }
    }

    private void clearPressed(Button button) {
        for (int xx = 0; xx < 16; xx++) {
            for (int yy = 0; yy < 16; yy++) {
                this.buttons[xx][yy].setCarved(false);
            }
        }
        this.saveHistoryStep();
    }


    private void undoPressed(Button button) {
        if (!this.history.isEmpty()) {
            for (var v : this.history.pollLast()) {
                this.buttons[v.x()][v.y()].setCarved(v.carved);
            }
            //clear history step from this undo we just added
            this.currentHistoryStep.clear();
        }
        if (this.history.isEmpty()) {
            this.historyButton.active = false;
        }
    }


    @Override
    protected void init() {
        for (int xx = 0; xx < 16; xx++) {
            for (int yy = 0; yy < 16; yy++) {
                boolean pixel = this.tile.getPixel(xx, yy);
                CarvingButton widget = new CarvingButton(this, (this.width / 2), 40 + 25, xx, yy, pixel);
                this.buttons[xx][yy] = this.addRenderableWidget(widget);
            }
        }
        this.recomputeMaterials();

        int buttonW = 56;
        int sep = 4;
        this.addRenderableWidget(Button.builder(CLEAR, this::clearPressed)
                .bounds(this.width / 2 - buttonW / 2 - buttonW + sep / 2, this.height / 4 + 120, buttonW - sep, 20).build());

        this.addRenderableWidget(Button.builder(CommonComponents.GUI_DONE, button -> this.onClose())
                .bounds(this.width / 2 - buttonW / 2 + sep / 2, this.height / 4 + 120, buttonW - sep, 20).build());

        this.historyButton = this.addRenderableWidget(Button.builder(UNDO, this::undoPressed)
                .bounds(this.width / 2 + buttonW / 2 + sep / 2, this.height / 4 + 120, buttonW - sep, 20).build());

    }

    /*
    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        graphics.drawCenteredString(this.font, this.title, this.width / 2, 40, 16777215);

        if (CompatHandler.IMMEDIATELY_FAST) ImmediatelyFastCompat.startBatching();

        // RenderSystem.enableDepthTest();
        super.render(graphics, mouseX, mouseY, partialTicks);
        // RenderSystem.disableDepthTest();
        graphics.pose().pushPose();
        label:
        for (int xx = 0; xx < 16; xx++) {
            for (int yy = 0; yy < 16; yy++) {
                CarvingButton button = this.buttons[xx][yy];
                if (button.isShouldDrawOverlay()) {
                    button.renderHoverOverlay(graphics);
                    break label;
                }
            }
        }
        graphics.pose().popPose();
        if (CompatHandler.IMMEDIATELY_FAST) ImmediatelyFastCompat.endBatching();
    }

*/
    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        graphics.drawCenteredString(this.font, this.title, this.width / 2, 40, 16777215);
        super.render(graphics, mouseX, mouseY, partialTicks);
    }
}

