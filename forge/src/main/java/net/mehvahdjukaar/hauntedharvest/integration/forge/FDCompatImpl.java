package net.mehvahdjukaar.hauntedharvest.integration.forge;

import net.mehvahdjukaar.hauntedharvest.reg.ModFoods;
import net.mehvahdjukaar.hauntedharvest.reg.ModRegistry;
import net.mehvahdjukaar.hauntedharvest.reg.ModTabs;
import net.mehvahdjukaar.moonlight.api.platform.RegHelper;
import net.mehvahdjukaar.moonlight.core.misc.forge.ModLootConditions;
import net.mehvahdjukaar.moonlight.forge.MoonlightForgeClient;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import vectorwing.farmersdelight.common.block.BuddingTomatoBlock;
import vectorwing.farmersdelight.common.block.TomatoVineBlock;
import vectorwing.farmersdelight.common.item.ConsumableItem;
import vectorwing.farmersdelight.common.registry.ModBlocks;
import vectorwing.farmersdelight.common.registry.ModEffects;
import vectorwing.farmersdelight.common.registry.ModItems;

import java.util.function.Supplier;

import static net.mehvahdjukaar.hauntedharvest.reg.ModRegistry.regItem;
import static net.mehvahdjukaar.hauntedharvest.reg.ModRegistry.regWithItem;

public class FDCompatImpl {

    public static void init() {
        RegHelper.addItemsToTabsRegistration(FDCompatImpl::addItemToTabsEvent);
    }

    public static void addItemToTabsEvent(RegHelper.ItemToTabEvent event) {
        ModTabs.after(event, Items.BREAD, CreativeModeTabs.FOOD_AND_DRINKS, ModRegistry.CORN_NAME, CORNBREAD);
        ModTabs.after(event, Items.BEETROOT_SOUP, CreativeModeTabs.FOOD_AND_DRINKS, ModRegistry.CORN_NAME, SUCCOTASH);
        ModTabs.add(event, CreativeModeTabs.BUILDING_BLOCKS, ModRegistry.CORN_NAME, CORN_CRATE);
    }

    public static BlockState getTomato(RandomSource randomSource) {
        int age = randomSource.nextInt(4);
        if (randomSource.nextBoolean()) {
            return ModBlocks.BUDDING_TOMATO_CROP.get().defaultBlockState().setValue(BuddingTomatoBlock.AGE, age);
        } else {
            return ModBlocks.TOMATO_CROP.get().defaultBlockState().setValue(TomatoVineBlock.VINE_AGE, age);
        }
    }

    public static final FoodProperties SUCCOTASH_FOOD = new FoodProperties.Builder()
            .nutrition(12)
            .saturationMod(0.8F)
            .effect(() -> new MobEffectInstance(ModEffects.NOURISHMENT.get(), 3600, 0), 1.0F)
            .build();

    public static final Supplier<Block> CORN_CRATE = regWithItem(
            "corn_crate", () ->
                    new Block(BlockBehaviour.Properties.copy(Blocks.OAK_PLANKS)
                            .strength(2.0F, 3.0F)
                            .sound(SoundType.WOOD)), new Item.Properties());

    public static final Supplier<Item> CORNBREAD = regItem(
            "cornbread", () -> new ConsumableItem(ModItems.foodItem(ModFoods.CORNBREAD),
                    false));

    public static final Supplier<Item> SUCCOTASH = regItem(
            "succotash", () -> new ConsumableItem(ModItems.bowlFoodItem(SUCCOTASH_FOOD),
                    true));


}
