package net.mehvahdjukaar.hauntedharvest.integration;

import dev.emi.emi.EmiPort;
import dev.emi.emi.api.EmiEntrypoint;
import dev.emi.emi.api.EmiPlugin;
import dev.emi.emi.api.EmiRegistry;
import dev.emi.emi.api.recipe.*;
import dev.emi.emi.api.stack.Comparison;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.TextWidget;
import dev.emi.emi.api.widget.WidgetHolder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.ShapelessRecipe;
import net.minecraft.world.level.block.Blocks;

import java.util.List;

@EmiEntrypoint
public class EMICompat implements EmiPlugin {

    @Override
    public void register(EmiRegistry registry) {
        SpecialRecipeDisplays.registerCraftingRecipes(recipes -> recipes.stream().map(rh -> {
                    var r = rh.value();
                    return new EmiCraftingRecipe(
                            r.getIngredients().stream().map(EmiIngredient::of).toList(),
                            EmiStack.of(r.getResultItem(null)),
                            rh.id(),
                            r instanceof ShapelessRecipe);
                }
        ).forEach(registry::addRecipe));

  }


}