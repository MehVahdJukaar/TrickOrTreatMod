package net.mehvahdjukaar.hauntedharvest.fabric;

import net.fabricmc.loader.api.FabricLoader;
import net.mehvahdjukaar.hauntedharvest.blocks.ModCarvedPumpkinBlockTile;
import net.mehvahdjukaar.hauntedharvest.reg.ModTags;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.world.entity.animal.SnowGolem;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ShearsItem;
import net.minecraft.world.item.SwordItem;

import java.util.List;

public class HHPlatformStuffImpl {
    public static void setItemLifespan(ItemEntity item, int lifespan) {
    }

    public static boolean isTopCarver(ItemStack stack) {
        return stack.getItem() instanceof ShearsItem || (!(stack.getItem() instanceof SwordItem) && stack.is(ModTags.CARVERS));
    }

    public static void addPumpkinData(ModCarvedPumpkinBlockTile tile, SnowGolem snowGolem) {
    }

    public static ShaderInstance getBlur() {
        return null;
    }

    @Deprecated(forRemoval = true)
    public static List<String> getMods() {
       return FabricLoader.getInstance().getAllMods().stream().map(m->m.getMetadata().getId()).toList();
    }
}
