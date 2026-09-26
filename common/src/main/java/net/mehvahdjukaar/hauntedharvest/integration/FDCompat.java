package net.mehvahdjukaar.hauntedharvest.integration;

import com.google.common.base.Suppliers;
import net.mehvahdjukaar.hauntedharvest.reg.ModFoods;
import net.mehvahdjukaar.hauntedharvest.reg.ModRegistry;
import net.mehvahdjukaar.hauntedharvest.reg.ModTabs;
import net.mehvahdjukaar.moonlight.api.platform.RegHelper;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.util.RandomSource;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import vectorwing.farmersdelight.common.FoodValues;
import vectorwing.farmersdelight.common.block.BuddingTomatoBlock;
import vectorwing.farmersdelight.common.block.TomatoBlock;
import vectorwing.farmersdelight.common.item.ConsumableItem;

import java.util.function.Supplier;

import static net.mehvahdjukaar.hauntedharvest.reg.ModRegistry.regItem;
import static net.mehvahdjukaar.hauntedharvest.reg.ModRegistry.regWithItem;

public class FDCompat {

    public static void init() {
        RegHelper.addItemsToTabsRegistration(FDCompat::addItemToTabsEvent);
    }

    private static final Supplier<Block> BUDDING_TOMATO_CROP = obj("farmersdelight:budding_tomatoes", BuiltInRegistries.BLOCK);
    private static final Supplier<Block> TOMATO_CROP = obj("farmersdelight:tomatoes", BuiltInRegistries.BLOCK);

    public static void addItemToTabsEvent(RegHelper.ItemToTabEvent event) {
        ModTabs.after(event, Items.BREAD, CreativeModeTabs.FOOD_AND_DRINKS, ModRegistry.CORN_NAME, CORNBREAD);
        ModTabs.after(event, Items.BEETROOT_SOUP, CreativeModeTabs.FOOD_AND_DRINKS, ModRegistry.CORN_NAME, SUCCOTASH);
        ModTabs.add(event, CreativeModeTabs.BUILDING_BLOCKS, ModRegistry.CORN_NAME, CORN_CRATE);
    }

    public static BlockState getTomato(RandomSource randomSource) {
        int age = randomSource.nextInt(4);
        if (randomSource.nextBoolean()) {
            return BUDDING_TOMATO_CROP.get().defaultBlockState().setValue(BuddingTomatoBlock.AGE, age);
        } else {
            return TOMATO_CROP.get().defaultBlockState().setValue(TomatoBlock.VINE_AGE, age);
        }
    }

    public static final FoodProperties SUCCOTASH_FOOD = new FoodProperties.Builder()
            .nutrition(12)
            .saturationModifier(0.8F)
            .build();

    public static final Supplier<Block> CORN_CRATE = regWithItem(
            "corn_crate", Block::new, BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_PLANKS)
                    .strength(2.0F, 3.0F)
                    .sound(SoundType.WOOD), new Item.Properties());

    public static final Supplier<Item> CORNBREAD = regItem(
            "cornbread", p -> new ConsumableItem(p, false),
            new Item.Properties().food(ModFoods.CORNBREAD));

    public static final Supplier<Item> SUCCOTASH = regItem(
            "succotash", p -> new ConsumableItem(p, true),
            new Item.Properties().food(SUCCOTASH_FOOD, FoodValues.ConsumableValues.NOURISHMENT_MEDIUM_DURATION)
                    .craftRemainder(Items.BOWL).stacksTo(16));


    private static <T> Supplier<@Nullable T> obj(String name, Registry<T> registry) {
        return Suppliers.memoize(() -> registry.getOptional(Identifier.parse(name)).orElseThrow());
    }
}
