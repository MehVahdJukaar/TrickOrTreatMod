package net.mehvahdjukaar.hauntedharvest.integration.fabric;

import com.nhoryzon.mc.farmersdelight.FarmersDelightMod;
import com.nhoryzon.mc.farmersdelight.block.BuddingTomatoBlock;
import com.nhoryzon.mc.farmersdelight.block.TomatoVineBlock;
import com.nhoryzon.mc.farmersdelight.item.ConsumableItem;
import com.nhoryzon.mc.farmersdelight.registry.BlocksRegistry;
import com.nhoryzon.mc.farmersdelight.registry.EffectsRegistry;
import net.mehvahdjukaar.hauntedharvest.reg.ModFoods;
import net.mehvahdjukaar.hauntedharvest.reg.ModRegistry;
import net.mehvahdjukaar.hauntedharvest.reg.ModTabs;
import net.mehvahdjukaar.moonlight.api.platform.RegHelper;
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

import java.util.function.Supplier;

import static net.mehvahdjukaar.hauntedharvest.reg.ModRegistry.*;

public class FDCompatImpl {
    public static void init() {
        RegHelper.addItemsToTabsRegistration(FDCompatImpl::addItemToTabsEvent);
    }

    public static void addItemToTabsEvent(RegHelper.ItemToTabEvent event) {
        ModTabs.after(event, Items.BREAD, CreativeModeTabs.FOOD_AND_DRINKS, ModRegistry.CORN_NAME, CORNBREAD);
        ModTabs.after(event, Items.BEETROOT_SOUP, CreativeModeTabs.FOOD_AND_DRINKS, ModRegistry.CORN_NAME, SUCCOTASH);
        ModTabs.add(event, CreativeModeTabs.BUILDING_BLOCKS, ModRegistry.CORN_NAME, CORN_CRATE);

    }

    public static final FoodProperties SUCCOTASH_FOOD = new FoodProperties.Builder()
            .nutrition(12)
            .saturationMod(0.8F)
            .effect(new MobEffectInstance(EffectsRegistry.NOURISHMENT.get(), 3600, 0), 1.0F)
            .build();

    public static final Supplier<Block> CORN_CRATE = regWithItem(
            "corn_crate", () ->
                    new Block(BlockBehaviour.Properties.copy(Blocks.OAK_WOOD)
                            .strength(2.0F, 3.0F)
                            .sound(SoundType.WOOD)), new Item.Properties());

    public static final Supplier<Item> CORNBREAD = regItem(
            "cornbread", () -> new ConsumableItem(new Item.Properties()
                    .food(ModFoods.CORNBREAD), false));

    public static final Supplier<Item> SUCCOTASH = regItem(
            "succotash", () -> new ConsumableItem(bowlFoodItem(SUCCOTASH_FOOD), true));

    public static Item.Properties bowlFoodItem(FoodProperties food) {
        return new Item.Properties().food(food).craftRemainder(Items.BOWL).stacksTo(16);
    }

    public static BlockState getTomato(RandomSource randomSource) {
        int age = randomSource.nextInt(4);
        if (randomSource.nextBoolean()) {
            return BlocksRegistry.BUDDING_TOMATO_CROP.get().defaultBlockState().setValue(BuddingTomatoBlock.AGE, age);
        } else {
            return BlocksRegistry.TOMATO_CROP.get().defaultBlockState().setValue(TomatoVineBlock.VINE_AGE, age);
        }
    }
}
