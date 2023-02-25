package net.mehvahdjukaar.hauntedharvest.integration.forge.configured;


import com.mojang.blaze3d.vertex.PoseStack;
import com.mrcrayfish.configured.api.IModConfig;
import com.mrcrayfish.configured.client.util.ScreenUtil;
import com.simibubi.create.foundation.block.ITE;
import net.mehvahdjukaar.hauntedharvest.HauntedHarvest;
import net.mehvahdjukaar.hauntedharvest.reg.ModRegistry;
import net.mehvahdjukaar.moonlight.api.integration.configured.CustomConfigScreen;
import net.mehvahdjukaar.moonlight.api.integration.configured.CustomConfigSelectScreen;
import net.mehvahdjukaar.moonlight.api.set.wood.WoodTypeRegistry;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.fml.config.ModConfig;

import java.util.HashMap;
import java.util.Map;

//credits to MrCrayfish's Configured Mod
public class ModConfigScreen extends CustomConfigScreen {


    private static final Map<String, ItemStack> ICONS = new HashMap<>();

    static {
        addIcon("trick or treating time", ModRegistry.CANDY_CORN.get());
        //addIcon("halloween season", Items.JACK_O_LANTERN);
        addIcon("mob pumpkins season", Items.ZOMBIE_HEAD);
        addIcon("pumpkin carving", Items.CARVED_PUMPKIN);
        addIcon("season mod compat", ModRegistry.COB_ITEM.get());
        addIcon("general", Items.SKELETON_SKULL);
        addIcon("splattered egg", Items.EGG);
        addIcon("carved pumpkin", Items.CARVED_PUMPKIN);
    }

    public ModConfigScreen(CustomConfigSelectScreen parent, IModConfig config) {
        super(parent, config);
        this.icons.putAll(ICONS);
    }

    public ModConfigScreen(String modId, ItemStack mainIcon, ResourceLocation background,
                           Component title, Screen parent, IModConfig config) {
        super(modId, mainIcon, background, title, parent, config);
        this.icons.putAll(ICONS);
    }


    private static void addIcon(String s, ItemLike i) {
        ICONS.put(s, i.asItem().getDefaultInstance());
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