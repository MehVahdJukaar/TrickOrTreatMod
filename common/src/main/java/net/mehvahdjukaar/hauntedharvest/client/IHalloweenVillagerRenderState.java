package net.mehvahdjukaar.hauntedharvest.client;

import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.Nullable;

public interface IHalloweenVillagerRenderState {

    @Nullable
    Identifier hauntedharvest$getMaskTexture();

    void hauntedharvest$setMaskTexture(@Nullable Identifier maskTexture);

    boolean hauntedharvest$isConverting();

    void hauntedharvest$setConverting(boolean converting);
}
