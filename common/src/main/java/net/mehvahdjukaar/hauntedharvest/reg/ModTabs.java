package net.mehvahdjukaar.hauntedharvest.reg;

import net.mehvahdjukaar.hauntedharvest.HauntedHarvest;
import net.mehvahdjukaar.hauntedharvest.configs.CommonConfigs;
import net.mehvahdjukaar.hauntedharvest.integration.CompatHandler;
import net.mehvahdjukaar.moonlight.api.platform.PlatHelper;
import net.mehvahdjukaar.moonlight.api.platform.RegHelper;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
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

    public static final Supplier<CreativeModeTab> MOD_TAB = !CommonConfigs.CREATIVE_TAB.get() ? null :
            RegHelper.registerCreativeModeTab(HauntedHarvest.res(HauntedHarvest.MOD_ID),
                    b -> b.icon(() -> ModRegistry.GRIM_APPLE.get().getDefaultInstance()));


    public static void addItemsToTab(RegHelper.ItemToTabEvent event) {
        after(event, Items.ENCHANTED_GOLDEN_APPLE, CreativeModeTabs.FOOD_AND_DRINKS,ModRegistry.GRIM_APPLE_NAME,
                ModRegistry.ROTTEN_APPLE,
                ModRegistry.GRIM_APPLE);
        before(event,Items.WHEAT, CreativeModeTabs.INGREDIENTS,ModRegistry.CORN_NAME,
                ModRegistry.COB_ITEM);
        after(event,Items.BEETROOT_SEEDS, CreativeModeTabs.NATURAL_BLOCKS,ModRegistry.CORN_NAME,
                ModRegistry.KERNELS);

    }


    public static void after(RegHelper.ItemToTabEvent event, TagKey<Item> target, CreativeModeTab tab, String key, Supplier<?>... items) {
        after(event, i -> i.is(target), tab, key, items);
    }

    public static void after(RegHelper.ItemToTabEvent event, Item target, CreativeModeTab tab, String key, Supplier<?>... items) {
        after(event, i -> i.is(target), tab, key, items);
    }

    public static void after(RegHelper.ItemToTabEvent event, Predicate<ItemStack> targetPred, CreativeModeTab tab, String key, Supplier<?>... items) {
        if (CommonConfigs.isEnabled(key)) {
            ItemLike[] entries = Arrays.stream(items).map((s -> (ItemLike) (s.get()))).toArray(ItemLike[]::new);
            event.addAfter(tab, targetPred, entries);
        }
    }

    public static void before(RegHelper.ItemToTabEvent event, Item target, CreativeModeTab tab, String key, Supplier<?>... items) {
        before(event, i -> i.is(target), tab, key, items);
    }

    public static void before(RegHelper.ItemToTabEvent event, Predicate<ItemStack> targetPred, CreativeModeTab tab, String key, Supplier<?>... items) {
        if (CommonConfigs.isEnabled(key)) {
            ItemLike[] entries = Arrays.stream(items).map(s -> (ItemLike) s.get()).toArray(ItemLike[]::new);
            event.addBefore(tab, targetPred, entries);
        }
    }

    public static void add(RegHelper.ItemToTabEvent event, CreativeModeTab tab, String key, Supplier<?>... items) {
        if (CommonConfigs.isEnabled(key)) {
            ItemLike[] entries = Arrays.stream(items).map((s -> (ItemLike) (s.get()))).toArray(ItemLike[]::new);
            event.add(tab, entries);
        }
    }

    public static boolean hasBlock(String name) {
        if (CompatHandler.AUTUMNITY_INSTALLED) return true;
        for (var id : PlatHelper.getInstalledMods()) {
            if (BuiltInRegistries.BLOCK.getOptional(new ResourceLocation(id, name)).isPresent()) return true;
        }
        return false;
    }

}
