package net.mehvahdjukaar.hauntedharvest.integration;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.RecipeTypes;
import mezz.jei.api.registration.IRecipeRegistration;
import net.mehvahdjukaar.moonlight.api.platform.PlatHelper;
import net.mehvahdjukaar.supplementaries.Supplementaries;
import net.minecraft.resources.ResourceLocation;

@JeiPlugin
public class JEICompat implements IModPlugin {

    private static final ResourceLocation ID = Supplementaries.res("jei_plugin");

    @Override
    public ResourceLocation getPluginUid() {
        return ID;
    }

    @Override
    public void registerRecipes(IRecipeRegistration registry) {
        if (!PlatHelper.isModLoaded("roughly_enough_items")) {
            SpecialRecipeDisplays.registerCraftingRecipes(r -> registry.addRecipes(RecipeTypes.CRAFTING, r));
        }
    }

}