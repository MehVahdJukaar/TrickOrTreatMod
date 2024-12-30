package net.mehvahdjukaar.hauntedharvest.items.crafting;

import net.mehvahdjukaar.hauntedharvest.blocks.PumpkinType;
import net.mehvahdjukaar.hauntedharvest.reg.ModRegistry;
import net.mehvahdjukaar.hauntedharvest.reg.ModTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;

public class ModCarvedPumpkinRecipe extends CustomRecipe {
    public ModCarvedPumpkinRecipe(CraftingBookCategory category) {
        super(category);
    }

    @Override
    public boolean matches(CraftingInput inv, Level level) {
        ItemStack itemstack = null;
        ItemStack itemstack1 = null;

        for (int i = 0; i < inv.size(); ++i) {
            ItemStack stack = inv.getItem(i);
            Item item = stack.getItem();
            if (item == ModRegistry.CARVED_PUMPKIN.get().asItem() && itemstack == null) {
                if (stack.has(ModRegistry.PUMPKIN_CARVING.get())) {
                    itemstack = stack;
                }
            } else if (PumpkinType.getFromTorch(item) != null || stack.is(ModTags.CARVABLE_PUMPKINS)) {
                if (itemstack1 != null) {
                    return false;
                }

                itemstack1 = stack;
            } else if (!stack.isEmpty()) return false;
        }

        return itemstack != null && itemstack1 != null;
    }

    @Override
    public ItemStack assemble(CraftingInput inv, HolderLookup.Provider provider) {
        Item jack = ModRegistry.CARVED_PUMPKIN.get().asItem();

        for (int i = 0; i < inv.size(); ++i) {
            PumpkinType b = PumpkinType.getFromTorch(inv.getItem(i).getItem());
            if (b != null) {
                jack = b.getPumpkin().asItem();
            }
        }
        for (int i = 0; i < inv.size(); ++i) {
            ItemStack stack = inv.getItem(i);
            if (stack.has(ModRegistry.PUMPKIN_CARVING.get())) {
                ItemStack s = new ItemStack(jack);
                s.setCount(1);
                s.set(ModRegistry.PUMPKIN_CARVING.get(), stack.get(ModRegistry.PUMPKIN_CARVING.get()));
                return s;
            }
        }
        return ItemStack.EMPTY;
    }

    @Override
    public NonNullList<ItemStack> getRemainingItems(CraftingInput inv) {
        NonNullList<ItemStack> stacks = NonNullList.withSize(inv.size(), ItemStack.EMPTY);
        for (int i = 0; i < inv.size(); ++i) {
            ItemStack stack = inv.getItem(i);
            Item item = stack.getItem();
            if (PumpkinType.getFromTorch(item) != null) {
                return stacks;
            }
        }
        for (int i = 0; i < stacks.size(); ++i) {
            ItemStack itemstack = inv.getItem(i);
            if (itemstack.has(ModRegistry.PUMPKIN_CARVING.get())) {
                ItemStack copy = itemstack.copy();
                copy.setCount(1);
                stacks.set(i, copy);
                return stacks;
            }
        }
        return stacks;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return width * height >= 2;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRegistry.CARVED_PUMPKIN_RECIPE.get();
    }


}
