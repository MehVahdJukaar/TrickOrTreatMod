package net.mehvahdjukaar.hauntedharvest.integration;

import net.mehvahdjukaar.hauntedharvest.HauntedHarvest;
import net.mehvahdjukaar.hauntedharvest.blocks.PumpkinType;
import net.mehvahdjukaar.hauntedharvest.items.components.PumpkinCarvingData;
import net.mehvahdjukaar.hauntedharvest.reg.ModRegistry;
import net.mehvahdjukaar.hauntedharvest.reg.ModTags;
import net.mehvahdjukaar.moonlight.api.platform.ClientHelper;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.*;

import java.util.List;
import java.util.function.Consumer;

public class SpecialRecipeDisplays {

    private static final Recipe.CommonInfo DISPLAY_INFO = new Recipe.CommonInfo(false);

    private static RecipeHolder<CraftingRecipe> createPumpkinDuplicate() {
        String group = "hauntedharvest.jei.carved_pumpkin";

        ItemStack output = new ItemStack(ModRegistry.CARVED_PUMPKIN.get());
        var pixels = new long[]{2238290114314764288L, 3458817360039263256L, 4330272718253469696L, 16785168L};
        output.set(ModRegistry.PUMPKIN_CARVING.get(), PumpkinCarvingData.of(
                PumpkinCarvingData.unpack(pixels), PumpkinType.NORMAL, false));

        Ingredient carvable = Ingredient.of(BuiltInRegistries.ITEM.getOrThrow(ModTags.CARVABLE_PUMPKINS));
        List<Ingredient> inputs = List.of(Ingredient.of(output.getItem()), carvable);
        ShapelessRecipe recipe = new ShapelessRecipe(DISPLAY_INFO, new CraftingRecipe.CraftingBookInfo(CraftingBookCategory.BUILDING, group),
                ItemStackTemplate.fromNonEmptyStack(output), inputs);
        return new RecipeHolder<>(ResourceKey.create(Registries.RECIPE, HauntedHarvest.res("jei_carved_pumpkin")), recipe);
    }

    private static RecipeHolder<CraftingRecipe> createJackOLantern() {
        String group = "hauntedharvest.jei.jack_o_lantern";

        ItemStack output = new ItemStack(ModRegistry.JACK_O_LANTERN.get());
        var pixels = new long[]{4499109221882658816L, 2017679119407127804L, 4537409593239146464L, 26388795002096L};
        output.set(ModRegistry.PUMPKIN_CARVING.get(), PumpkinCarvingData.of(
                PumpkinCarvingData.unpack(pixels), PumpkinType.NORMAL, false));

        List<Ingredient> inputs = List.of(Ingredient.of(Items.TORCH), Ingredient.of(ModRegistry.CARVED_PUMPKIN.get()));
        ShapelessRecipe recipe = new ShapelessRecipe(DISPLAY_INFO, new CraftingRecipe.CraftingBookInfo(CraftingBookCategory.BUILDING, group),
                ItemStackTemplate.fromNonEmptyStack(output), inputs);
        return new RecipeHolder<>(ResourceKey.create(Registries.RECIPE, HauntedHarvest.res("jei_jack_o_lantern")), recipe);
    }


    public static void registerCraftingRecipes(Consumer<List<RecipeHolder<CraftingRecipe>>> registry) {
        //skip if the recipe got disabled
        var synced = ClientHelper.getSyncedRecipe(ResourceKey.create(Registries.RECIPE, HauntedHarvest.res("carved_pumpkin_duplicate")));
        if (synced == null) return;
        registry.accept(List.of(createJackOLantern(), createPumpkinDuplicate()));
    }
}
