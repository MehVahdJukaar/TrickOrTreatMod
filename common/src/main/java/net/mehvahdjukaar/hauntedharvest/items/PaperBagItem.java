package net.mehvahdjukaar.hauntedharvest.items;

import dev.architectury.injectables.annotations.PlatformOnly;
import net.mehvahdjukaar.moonlight.api.misc.ForgeOverride;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.EnderMan;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.Nullable;

import static net.mehvahdjukaar.hauntedharvest.configs.CommonConfigs.PAPER_BAG_ENDERMAN;

public class PaperBagItem extends BlockItem {

    public PaperBagItem(Block block, Properties properties) {
        super(block, properties);
    }

    @ForgeOverride
    public @Nullable EquipmentSlot getEquipmentSlot(ItemStack stack) {
        return EquipmentSlot.HEAD;
    }

    @ForgeOverride
    public boolean isEnderMask(ItemStack stack, Player player, EnderMan enderMan) {
        return PAPER_BAG_ENDERMAN.get();
    }

    @ForgeOverride
    public boolean canEquip(ItemStack stack, EquipmentSlot armorType, LivingEntity entity) {
        return getEquipmentSlot(stack) == armorType;
    }
}
