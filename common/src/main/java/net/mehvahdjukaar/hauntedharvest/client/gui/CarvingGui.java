package net.mehvahdjukaar.hauntedharvest.client.gui;


import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.vertex.PoseStack;
import net.mehvahdjukaar.hauntedharvest.blocks.ModCarvedPumpkinBlockTile;
import net.mehvahdjukaar.hauntedharvest.network.NetworkHandler;
import net.mehvahdjukaar.hauntedharvest.network.ServerBoundCarvePumpkinPacket;
import net.mehvahdjukaar.hauntedharvest.client.PumpkinTextureGenerator;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.model.Material;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

public class CarvingGui extends Screen {

    private static final MutableComponent CLEAR = Component.translatable("gui.hauntedharvest.carving.clear");
    private static final MutableComponent EDIT = Component.translatable("gui.hauntedharvest.carving.edit");


    private final ModCarvedPumpkinBlockTile tile;

    private final CarvingButton[][] buttons = new CarvingButton[16][16];

    private CarvingGui(ModCarvedPumpkinBlockTile teBoard) {
        super(EDIT);
        this.tile = teBoard;
    }

    public static void open(ModCarvedPumpkinBlockTile sign) {
        Minecraft.getInstance().setScreen(new CarvingGui(sign));
    }

    @Override
    public void tick() {
        if (!this.tile.getType().isValid(this.tile.getBlockState())) {
            this.close();
        }
    }

    @Override
    public void onClose() {
        this.close();
    }

    @Override
    public void removed() {
        // send new image to the server
        boolean[][] pixels = getPixelMatrix();
        getPixelMatrix();
        NetworkHandler.CHANNEL.sendToServer(new ServerBoundCarvePumpkinPacket(this.tile.getBlockPos(), pixels));
    }

    private boolean[][] getPixelMatrix() {
        boolean[][] pixels = new boolean[16][16];
        for (int xx = 0; xx < 16; xx++) {
            for (int yy = 0; yy < 16; yy++) {
                pixels[xx][yy] = (this.buttons[xx][yy].carved);
            }
        }
        return pixels;
    }

    private void close() {

        this.tile.setChanged();
        this.minecraft.setScreen(null);
    }

    //dynamic refreshTextures for client
    public void setPixel(int x, int y, boolean on) {
        this.tile.setPixel(x, y, on);
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
                this.buttons[xx][yy].carved = this.tile.getPixel(xx, yy);
            }
        }

        this.addRenderableWidget(Button.builder(CLEAR, (b) -> this.clear())
                .bounds( this.width / 2 - 100, this.height / 4 + 120, 100 - 4, 20)
                .build());
        this.addRenderableWidget(Button.builder(CommonComponents.GUI_DONE, (b) -> this.close())
                .bounds(this.width / 2 + 4, this.height / 4 + 120, 100 - 4, 20)
                .build());
    }

    @Override
    public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTicks) {
        Lighting.setupForFlatItems();
        this.renderBackground(poseStack);
        drawCenteredString(poseStack, this.font, this.title, this.width / 2, 40, 16777215);

        Material[][] materials = PumpkinTextureGenerator.computePixelMaterialMap(getPixelMatrix(), tile.getPumpkinType());

        poseStack.pushPose();
        //float ff = 93.75F/16f;
        //poseStack.scale(ff,ff,ff);
        int ut = -1;
        int vt = -1;
        for (int xx = 0; xx < 16; xx++) {
            for (int yy = 0; yy < 16; yy++) {
                if (this.buttons[xx][yy].isHovered()) {
                    ut = xx;
                    vt = yy;
                }
                this.buttons[xx][yy].setMaterial(materials[xx][yy]);
                this.buttons[xx][yy].render(poseStack, mouseX, mouseY, partialTicks);
            }
        }
        if (ut != -1) this.buttons[ut][vt].renderTooltip(poseStack);
        poseStack.popPose();

        Lighting.setupFor3DItems();
        super.render(poseStack, mouseX, mouseY, partialTicks);
    }
}

