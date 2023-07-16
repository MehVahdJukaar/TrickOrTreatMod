package net.mehvahdjukaar.hauntedharvest.integration.fabric.mod_menu;

import net.mehvahdjukaar.hauntedharvest.HauntedHarvest;
import net.mehvahdjukaar.hauntedharvest.configs.CommonConfigs;
import net.mehvahdjukaar.hauntedharvest.reg.ModRegistry;
import net.mehvahdjukaar.moonlight.api.client.gui.LinkButton;
import net.mehvahdjukaar.moonlight.api.platform.configs.fabric.FabricConfigListScreen;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class ModConfigSelectScreen extends FabricConfigListScreen {

    public ModConfigSelectScreen(Screen parent) {
        super(HauntedHarvest.MOD_ID, ModRegistry.GRIM_APPLE.get().getDefaultInstance(),
                Component.literal(ChatFormatting.GOLD + "Haunted Harvest Configs"),
                new ResourceLocation("textures/block/cracked_stone_bricks.png"),
                parent, CommonConfigs.SPEC);
    }

    @Override
    protected void addExtraButtons() {

        int y = this.height - 27;
        int centerX = this.width / 2;

        this.addRenderableWidget(Button.builder(CommonComponents.GUI_BACK, (buttonx) -> {
            this.minecraft.setScreen(this.parent);
        }).bounds(centerX - 45, y, 90, 20).build());
        this.addRenderableWidget(LinkButton.create(this, centerX - 45 - 22, y, 3, 1, "https://www.patreon.com/user?u=53696377", "Support me on Patreon :D"));
        this.addRenderableWidget(LinkButton.create(this, centerX - 45 - 44, y, 2, 2, "https://ko-fi.com/mehvahdjukaar", "Donate a Coffee"));
        this.addRenderableWidget(LinkButton.create(this, centerX - 45 - 66, y, 1, 2, "https://www.curseforge.com/minecraft/mc-mods/haunted-harvest", "CurseForge Page"));
        this.addRenderableWidget(LinkButton.create(this, centerX - 45 - 88, y, 0, 2, "https://github.com/MehVahdJukaar/Supplementaries/TrickOrTreatMod", "Mod Wiki"));
        this.addRenderableWidget(LinkButton.create(this, centerX + 45 + 2, y, 1, 1, "https://discord.com/invite/qdKRTDf8Cv", "Mod Discord"));
        this.addRenderableWidget(LinkButton.create(this, centerX + 45 + 2 + 22, y, 0, 1, "https://www.youtube.com/watch?v=LSPNAtAEn28&t=1s", "Youtube Channel"));
        this.addRenderableWidget(LinkButton.create(this, centerX + 45 + 2 + 44, y, 2, 1, "https://twitter.com/Supplementariez?s=09", "Twitter Page"));
        this.addRenderableWidget(LinkButton.create(this, centerX + 45 + 2 + 66, y, 3, 2, "https://www.akliz.net/supplementaries", "Need a server? Get one with Akliz"));

    }

}
