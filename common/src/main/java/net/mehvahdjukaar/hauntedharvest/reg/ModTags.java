package net.mehvahdjukaar.hauntedharvest.reg;

import net.mehvahdjukaar.hauntedharvest.HauntedHarvest;
import net.minecraft.core.Registry;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

public class ModTags {

    public static final TagKey<Item> SWEETS = itemTag("sweets");
    public static final TagKey<Block> PUMPKIN_SUPPORT = blockTag("pumpkin_support");

    public static final TagKey<Item> MODDED_CANDIES = itemTag("candy");
    public static final TagKey<Item> CARVERS = itemTag("pumpkin_carvers");
    public static final TagKey<Item> CARVABLE_PUMPKINS = itemTag("carvable_pumpkins");

    private static TagKey<Item> itemTag(String name) {
        return TagKey.create(Registry.ITEM_REGISTRY, HauntedHarvest.res(name));
    }
    private static TagKey<Block> blockTag(String name) {
        return TagKey.create(Registry.BLOCK_REGISTRY, HauntedHarvest.res(name));
    }
}
