package net.mehvahdjukaar.hauntedharvest.integration.forge;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.RecipeTypes;
import mezz.jei.api.registration.IRecipeRegistration;
import net.mehvahdjukaar.harvestseason.HarvestSeason;
import net.mehvahdjukaar.harvestseason.reg.ModRegistry;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.ShapelessRecipe;

import java.util.ArrayList;
import java.util.List;

@JeiPlugin
public class JEICompat implements IModPlugin {

    private static final ResourceLocation ID = HarvestSeason.res("jei_plugin");

    @Override
    public ResourceLocation getPluginUid() {
        return ID;
    }

    @Override
    public void registerRecipes(IRecipeRegistration registry) {
        //if (RegistryConfigs.BLACKBOARD_ENABLED.get()) {
        registry.addRecipes(RecipeTypes.CRAFTING, createPumpkinDuplicate());
        registry.addRecipes(RecipeTypes.CRAFTING, createJackOLantern());
        // }
    }


    public static List<CraftingRecipe> createPumpkinDuplicate() {
        List<CraftingRecipe> recipes = new ArrayList<>();
        String group = "harvestseason.jei.carved_pumpkin";

        ItemStack output = new ItemStack(ModRegistry.MOD_CARVED_PUMPKIN.get());
        CompoundTag com = new CompoundTag();

        var pixels = new long[]{2238290114314764288L, 3458817360039263256L, 4330272718253469696L, 16785168L};
        com.putLongArray("Pixels", pixels);
        output.addTagElement("BlockEntityTag", com);

        Ingredient emptyBoard = Ingredient.of(HarvestSeason.CARVABLE_PUMPKINS);
        Ingredient fullBoard = Ingredient.of(output);
        NonNullList<Ingredient> inputs = NonNullList.of(Ingredient.EMPTY, fullBoard, emptyBoard);
        ResourceLocation id = new ResourceLocation(HarvestSeason.MOD_ID, "jei_carved_pumpkin");
        ShapelessRecipe recipe = new ShapelessRecipe(id, group, output, inputs);
        recipes.add(recipe);

        return recipes;
    }

    public static List<CraftingRecipe> createJackOLantern() {
        List<CraftingRecipe> recipes = new ArrayList<>();
        String group = "harvestseason.jei.jack_o_lantern";

        ItemStack output = new ItemStack(ModRegistry.MOD_JACK_O_LANTERN_ITEM.get());
        CompoundTag com = new CompoundTag();
        var pixels = new long[]{4499109221882658816L, 2017679119407127804L, 4537409593239146464L, 26388795002096L};
        com.putLongArray("Pixels", pixels);
        output.addTagElement("BlockEntityTag", com);

        NonNullList<Ingredient> inputs = NonNullList.of(Ingredient.EMPTY, Ingredient.of(Items.TORCH), Ingredient.of(ModRegistry.MOD_CARVED_PUMPKIN_ITEM.get()));
        ResourceLocation id = new ResourceLocation(HarvestSeason.MOD_ID, "jei_jack_o_lantern");
        ShapelessRecipe recipe = new ShapelessRecipe(id, group, output, inputs);
        recipes.add(recipe);

        return recipes;
    }

}