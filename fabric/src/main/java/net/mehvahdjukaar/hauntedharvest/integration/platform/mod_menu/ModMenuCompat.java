package net.mehvahdjukaar.hauntedharvest.integration.platform.mod_menu;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import net.mehvahdjukaar.hauntedharvest.HauntedHarvest;
import net.mehvahdjukaar.moonlight.core.client.config.MoonlightConfigSelectScreen;
import net.minecraft.resources.ResourceLocation;

public class ModMenuCompat implements ModMenuApi {

    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return parent -> MoonlightConfigSelectScreen.create(HauntedHarvest.MOD_ID, parent,
                ResourceLocation.withDefaultNamespace("textures/block/cracked_stone_bricks.png"));
    }
}
