package net.mehvahdjukaar.hauntedharvest.integration.forge.configured;


import com.mojang.blaze3d.vertex.PoseStack;
import com.mrcrayfish.configured.client.util.ScreenUtil;
import net.mehvahdjukaar.hauntedharvest.HauntedHarvest;
import net.mehvahdjukaar.hauntedharvest.reg.ModRegistry;
import net.mehvahdjukaar.moonlight.api.integration.configured.CustomConfigScreen;
import net.mehvahdjukaar.moonlight.api.integration.configured.CustomConfigSelectScreen;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.fml.config.ModConfig;

//credits to MrCrayfish's Configured Mod
public class ModConfigScreen extends CustomConfigScreen {


    public ModConfigScreen(CustomConfigSelectScreen parent, ModConfig config) {
        super(parent, config);
    }

    public ModConfigScreen(String modId, ItemStack mainIcon, ResourceLocation background, Component title, Screen parent, ModConfig config) {
        super(modId, mainIcon, background, title, parent, config);
    }

    @Override
    public boolean hasFancyBooleans() {
        return true;
    }

    @Override
    public void onSave() {
    }

    @Override
    public CustomConfigScreen createSubScreen(Component title) {
        return new ModConfigScreen(this.modId, this.mainIcon, this.background, title, this, this.config);
    }

    @Override
    public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTicks) {
        super.render(poseStack, mouseX, mouseY, partialTicks);

        var level = Minecraft.getInstance().level;
        if (level != null && HauntedHarvest.isHalloweenSeason(level)) {
            int x = (int) (this.width * 0.93f);
            this.itemRenderer.renderAndDecorateFakeItem(Items.JACK_O_LANTERN.getDefaultInstance(), x, 16);
            if (ScreenUtil.isMouseWithin(x, 16, 16, 16, mouseX, mouseY)) {
                this.renderTooltip(poseStack, this.font.split(Component.translatable("gui.hauntedharvest.autumn_season_on").withStyle(ChatFormatting.GOLD), 200), mouseX, mouseY);
            }
        }
    }
}