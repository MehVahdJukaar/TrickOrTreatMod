package net.mehvahdjukaar.hauntedharvest.mixins;

import com.llamalad7.mixinextras.sugar.Local;
import net.mehvahdjukaar.hauntedharvest.client.CarvingManager;
import net.mehvahdjukaar.hauntedharvest.items.components.PumpkinCarvingData;
import net.mehvahdjukaar.hauntedharvest.reg.ModRegistry;
import net.minecraft.client.gui.Gui;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(Gui.class)
public abstract class GuiMixin {

    @ModifyArg(method = "extractCameraOverlays",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/Gui;extractTextureOverlay(Lnet/minecraft/client/gui/GuiGraphicsExtractor;Lnet/minecraft/resources/Identifier;F)V",
                    ordinal = 0),
            index = 1)
    private Identifier hauntedharvest$carvedPumpkinBlur(Identifier texture, @Local ItemStack stack) {
        PumpkinCarvingData carving = stack.get(ModRegistry.PUMPKIN_CARVING.get());
        if (carving == null || carving.isEmpty()) return texture;
        Identifier blur = CarvingManager.getInstance(carving).getPumpkinBlur();
        return (blur != null) ? blur : texture;
    }

}
