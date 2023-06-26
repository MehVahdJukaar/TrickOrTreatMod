package net.mehvahdjukaar.hauntedharvest.reg;

import net.mehvahdjukaar.hauntedharvest.HauntedHarvest;
import net.mehvahdjukaar.hauntedharvest.configs.CommonConfigs;
import net.mehvahdjukaar.moonlight.api.misc.RegSupplier;
import net.mehvahdjukaar.moonlight.api.platform.RegHelper;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.*;
import net.minecraft.world.level.ItemLike;

import java.util.Arrays;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class ModTabs {

    public static void init() {
        RegHelper.addItemsToTabsRegistration(ModTabs::addItemsToTab);
    }

    public static final RegSupplier<CreativeModeTab> MOD_TAB = !CommonConfigs.CREATIVE_TAB.get() ? null :
            RegHelper.registerCreativeModeTab(HauntedHarvest.res(HauntedHarvest.MOD_ID),
                    b -> b.title(Component.translatable("itemGroup.hauntedharvest"))
                            .icon(() -> ModRegistry.GRIM_APPLE.get().getDefaultInstance()));


    public static void addItemsToTab(RegHelper.ItemToTabEvent event) {
        after(event, Items.ENCHANTED_GOLDEN_APPLE, CreativeModeTabs.FOOD_AND_DRINKS, ModRegistry.GRIM_APPLE_NAME,
                ModRegistry.ROTTEN_APPLE,
                ModRegistry.GRIM_APPLE);
        before(event, Items.WHEAT, CreativeModeTabs.INGREDIENTS, ModRegistry.CORN_NAME,
                ModRegistry.COB_ITEM);
        after(event, Items.BEETROOT_SEEDS, CreativeModeTabs.NATURAL_BLOCKS, ModRegistry.CORN_NAME,
                ModRegistry.KERNELS);

        after(event, Items.POISONOUS_POTATO, CreativeModeTabs.FOOD_AND_DRINKS, ModRegistry.POPCORN_NAME,
                ModRegistry.POP_CORN);
        after(event, Items.POISONOUS_POTATO, CreativeModeTabs.FOOD_AND_DRINKS, ModRegistry.CANDY_CORN_NAME,
                ModRegistry.CANDY_CORN);
        after(event, Items.POISONOUS_POTATO, CreativeModeTabs.FOOD_AND_DRINKS, ModRegistry.CORN_NAME,
                ModRegistry.COOKED_COB);

        after(event, i -> i.getItem() instanceof BlockItem bi && bi.getBlock().builtInRegistryHolder().is(BlockTags.CAMPFIRES),
                CreativeModeTabs.FUNCTIONAL_BLOCKS, ModRegistry.PAPER_BAG_NAME,
                ModRegistry.PAPER_BAG_ITEM);


    }


    public static void after(RegHelper.ItemToTabEvent event, TagKey<Item> target, ResourceKey<CreativeModeTab> tab, String key, Supplier<?>... items) {
        after(event, i -> i.is(target), tab, key, items);
    }

    public static void after(RegHelper.ItemToTabEvent event, Item target, ResourceKey<CreativeModeTab> tab, String key, Supplier<?>... items) {
        after(event, i -> i.is(target), tab, key, items);
    }

    public static void after(RegHelper.ItemToTabEvent event, Predicate<ItemStack> targetPred, ResourceKey<CreativeModeTab> tab, String key, Supplier<?>... items) {
        if (CommonConfigs.isEnabled(key)) {
            ItemLike[] entries = Arrays.stream(items).map((s -> (ItemLike) (s.get()))).toArray(ItemLike[]::new);
            if (MOD_TAB != null) tab = MOD_TAB.getHolder().unwrapKey().get();
            event.addAfter(tab, targetPred, entries);
        }
    }

    public static void before(RegHelper.ItemToTabEvent event, Item target, ResourceKey<CreativeModeTab> tab, String key, Supplier<?>... items) {
        before(event, i -> i.is(target), tab, key, items);
    }

    public static void before(RegHelper.ItemToTabEvent event, Predicate<ItemStack> targetPred, ResourceKey<CreativeModeTab> tab, String key, Supplier<?>... items) {
        if (CommonConfigs.isEnabled(key)) {
            ItemLike[] entries = Arrays.stream(items).map(s -> (ItemLike) s.get()).toArray(ItemLike[]::new);
            if (MOD_TAB != null) tab = MOD_TAB.getHolder().unwrapKey().get();
            event.addBefore(tab, targetPred, entries);
        }
    }

    public static void add(RegHelper.ItemToTabEvent event, ResourceKey<CreativeModeTab> tab, String key, Supplier<?>... items) {
        if (CommonConfigs.isEnabled(key)) {
            ItemLike[] entries = Arrays.stream(items).map((s -> (ItemLike) (s.get()))).toArray(ItemLike[]::new);
            event.add(tab, entries);
        }
    }

}
