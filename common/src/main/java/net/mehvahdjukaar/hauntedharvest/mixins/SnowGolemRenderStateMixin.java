package net.mehvahdjukaar.hauntedharvest.mixins;

import net.mehvahdjukaar.hauntedharvest.client.ICustomPumpkinRenderState;
import net.minecraft.client.renderer.entity.state.SnowGolemRenderState;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(SnowGolemRenderState.class)
public abstract class SnowGolemRenderStateMixin implements ICustomPumpkinRenderState {

    @Unique
    private final ItemStackRenderState hauntedharvest$customPumpkin = new ItemStackRenderState();

    @Override
    public ItemStackRenderState hauntedharvest$getCustomPumpkin() {
        return hauntedharvest$customPumpkin;
    }
}
