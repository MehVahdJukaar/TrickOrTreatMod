package net.mehvahdjukaar.hauntedharvest.reg;

import net.mehvahdjukaar.hauntedharvest.HauntedHarvest;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

public class ModTags {

    public static final TagKey<Item> VILLAGER_SWEETS = itemTag("villager_sweets");
    public static final TagKey<Block> PUMPKIN_SUPPORT = blockTag("pumpkin_support");
    public static final TagKey<Block> PUMPKINS = blockTag("pumpkins");
    public static final TagKey<Block> CARVED_PUMPKINS = blockTag("carved_pumpkins");
    public static final TagKey<Block> JACK_O_LANTERNS = blockTag("jack_o_lanterns");

    public static final TagKey<Item> MODDED_CANDIES = itemTag("candy_bag_candies");
    public static final TagKey<Item> CARVERS = itemTag("pumpkin_carvers");
    public static final TagKey<Item> CARVABLE_PUMPKINS = itemTag("carvable_pumpkins");

    private static TagKey<Item> itemTag(String name) {
        return TagKey.create(Registries.ITEM, HauntedHarvest.res(name));
    }
    private static TagKey<Block> blockTag(String name) {
        return TagKey.create(Registries.BLOCK, HauntedHarvest.res(name));
    }
}
