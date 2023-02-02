package net.mehvahdjukaar.hauntedharvest.integration.forge.configured;


import com.mojang.blaze3d.vertex.PoseStack;
import com.mrcrayfish.configured.client.util.ScreenUtil;
import net.mehvahdjukaar.hauntedharvest.HauntedHarvest;
import net.mehvahdjukaar.hauntedharvest.configs.CommonConfigs;
import net.mehvahdjukaar.hauntedharvest.configs.RegistryConfigs;
import net.mehvahdjukaar.hauntedharvest.reg.ModRegistry;
import net.mehvahdjukaar.moonlight.api.client.gui.LinkButton;
import net.mehvahdjukaar.moonlight.api.client.util.RenderUtil;
import net.mehvahdjukaar.moonlight.api.integration.configured.CustomConfigSelectScreen;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntries;

public class ModConfigSelectScreen extends CustomConfigSelectScreen {


    public ModConfigSelectScreen(Screen parent) {
        super(HauntedHarvest.MOD_ID, ModRegistry.DEATH_APPLE.get().asItem().getDefaultInstance(),
                ChatFormatting.GOLD + "Haunted Harvest Configured",
                new ResourceLocation("textures/block/cracked_stone_bricks.png"),
                parent, ModConfigScreen::new, CommonConfigs.SPEC, RegistryConfigs.SPEC);
    }


    @Override
    protected void init() {
        super.init();
        Button found = null;
        for (var c : this.children()) {
            if (c instanceof Button button) {
                if (button.getWidth() == 150) found = button;
            }
        }
        if (found != null) this.removeWidget(found);

        int y = this.height - 29;
        int centerX = this.width / 2;

        this.addRenderableWidget(new Button(centerX - 45, y, 90, 20, CommonComponents.GUI_BACK, (button) -> this.minecraft.setScreen(this.parent)));

        LinkButton patreon = LinkButton.create(MISC_ICONS, this, centerX - 45 - 22, y, 3, 1,
                "https://www.patreon.com/user?u=53696377", "Support me on Patreon :D");

        LinkButton kofi = LinkButton.create(MISC_ICONS, this, centerX - 45 - 22 * 2, y, 2, 2,
                "https://ko-fi.com/mehvahdjukaar", "Donate a Coffe");

        LinkButton curseforge = LinkButton.create(MISC_ICONS, this, centerX - 45 - 22 * 3, y, 1, 2,
                "https://www.curseforge.com/minecraft/mc-mods/haunted-harvest", "CurseForge Page");

        LinkButton github = LinkButton.create(MISC_ICONS, this, centerX - 45 - 22 * 4, y, 0, 2,
                "https://github.com/MehVahdJukaar/TrickOrTreatMod", "Github");


        LinkButton discord = LinkButton.create(MISC_ICONS, this, centerX + 45 + 2, y, 1, 1,
                "https://discord.com/invite/qdKRTDf8Cv", "Mod Discord");

        LinkButton youtube = LinkButton.create(MISC_ICONS, this, centerX + 45 + 2 + 22, y, 0, 1,
                "https://www.youtube.com/watch?v=LSPNAtAEn28&t=1s", "Youtube Channel");

        LinkButton twitter = LinkButton.create(MISC_ICONS, this, centerX + 45 + 2 + 22 * 2, y, 2, 1,
                "https://twitter.com/Supplementariez?s=09", "Twitter Page");

        LinkButton akliz = LinkButton.create(MISC_ICONS, this, centerX + 45 + 2 + 22 * 3, y, 3, 2,
                "https://www.akliz.net/supplementaries", "Need a server? Get one with Akliz");


        this.addRenderableWidget(kofi);
        this.addRenderableWidget(akliz);
        this.addRenderableWidget(patreon);
        this.addRenderableWidget(curseforge);
        this.addRenderableWidget(discord);
        this.addRenderableWidget(youtube);
        this.addRenderableWidget(github);
        this.addRenderableWidget(twitter);
    }

    @Override
    public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTicks) {
        super.render(poseStack, mouseX, mouseY, partialTicks);

        var level = Minecraft.getInstance().level;
        if (level != null) {
            boolean h = HauntedHarvest.isHalloweenSeason(level);
            int x = (int) (this.width * 0.93f);
            RenderUtil.renderGuiItemRelative(Items.JACK_O_LANTERN.getDefaultInstance(), x, 16, this.itemRenderer,
                    (p, s) -> {
                    }, h ? LightTexture.FULL_BRIGHT : 0, OverlayTexture.NO_OVERLAY);
            if (ScreenUtil.isMouseWithin(x, 16, 16, 16, mouseX, mouseY)) {
                String c = h ? "gui.hauntedharvest.autumn_season_on" : "gui.hauntedharvest.autumn_season_off";
                this.renderTooltip(poseStack, this.font.split(Component.translatable(c).withStyle(ChatFormatting.GOLD), 200), mouseX, mouseY);
            }
        }
    }
}
