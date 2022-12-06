package net.mehvahdjukaar.hauntedharvest.mixins.forge;

import net.mehvahdjukaar.harvestseason.forge.PumpkinBlurGuiOverlay;
import net.mehvahdjukaar.harvestseason.items.ModCarvedPumpkinItem;
import net.mehvahdjukaar.moonlight.api.client.ICustomItemRendererProvider;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import net.minecraftforge.common.util.NonNullLazy;
import org.spongepowered.asm.mixin.Mixin;

import java.util.function.Consumer;

@Mixin(ModCarvedPumpkinItem.class)
public abstract class SelfCarvedPumpkinItemMixin extends Item {

    protected SelfCarvedPumpkinItemMixin(Properties properties) {
        super(properties);
    }

    @Override
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        if(this instanceof ICustomItemRendererProvider provider) {
            consumer.accept(new IClientItemExtensions() {

                @Override
                public void renderHelmetOverlay(ItemStack stack, Player player, int width, int height, float partialTick) {
                    PumpkinBlurGuiOverlay.INSTANCE.renderPumpkin(stack, width, height);
                }

                final NonNullLazy<BlockEntityWithoutLevelRenderer> renderer = NonNullLazy.of(provider.getRendererFactory()::get);

                @Override
                public BlockEntityWithoutLevelRenderer getCustomRenderer() {
                    return renderer.get();
                }
            });
        }
    }
}
