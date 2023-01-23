package net.mehvahdjukaar.hauntedharvest.mixins.forge;

import net.mehvahdjukaar.hauntedharvest.forge.ICustomPumpkinHolder;
import net.minecraft.core.BlockPos;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.SnowGolem;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Collection;
import java.util.List;

@Mixin(SnowGolem.class)
public abstract class SnowGolemMixin extends Entity implements ICustomPumpkinHolder {
    @Shadow
    public abstract void setPumpkin(boolean pumpkinEquipped);

    @Unique
    private static final EntityDataAccessor<ItemStack> CUSTOM_PUMPKIN =
            SynchedEntityData.defineId(SnowGolem.class, EntityDataSerializers.ITEM_STACK);

    protected SnowGolemMixin(EntityType<?> arg, Level arg2) {
        super(arg, arg2);
    }

    @Inject(method = "defineSynchedData", at = @At("TAIL"))
    protected void defineSynchedData(CallbackInfo ci) {
        this.entityData.define(CUSTOM_PUMPKIN, ItemStack.EMPTY);
    }

    @Override
    public ItemStack getCustomPumpkin() {
        return this.entityData.get(CUSTOM_PUMPKIN);
    }

    @Override
    public void setCustomPumpkin(ItemStack stack) {
        this.entityData.set(CUSTOM_PUMPKIN, stack);
    }

    @Inject(method = "setPumpkin", at = @At("TAIL"))
    protected void setPumpkin(boolean pumpkinEquipped, CallbackInfo ci) {
        if (!pumpkinEquipped) this.setCustomPumpkin(ItemStack.EMPTY);
    }

    @Inject(method = "onSheared", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/entity/animal/SnowGolem;setPumpkin(Z)V",
            shift = At.Shift.BEFORE), cancellable = true)
    protected void shear(@Nullable Player player, @NotNull ItemStack item, Level level, BlockPos pos, int fortune,
                         CallbackInfoReturnable<Collection<ItemStack>> cir) {
        var s = getCustomPumpkin();
        if (!s.isEmpty()) {
            cir.setReturnValue(List.of(s.copy()));
            this.setPumpkin(false);
        }
    }


}
