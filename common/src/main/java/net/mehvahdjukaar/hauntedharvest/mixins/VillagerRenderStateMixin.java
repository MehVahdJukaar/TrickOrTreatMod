package net.mehvahdjukaar.hauntedharvest.mixins;

import net.mehvahdjukaar.hauntedharvest.client.IHalloweenVillagerRenderState;
import net.minecraft.client.renderer.entity.state.VillagerRenderState;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(VillagerRenderState.class)
public abstract class VillagerRenderStateMixin implements IHalloweenVillagerRenderState {

    @Unique
    @Nullable
    private Identifier hauntedharvest$maskTexture;
    @Unique
    private boolean hauntedharvest$converting;

    @Override
    public @Nullable Identifier hauntedharvest$getMaskTexture() {
        return hauntedharvest$maskTexture;
    }

    @Override
    public void hauntedharvest$setMaskTexture(@Nullable Identifier maskTexture) {
        this.hauntedharvest$maskTexture = maskTexture;
    }

    @Override
    public boolean hauntedharvest$isConverting() {
        return hauntedharvest$converting;
    }

    @Override
    public void hauntedharvest$setConverting(boolean converting) {
        this.hauntedharvest$converting = converting;
    }
}
