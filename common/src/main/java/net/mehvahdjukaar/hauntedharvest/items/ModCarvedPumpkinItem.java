package net.mehvahdjukaar.hauntedharvest.items;

import dev.architectury.injectables.annotations.PlatformOnly;
import net.mehvahdjukaar.hauntedharvest.blocks.ModCarvedPumpkinBlock;
import net.mehvahdjukaar.hauntedharvest.blocks.PumpkinType;
import net.mehvahdjukaar.hauntedharvest.client.CarvedPumpkinItemRenderer;
import net.mehvahdjukaar.hauntedharvest.items.components.PumpkinCarvingData;
import net.mehvahdjukaar.hauntedharvest.reg.ModRegistry;
import net.mehvahdjukaar.moonlight.api.client.ICustomItemRendererProvider;
import net.mehvahdjukaar.moonlight.api.client.ItemStackRenderer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.monster.EnderMan;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.function.Supplier;

public class ModCarvedPumpkinItem extends BlockItem implements ICustomItemRendererProvider {

    private final PumpkinType type;

    public ModCarvedPumpkinItem(ModCarvedPumpkinBlock block, Properties properties) {
        super(block, properties);
        this.type = block.getType(block.defaultBlockState());
    }

    public PumpkinType getType() {
        return type;
    }

    @Override
    public Optional<TooltipComponent> getTooltipImage(ItemStack pStack) {
        PumpkinCarvingData data = pStack.get(ModRegistry.PUMPKIN_CARVING.get());
        return Optional.ofNullable(data);
    }

    @Override
    public Supplier<ItemStackRenderer> getRendererFactory() {
        return CarvedPumpkinItemRenderer::new;
    }

    @PlatformOnly(PlatformOnly.FORGE)
    public @Nullable EquipmentSlot getEquipmentSlot(ItemStack stack) {
        return type.isJackOLantern() ? null : EquipmentSlot.HEAD;
    }

    @PlatformOnly(PlatformOnly.FORGE)
    public boolean isEnderMask(ItemStack stack, Player player, EnderMan enderMan) {
        return true;
    }
}
