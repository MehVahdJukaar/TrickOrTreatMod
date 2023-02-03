package net.mehvahdjukaar.hauntedharvest.items;

import dev.architectury.injectables.annotations.PlatformOnly;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.Nullable;

public class PaperBagItem extends BlockItem {

    public PaperBagItem(Block block, Properties properties) {
        super(block, properties);
    }

    @PlatformOnly(PlatformOnly.FORGE)
    public @Nullable EquipmentSlot getEquipmentSlot(ItemStack stack) {
        return EquipmentSlot.HEAD;
    }
}
