package net.mehvahdjukaar.hauntedharvest.mixins;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import net.mehvahdjukaar.hauntedharvest.entity.ICustomPumpkinHolder;
import net.mehvahdjukaar.hauntedharvest.network.SyncSnowGolemPumpkinPacket;
import net.mehvahdjukaar.moonlight.api.platform.network.NetworkHelper;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.golem.SnowGolem;
import net.minecraft.world.item.ItemInstance;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.level.storage.loot.LootTable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.BiConsumer;

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

    @Inject(method = "shear", at = @At("HEAD"))
    protected void hauntedharvest$rememberShearedPumpkin(ServerLevel level, SoundSource soundSource, ItemStack tool, CallbackInfo ci,
                                                         @Share("shearedPumpkin") LocalRef<ItemStack> shearedPumpkin) {
        shearedPumpkin.set(this.hauntedharvest$getCustomPumpkin());
    }

    @WrapOperation(method = "shear", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/entity/animal/golem/SnowGolem;dropFromShearingLootTable(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/resources/ResourceKey;Lnet/minecraft/world/item/ItemInstance;Ljava/util/function/BiConsumer;)V"))
    protected void hauntedharvest$shearCustomPumpkin(SnowGolem golem, ServerLevel level, ResourceKey<LootTable> lootTable,
                                                     ItemInstance tool, BiConsumer<ServerLevel, ItemStack> dropper,
                                                     Operation<Void> original,
                                                     @Share("shearedPumpkin") LocalRef<ItemStack> shearedPumpkin) {
        ItemStack customPumpkin = shearedPumpkin.get();
        if (customPumpkin.isEmpty()) original.call(golem, level, lootTable, tool, dropper);
        else dropper.accept(level, customPumpkin);
    }


}
