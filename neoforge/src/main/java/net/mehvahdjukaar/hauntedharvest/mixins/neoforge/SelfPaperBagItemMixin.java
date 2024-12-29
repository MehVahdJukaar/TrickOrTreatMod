package net.mehvahdjukaar.hauntedharvest.mixins.neoforge;

import net.mehvahdjukaar.hauntedharvest.neoforge.BlurOverlay;
import net.mehvahdjukaar.hauntedharvest.items.PaperBagItem;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import org.spongepowered.asm.mixin.Mixin;

import java.util.function.Consumer;

@Mixin(PaperBagItem.class)
public abstract class SelfPaperBagItemMixin extends Item {

    protected SelfPaperBagItemMixin(Properties properties) {
        super(properties);
    }

    @Override
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(new IClientItemExtensions() {

            @Override
            public void renderHelmetOverlay(ItemStack stack, Player player, int width, int height, float partialTick) {
                BlurOverlay.renderPaperBag(stack, width, height);
            }
        });
    }
}
