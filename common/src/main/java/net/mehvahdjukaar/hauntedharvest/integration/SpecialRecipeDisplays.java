package net.mehvahdjukaar.hauntedharvest.integration;

import net.mehvahdjukaar.hauntedharvest.HauntedHarvest;
import net.minecraft.client.RecipeBookCategories;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.ShapelessRecipe;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class SpecialRecipeDisplays {


    public static List<CraftingRecipe> createPumpkinDuplicate() {
        List<CraftingRecipe> recipes = new ArrayList<>();
        String group = "hauntedharvest.jei.carved_pumpkin";

        ItemStack output = new ItemStack(net.mehvahdjukaar.hauntedharvest.reg.ModRegistry.CARVED_PUMPKIN.get());
        CompoundTag com = new CompoundTag();

        var pixels = new long[]{2238290114314764288L, 3458817360039263256L, 4330272718253469696L, 16785168L};
        com.putLongArray("Pixels", pixels);
        output.addTagElement("BlockEntityTag", com);

        Ingredient emptyBoard = Ingredient.of(net.mehvahdjukaar.hauntedharvest.reg.ModTags.CARVABLE_PUMPKINS);
        Ingredient fullBoard = Ingredient.of(output);
        NonNullList<Ingredient> inputs = NonNullList.of(Ingredient.EMPTY, fullBoard, emptyBoard);
        ResourceLocation id = HauntedHarvest.res("jei_carved_pumpkin");
        ShapelessRecipe recipe = new ShapelessRecipe(id, group, CraftingBookCategory.BUILDING, output, inputs);
        recipes.add(recipe);

        return recipes;
    }

    public static List<CraftingRecipe> createJackOLantern() {
        List<CraftingRecipe> recipes = new ArrayList<>();
        String group = "hauntedharvest.jei.jack_o_lantern";

        ItemStack output = new ItemStack(net.mehvahdjukaar.hauntedharvest.reg.ModRegistry.JACK_O_LANTERN.get());
        CompoundTag com = new CompoundTag();
        var pixels = new long[]{4499109221882658816L, 2017679119407127804L, 4537409593239146464L, 26388795002096L};
        com.putLongArray("Pixels", pixels);
        output.addTagElement("BlockEntityTag", com);

        NonNullList<Ingredient> inputs = NonNullList.of(Ingredient.EMPTY, Ingredient.of(Items.TORCH), Ingredient.of(net.mehvahdjukaar.hauntedharvest.reg.ModRegistry.CARVED_PUMPKIN.get()));
        ResourceLocation id = HauntedHarvest.res("jei_jack_o_lantern");
        ShapelessRecipe recipe = new ShapelessRecipe(id, group, CraftingBookCategory.BUILDING, output, inputs);
        recipes.add(recipe);

        return recipes;
    }


    public static void registerCraftingRecipes(Consumer<List<CraftingRecipe>> registry) {
        for (var c : RecipeBookCategories.AGGREGATE_CATEGORIES.get(RecipeBookCategories.CRAFTING_SEARCH)) {
            registerRecipes(c, registry);
        }
    }

    public static void registerRecipes(RecipeBookCategories category, Consumer<List<CraftingRecipe>> registry) {

        if (category == RecipeBookCategories.CRAFTING_BUILDING_BLOCKS) {
            if (net.mehvahdjukaar.hauntedharvest.configs.CommonConfigs.CARVED_PUMPKINS_ENABLED.get()) {
                registry.accept(createJackOLantern());
                registry.accept(createPumpkinDuplicate());
            }
        }
    }
}
