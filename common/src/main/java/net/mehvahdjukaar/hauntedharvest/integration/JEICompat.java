package net.mehvahdjukaar.hauntedharvest.integration;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.RecipeTypes;
import mezz.jei.api.registration.IRecipeRegistration;
import net.mehvahdjukaar.hauntedharvest.HauntedHarvest;
import net.mehvahdjukaar.moonlight.api.platform.PlatHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.RecipeHolder;

import java.util.List;

@JeiPlugin
public class JEICompat implements IModPlugin {

    private static final ResourceLocation ID = HauntedHarvest.res("jei_plugin");

    @Override
    public ResourceLocation getPluginUid() {
        return ID;
    }

    @Override
    public void registerRecipes(IRecipeRegistration registry) {
        if (!PlatHelper.isModLoaded("roughly_enough_items") && !PlatHelper.isModLoaded("emi")) {
            SpecialRecipeDisplays.registerCraftingRecipes(r -> registry.addRecipes(RecipeTypes.CRAFTING,
                    (List<RecipeHolder<CraftingRecipe>>) (List) r));
        }
    }

}