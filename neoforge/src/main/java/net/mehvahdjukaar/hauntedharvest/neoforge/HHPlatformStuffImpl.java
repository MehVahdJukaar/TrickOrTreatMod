package net.mehvahdjukaar.hauntedharvest.neoforge;

import net.mehvahdjukaar.hauntedharvest.blocks.ModCarvedPumpkinBlockTile;
import net.mehvahdjukaar.hauntedharvest.entity.ICustomPumpkinHolder;
import net.mehvahdjukaar.moonlight.api.util.Utils;
import net.minecraft.world.entity.animal.SnowGolem;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.ItemAbilities;

public class HHPlatformStuffImpl {
    public static void setItemLifespan(ItemEntity item, int lifespan) {
        item.lifespan = lifespan;
    }

    public static boolean isTopCarver(ItemStack stack) {
        return stack.canPerformAction(ItemAbilities.SHEARS_CARVE);
    }

    public static void addPumpkinData(ModCarvedPumpkinBlockTile tile, SnowGolem snowGolem) {
        if (snowGolem instanceof ICustomPumpkinHolder customPumpkinHolder) {
            ItemStack stack =  Utils.saveTileToItem(tile);
            customPumpkinHolder.hauntedharvest$setCustomPumpkin(stack);
        }
    }

}
