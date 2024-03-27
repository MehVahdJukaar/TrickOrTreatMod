package net.mehvahdjukaar.hauntedharvest.integration;

import com.google.common.base.Suppliers;
import com.ibm.icu.impl.Assert;
import dev.architectury.injectables.annotations.ExpectPlatform;
import net.mehvahdjukaar.hauntedharvest.reg.ModFoods;
import net.mehvahdjukaar.hauntedharvest.reg.ModRegistry;
import net.mehvahdjukaar.hauntedharvest.reg.ModTabs;
import net.mehvahdjukaar.moonlight.api.platform.RegHelper;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffect;
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
import org.jetbrains.annotations.Nullable;
import vectorwing.farmersdelight.common.block.BuddingTomatoBlock;
import vectorwing.farmersdelight.common.block.TomatoVineBlock;
import vectorwing.farmersdelight.common.item.ConsumableItem;
import vectorwing.farmersdelight.common.registry.ModBlocks;
import vectorwing.farmersdelight.common.registry.ModEffects;
import vectorwing.farmersdelight.common.registry.ModItems;

import java.util.function.Supplier;

import static net.mehvahdjukaar.hauntedharvest.reg.ModRegistry.regItem;
import static net.mehvahdjukaar.hauntedharvest.reg.ModRegistry.regWithItem;

public class FDCompat {

    public static void init() {
        RegHelper.addItemsToTabsRegistration(FDCompat::addItemToTabsEvent);
    }
    private static final Supplier<Block> BUDDING_TOMATO_CROP = make("farmersdelight:budding_tomatoes", BuiltInRegistries.BLOCK);
    private static final Supplier<Block> TOMATO_CROP = make("farmersdelight:tomatoes", BuiltInRegistries.BLOCK);
    public static final Supplier<MobEffect> NOURISHMENT = make("farmersdelight:nourishment", BuiltInRegistries.MOB_EFFECT);

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
            return TOMATO_CROP.get().defaultBlockState().setValue(TomatoVineBlock.VINE_AGE, age);
        }
    }

    public static final FoodProperties SUCCOTASH_FOOD = makeFood();

    @ExpectPlatform
    private static FoodProperties makeFood() {
    throw new AssertionError();
    }

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



    private static <T> Supplier<@Nullable T> make(String name, Registry<T> registry) {
        return Suppliers.memoize(() -> registry.getOptional(new ResourceLocation(name)).orElse(null));
    }
}
