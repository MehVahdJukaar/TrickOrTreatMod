package net.mehvahdjukaar.hauntedharvest.integration.forge;

import net.mehvahdjukaar.harvestseason.reg.ModFood;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.Material;
import vectorwing.farmersdelight.FarmersDelight;
import vectorwing.farmersdelight.common.item.ConsumableItem;
import vectorwing.farmersdelight.common.registry.ModEffects;
import vectorwing.farmersdelight.common.registry.ModItems;

import java.util.function.Supplier;

import static net.mehvahdjukaar.harvestseason.reg.ModRegistry.regItem;
import static net.mehvahdjukaar.harvestseason.reg.ModRegistry.regWithItem;

public class FDCompatImpl {
    public static void init() {

    }

    public static final FoodProperties SUCCOTASH_FOOD = new FoodProperties.Builder()
            .nutrition(12)
            .saturationMod(0.8F)
            .effect(() -> new MobEffectInstance(ModEffects.NOURISHMENT.get(), 3600, 0), 1.0F)
            .build();

    public static final Supplier<Block> CORN_CRATE = regWithItem(
            "corn_crate", () ->
                    new Block(BlockBehaviour.Properties.of(Material.WOOD)
                            .strength(2.0F, 3.0F)
                            .sound(SoundType.WOOD)),
            FarmersDelight.CREATIVE_TAB
    );
    public static final Supplier<Item> CORNBREAD = regItem(
            "cornbread", () -> new ConsumableItem(ModItems.foodItem(ModFood.CORNBREAD), false)
    );

    public static final Supplier<Item> SUCCOTASH = regItem(
            "succotash", () -> new ConsumableItem(ModItems.bowlFoodItem(SUCCOTASH_FOOD), true)
    );


}
