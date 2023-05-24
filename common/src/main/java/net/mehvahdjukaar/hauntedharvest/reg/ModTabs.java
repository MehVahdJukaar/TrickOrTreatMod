package net.mehvahdjukaar.hauntedharvest.reg;

import net.mehvahdjukaar.hauntedharvest.HauntedHarvest;
import net.mehvahdjukaar.hauntedharvest.configs.CommonConfigs;
import net.mehvahdjukaar.moonlight.api.platform.PlatHelper;
import net.mehvahdjukaar.moonlight.api.platform.RegHelper;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
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
                    b -> b.icon(() -> ModRegistry.DEATH_APPLE.get().getDefaultInstance()));


    public static void addItemsToTab(RegHelper.ItemToTabEvent event) {
//hasBlock("redstone_jack_o_lantern")
    }


    private static void after(RegHelper.ItemToTabEvent event, TagKey<Item> target, CreativeModeTab tab, String key, Supplier<?>... items) {
        after(event, i -> i.is(target), tab, key, items);
    }

    private static void after(RegHelper.ItemToTabEvent event, Item target, CreativeModeTab tab, String key, Supplier<?>... items) {
        after(event, i -> i.is(target), tab, key, items);
    }

    private static void after(RegHelper.ItemToTabEvent event, Predicate<ItemStack> targetPred, CreativeModeTab tab, String key, Supplier<?>... items) {
        if (CommonConfigs.isEnabled(key)) {
            ItemLike[] entries = Arrays.stream(items).map((s -> (ItemLike) (s.get()))).toArray(ItemLike[]::new);
            event.addAfter(tab, targetPred, entries);
        }
    }

    private static void before(RegHelper.ItemToTabEvent event, Item target, CreativeModeTab tab, String key, Supplier<?>... items) {
        before(event, i -> i.is(target), tab, key, items);
    }

    private static void before(RegHelper.ItemToTabEvent event, Predicate<ItemStack> targetPred, CreativeModeTab tab, String key, Supplier<?>... items) {
        if (CommonConfigs.isEnabled(key)) {
            ItemLike[] entries = Arrays.stream(items).map(s -> (ItemLike) s.get()).toArray(ItemLike[]::new);
            event.addBefore(tab, targetPred, entries);
        }
    }

    private static void add(RegHelper.ItemToTabEvent event, CreativeModeTab tab, String key, Supplier<?>... items) {
        if (CommonConfigs.isEnabled(key)) {
            ItemLike[] entries = Arrays.stream(items).map((s -> (ItemLike) (s.get()))).toArray(ItemLike[]::new);
            event.add(tab, entries);
        }
    }

    private static boolean hasBlock(String name) {
        if (HauntedHarvest.AUTUMNITY_INSTALLED) return true;
        for (var id : PlatHelper.getInstalledMods()) {
            if (BuiltInRegistries.BLOCK.getOptional(new ResourceLocation(id, name)).isPresent()) return true;
        }
        return false;
    }

}
