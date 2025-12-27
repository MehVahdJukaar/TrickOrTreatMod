package net.mehvahdjukaar.hauntedharvest.items;

import dev.architectury.injectables.annotations.PlatformOnly;
import net.mehvahdjukaar.hauntedharvest.blocks.ModCarvedPumpkinBlock;
import net.mehvahdjukaar.hauntedharvest.blocks.PumpkinType;
import net.mehvahdjukaar.hauntedharvest.items.components.PumpkinCarvingData;
import net.mehvahdjukaar.hauntedharvest.reg.ModRegistry;
import net.mehvahdjukaar.moonlight.api.misc.ForgeOverride;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.EnderMan;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class ModCarvedPumpkinItem extends BlockItem {

    private final Holder<PumpkinType> type;

    public ModCarvedPumpkinItem(ModCarvedPumpkinBlock block, Properties properties) {
        super(block, properties);
        this.type = block.getType(block.defaultBlockState());
    }

    public Holder<PumpkinType> getType() {
        return type;
    }

    @Override
    public Optional<TooltipComponent> getTooltipImage(ItemStack pStack) {
        PumpkinCarvingData data = pStack.get(ModRegistry.PUMPKIN_CARVING.get());
        return Optional.ofNullable(data);
    }

    @ForgeOverride
    public @Nullable EquipmentSlot getEquipmentSlot(ItemStack stack) {
        return getType().value().isJackOLantern() ? null : EquipmentSlot.HEAD;
    }

    @ForgeOverride
    public boolean isEnderMask(ItemStack stack, Player player, EnderMan enderMan) {
        return true;
    }


    @ForgeOverride
    public boolean canEquip(ItemStack stack, EquipmentSlot armorType, LivingEntity entity) {
        return getEquipmentSlot(stack) == armorType;
    }

}
