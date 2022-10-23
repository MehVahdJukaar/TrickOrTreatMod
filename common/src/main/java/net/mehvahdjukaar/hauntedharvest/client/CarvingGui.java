package net.mehvahdjukaar.hauntedharvest.client;


import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.vertex.PoseStack;
import net.mehvahdjukaar.hauntedharvest.blocks.ModCarvedPumpkinBlockTile;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;

public class CarvingGui extends Screen {
    private final ModCarvedPumpkinBlockTile tileBoard;

    private final CarvingButton[][] buttons = new CarvingButton[16][16];

    private CarvingGui(ModCarvedPumpkinBlockTile teBoard) {
        super(Component.translatable("gui.supplementaries.blackboard.edit"));
        this.tileBoard = teBoard;
    }

    public static void open(ModCarvedPumpkinBlockTile sign) {
        Minecraft.getInstance().setScreen(new CarvingGui(sign));
    }

    @Override
    public void tick() {
        if (!this.tileBoard.getType().isValid(this.tileBoard.getBlockState())) {
            this.close();
        }
    }

    @Override
    public void onClose() {
        this.close();
    }

    @Override
    public void removed() {
        this.minecraft.keyboardHandler.setSendRepeatsToGui(false);
        // send new image to the server
        boolean[][] pixels = new boolean[16][16];
        for (int xx = 0; xx < 16; xx++) {
            for (int yy = 0; yy < 16; yy++) {
                pixels[xx][yy] = (this.buttons[xx][yy].carved);
            }
        }
        //NetworkHandler.CHANNEL.sendToServer(new ServerBoundSetBlackboardPacket(this.tileBoard.getBlockPos(), pixels));
    }

    private void close() {

        this.tileBoard.setChanged();
        this.minecraft.setScreen(null);
    }

    //dynamic refreshTextures for client
    public void setPixel(int x, int y, boolean on) {
        this.tileBoard.setPixel(x, y, on);
    }

    //calls drag for other buttons
    public void dragButtons(double mx, double my, boolean on) {
        for (int xx = 0; xx < 16; xx++) {
            for (int yy = 0; yy < 16; yy++) {
                if (this.buttons[xx][yy].isMouseOver(mx, my))
                    this.buttons[xx][yy].onDrag(mx, my, on);
            }
        }
    }

    private void clear() {
        for (int xx = 0; xx < 16; xx++) {
            for (int yy = 0; yy < 16; yy++) {
                setPixel(xx, yy, false);
                this.buttons[xx][yy].carved = false;
            }
        }
    }

    @Override
    protected void init() {
        for (int xx = 0; xx < 16; xx++) {
            for (int yy = 0; yy < 16; yy++) {
                this.buttons[xx][yy] = new CarvingButton((this.width / 2), 40 + 25, xx, yy, this::setPixel, this::dragButtons);
                this.addWidget(this.buttons[xx][yy]);
                this.buttons[xx][yy].carved = this.tileBoard.getPixel(xx, yy);
            }
        }

        this.minecraft.keyboardHandler.setSendRepeatsToGui(true);
        this.addRenderableWidget(new Button(this.width / 2 - 100, this.height / 4 + 120, 100 - 4, 20, Component.translatable("gui.supplementaries.blackboard.clear"), (b) -> this.clear()));
        this.addRenderableWidget(new Button(this.width / 2 + 4, this.height / 4 + 120, 100 - 4, 20, CommonComponents.GUI_DONE, (p_238847_1_) -> this.close()));
    }

    @Override
    public void render(PoseStack matrixstack, int mouseX, int mouseY, float partialTicks) {
        Lighting.setupForFlatItems();
        this.renderBackground(matrixstack);
        drawCenteredString(matrixstack, this.font, this.title, this.width / 2, 40, 16777215);


        matrixstack.pushPose();
        //float ff = 93.75F/16f;
        //matrixstack.scale(ff,ff,ff);
        int ut = -1;
        int vt = -1;
        for (int xx = 0; xx < 16; xx++) {
            for (int yy = 0; yy < 16; yy++) {
                if (this.buttons[xx][yy].isHovered()) {
                    ut = xx;
                    vt = yy;
                }
                this.buttons[xx][yy].render(matrixstack, mouseX, mouseY, partialTicks);
            }
        }
        if (ut != -1) this.buttons[ut][vt].renderTooltip(matrixstack);
        matrixstack.popPose();

        Lighting.setupFor3DItems();
        super.render(matrixstack, mouseX, mouseY, partialTicks);
    }
}

