package net.mehvahdjukaar.hauntedharvest.mixins;

import net.mehvahdjukaar.hauntedharvest.entity.ICustomPumpkinHolder;
import net.mehvahdjukaar.hauntedharvest.network.SyncSnowGolemPumpkinPacket;
import net.mehvahdjukaar.moonlight.api.platform.network.NetworkHelper;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.golem.SnowGolem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SnowGolem.class)
public abstract class SnowGolemMixin extends Entity implements ICustomPumpkinHolder {

    @Shadow
    public abstract void setPumpkin(boolean pumpkinEquipped);

    @Unique
    private ItemStack hauntedHarvest$customPumpkin = ItemStack.EMPTY;

    protected SnowGolemMixin(EntityType<?> arg, Level arg2) {
        super(arg, arg2);
    }

    @Inject(method = "addAdditionalSaveData", at = @At("TAIL"))
    protected void hauntedharvest$saveCustomPumpkinData(ValueOutput output, CallbackInfo ci) {
        ItemStack itemStack = this.hauntedharvest$getCustomPumpkin();
        if (!itemStack.isEmpty()) output.store("CustomPumpkin", ItemStack.CODEC, itemStack);
    }

    @Inject(method = "readAdditionalSaveData", at = @At("TAIL"))
    protected void hauntedharvest$loadCustomPumpkinData(ValueInput input, CallbackInfo ci) {
        input.read("CustomPumpkin", ItemStack.CODEC).ifPresent(this::hauntedharvest$setCustomPumpkin);
    }

    @Override
    public ItemStack hauntedharvest$getCustomPumpkin() {
        return hauntedHarvest$customPumpkin;
    }

    @Override
    public void hauntedharvest$setCustomPumpkin(ItemStack stack) {
        hauntedHarvest$customPumpkin = stack;
        if (!level().isClientSide()) {
            //only needed when entity is already spawned
            NetworkHelper.sendToAllClientPlayersTrackingEntity(this,
                    new SyncSnowGolemPumpkinPacket(this));
        }
    }

    @Inject(method = "setPumpkin", at = @At("TAIL"))
    protected void hauntedharvest$setPumpkin(boolean pumpkinEquipped, CallbackInfo ci) {
        if (!pumpkinEquipped) this.hauntedharvest$setCustomPumpkin(ItemStack.EMPTY);
    }

    @ModifyArg(method = "shear", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/entity/animal/SnowGolem;spawnAtLocation(Lnet/minecraft/world/item/ItemStack;F)Lnet/minecraft/world/entity/item/ItemEntity;"))
    protected ItemStack hauntedharvest$shearCustomPumpkin(ItemStack original) {
        ItemStack s = this.hauntedharvest$getCustomPumpkin();
        if (!s.isEmpty()) {
            return s;
        }
        return original;
    }


}
