package net.mehvahdjukaar.hauntedharvest.client.screens;

import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.mehvahdjukaar.hauntedharvest.CustomCarvingsManager;
import net.mehvahdjukaar.hauntedharvest.HauntedHarvest;
import net.mehvahdjukaar.hauntedharvest.SeasonManager;
import net.mehvahdjukaar.hauntedharvest.blocks.ModCarvedPumpkinBlock;
import net.mehvahdjukaar.hauntedharvest.client.CarvedPumpkinItemRenderer;
import net.mehvahdjukaar.hauntedharvest.items.components.PumpkinCarvingData;
import net.mehvahdjukaar.hauntedharvest.reg.ModRegistry;
import net.mehvahdjukaar.moonlight.api.client.gui.ConfigScreenExtensions;
import net.mehvahdjukaar.moonlight.api.client.gui.particle.ScreenParticle;
import net.mehvahdjukaar.moonlight.api.client.gui.particle.ScreenParticleEngine;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;

import java.util.List;

/**
 * The pumpkin sitting in the corner of our config screen: it turns to whatever the cursor is doing and gets a new
 * face on click. A jack o'lantern instead of a plain carved pumpkin while the autumn season is on.
 */
public class PumpkinShowcaseWidget extends AbstractWidget {

    public static final ConfigScreenExtensions.Showcase SHOWCASE = new ConfigScreenExtensions.Showcase() {
        @Override
        public AbstractWidget create(String modId, int x, int y, int width, int maxHeight) {
            return new PumpkinShowcaseWidget(x, y, width, maxHeight);
        }

        @Override
        public boolean replacesCarousel() {
            return false;
        }
    };

    // carvings are datapack driven and there's no world behind a config screen, so the usual pool is empty when
    // this is opened from the main menu. packed pixels of a few of the faces we ship, as a stand in
    private static final List<long[]> BUILTIN_FACES = List.of(
            new long[]{2177506019154853888L, 2143745309474307640L, 4339280020737375616L, 236985912L}, // classic
            new long[]{2177505812996423680L, 2193318990237679164L, 2178682549142822496L, 234888760L}, // happy
            new long[]{3598376583305363456L, 1729409058469853680L, 4174900008180520960L, 15741424L}, // angery
            new long[]{69806825016393728L, 1116909681818220792L, 4393295487259643776L, 14680312L}, // creeper
            new long[]{2044640896632225792L, 4035279143162624192L, 4089331959271012352L, 1099613346912L}, // scary_face
            new long[]{2017620570194051168L, -2305596171265492992L, 4035348655710920704L, 27023728189119488L}, // smiley
            new long[]{1013318162527027200L, 1729409195429596272L, 896248074656028672L, 2061710134800L}, // sus
            new long[]{1736154217803939840L, 3458901884867739192L, 9095072741525041152L, 3848543410200L}, // teeth
            new long[]{1013318162495569920L, 4467604228234288956L, 2250688139219713536L, 125832720L}, // dastardly
            new long[]{141872115635716096L, 2017679086817116408L, 8140318964752325632L, 133169656L}, // gloomy
            new long[]{4393266040839077888L, 4323518246135804056L, 4073568363537767424L, 13194549528792L}, // wacky
            new long[]{2249564712763129856L, 4323522644780007032L, 4357299126597073920L, 3848544460344L}); // psycho

    // of the smaller widget side. the turned block projects wider than one unit, so this leaves the corners room
    // to swing out without touching the panel edges
    private static final float BLOCK_FILL = 0.76f;
    private static final float MAX_YAW = 38;
    private static final float MAX_PITCH = 24;
    // fraction of the angle left to cover per second, so the head turn lags the cursor a little
    private static final float FOLLOW_SPEED = 12;
    // how far the cursor has to travel from the widget for the pumpkin to be turned all the way, as a fraction of
    // the screen. it lives in a narrow side panel, so anything wider barely moves it
    private static final float FOLLOW_RANGE = 0.35f;
    private static final int RECARVE_ATTEMPTS = 4;
    // idle hover. the two run at frequencies that don't line up, so the bob and the tilt keep drifting apart
    // instead of settling into an obvious loop
    private static final float BOB_SPEED = 2.2f;
    private static final float BOB_HEIGHT = 0.035f;
    private static final float WOBBLE_SPEED = 1.5f;
    private static final float WOBBLE_ANGLE = 3.5f;
    // carving crumbs, standing in for the block break particles the real thing throws off. shades picked off the
    // pumpkin side texture
    private static final int[] CRUMB_TINTS = {0xE0912B, 0xC4761C, 0x9C5613};
    private static final int CRUMB_COUNT = 14;
    // the pumpkin sits well forward in the gui, so the crumbs have to be pushed past it or they come out behind
    private static final float CRUMB_DEPTH = 200;

    private final RandomSource random = RandomSource.create();
    private final ScreenParticleEngine crumbs = new ScreenParticleEngine();

    private ModCarvedPumpkinBlock block;
    private PumpkinCarvingData carving;
    private float yaw;
    private float pitch;
    private float time;
    private long lastMs = -1;

    public PumpkinShowcaseWidget(int x, int y, int width, int height) {
        super(x, y, width, height, Component.empty());
        this.carve();
    }

    public static void register() {
        ConfigScreenExtensions.registerShowcase(HauntedHarvest.MOD_ID, SHOWCASE);
    }

    @Override
    protected void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        this.animate(mouseX, mouseY);

        float size = Math.min(this.width, this.height) * BLOCK_FILL;

        PoseStack pose = graphics.pose();
        pose.pushPose();
        pose.translate(this.getX() + this.width / 2f, this.getY() + this.height / 2f, 150);
        // same chain an item goes through in a slot: the negative y cancels out the gui projection's own flip, so
        // the quads keep their winding and the culled block render types don't turn inside out
        pose.scale(size, -size, size);
        // hover, applied before the aiming so it stays a straight up and down bob and a screen space tilt no
        // matter where the pumpkin is looking
        pose.translate(0, Mth.sin(this.time * BOB_SPEED) * BOB_HEIGHT, 0);
        pose.mulPose(Axis.ZP.rotationDegrees(Mth.sin(this.time * WOBBLE_SPEED) * WOBBLE_ANGLE));
        pose.mulPose(Axis.XP.rotationDegrees(this.pitch));
        // the carved face is on the block's north side, so 180 turns it to us
        pose.mulPose(Axis.YP.rotationDegrees(180 + this.yaw));
        pose.translate(-0.5f, -0.5f, -0.5f); // the block renderer starts from the block corner

        Lighting.setupFor3DItems();
        MultiBufferSource.BufferSource buffer = graphics.bufferSource();
        CarvedPumpkinItemRenderer.renderPumpkin(this.block, this.carving, pose, buffer,
                LightTexture.FULL_BRIGHT, OverlayTexture.NO_OVERLAY);

        graphics.flush();
        pose.popPose();

        pose.pushPose();
        pose.translate(0, 0, CRUMB_DEPTH);
        this.crumbs.renderAndTick(graphics);
        pose.popPose();
    }

    private void animate(int mouseX, int mouseY) {
        long now = Util.getMillis();
        float dt = this.lastMs < 0 ? 0 : Math.min((now - this.lastMs) / 1000f, 0.1f); // clamp screen-reopen gaps
        this.lastMs = now;
        this.time += dt;

        var window = Minecraft.getInstance().getWindow();
        float reachX = window.getGuiScaledWidth() * FOLLOW_RANGE;
        float reachY = window.getGuiScaledHeight() * FOLLOW_RANGE;
        float targetYaw = Mth.clamp((mouseX - (this.getX() + this.width / 2f)) / reachX, -1, 1) * MAX_YAW;
        float targetPitch = Mth.clamp((mouseY - (this.getY() + this.height / 2f)) / reachY, -1, 1) * MAX_PITCH;

        float t = 1 - (float) Math.exp(-dt * FOLLOW_SPEED);
        this.yaw = Mth.lerp(t, this.yaw, targetYaw);
        this.pitch = Mth.lerp(t, this.pitch, targetPitch);
    }

    private void carve() {
        this.block = (isAutumnSeason() ? ModRegistry.JACK_O_LANTERN : ModRegistry.CARVED_PUMPKIN).get();

        boolean[][] pixels = PumpkinCarvingData.unpack(this.rollFace());
        // landing back on the face already shown reads as the click having done nothing
        for (int i = 0; i < RECARVE_ATTEMPTS && this.carving != null && this.carving.hasSamePixels(pixels); i++) {
            pixels = PumpkinCarvingData.unpack(this.rollFace());
        }

        this.carving = PumpkinCarvingData.of(pixels, this.block.getType(this.block.defaultBlockState()), false);
        this.setMessage(this.block.getName());
    }

    private long[] rollFace() {
        long[] carved = CustomCarvingsManager.getRandomCarving(this.random, true);
        for (long row : carved) {
            if (row != 0) return carved;
        }
        return BUILTIN_FACES.get(this.random.nextInt(BUILTIN_FACES.size()));
    }

    private static boolean isAutumnSeason() {
        SeasonManager seasons = HauntedHarvest.getSeasonManager();
        Level level = Minecraft.getInstance().level;
        //season mods decide per world, so outside of one we have nothing to go by
        if (seasons.usesSeasonMod() && level == null) return false;
        return seasons.isHalloween(level);
    }

    private void spawnCrumbs() {
        float centerX = this.getX() + this.width / 2f;
        float centerY = this.getY() + this.height / 2f;
        float radius = Math.min(this.width, this.height) * BLOCK_FILL / 2f;

        for (int i = 0; i < CRUMB_COUNT; i++) {
            float angle = this.random.nextFloat() * Mth.TWO_PI;
            float dist = radius * (0.3f + this.random.nextFloat() * 0.7f);
            float speed = 25 + this.random.nextFloat() * 45;
            this.crumbs.add(ScreenParticle
                    .square(centerX + Mth.cos(angle) * dist, centerY + Mth.sin(angle) * dist)
                    .velocity(Mth.cos(angle) * speed, Mth.sin(angle) * speed - 30) // biased up, so they arc
                    .gravity(170)
                    .drag(1.4f)
                    .size(1.5f + this.random.nextFloat() * 2f)
                    .tint(CRUMB_TINTS[this.random.nextInt(CRUMB_TINTS.length)])
                    .fadeOut(0.6f)
                    .lifetime(0.45f + this.random.nextFloat() * 0.35f));
        }
    }

    @Override
    public void onClick(double mouseX, double mouseY) {
        this.carve();
        this.spawnCrumbs();
    }

    @Override
    public void playDownSound(SoundManager handler) {
        // same sound and pitch carving one in the world gives you
        handler.play(SimpleSoundInstance.forUI(SoundEvents.PUMPKIN_CARVE, 1.2f));
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput output) {
        output.add(NarratedElementType.TITLE, this.getMessage());
    }
}
