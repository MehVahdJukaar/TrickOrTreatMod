package net.mehvahdjukaar.hauntedharvest.items;

import net.mehvahdjukaar.hauntedharvest.blocks.ModCarvedPumpkinBlock;
import net.mehvahdjukaar.hauntedharvest.blocks.PumpkinType;
import net.mehvahdjukaar.hauntedharvest.items.components.PumpkinCarvingData;
import net.mehvahdjukaar.hauntedharvest.reg.ModRegistry;
import net.minecraft.core.Holder;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;

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

}
