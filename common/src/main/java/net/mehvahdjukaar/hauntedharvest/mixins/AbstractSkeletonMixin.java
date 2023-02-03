package net.mehvahdjukaar.hauntedharvest.mixins;

import net.mehvahdjukaar.hauntedharvest.CustomCarvingsManager;
import net.mehvahdjukaar.hauntedharvest.HauntedHarvest;
import net.mehvahdjukaar.hauntedharvest.configs.CommonConfigs;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.monster.AbstractSkeleton;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractSkeleton.class)
public abstract class AbstractSkeletonMixin extends Mob {

    protected AbstractSkeletonMixin(EntityType<? extends Mob> entityType, Level level) {
        super(entityType, level);
    }

    @Inject(method = "finalizeSpawn", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/EquipmentSlot;getIndex()I"))
    public void addCustomPumpkins(ServerLevelAccessor level, DifficultyInstance difficulty, MobSpawnType reason, SpawnGroupData spawnData, CompoundTag dataTag, CallbackInfoReturnable<SpawnGroupData> cir) {
        if (getItemBySlot(EquipmentSlot.HEAD).isEmpty() && level instanceof Level l &&
                HauntedHarvest.getSeasonManager().shouldWearCustomPumpkin(l)) {
            if (level.getRandom().nextFloat() < CommonConfigs.WEAR_CHANCE.get()) {
                ItemStack stack = CustomCarvingsManager.getRandomPumpkinItem(level, true,
                        0.5f, 0.1f);

                this.setItemSlot(EquipmentSlot.HEAD, stack);
                this.armorDropChances[EquipmentSlot.HEAD.getIndex()] = 0.0F;
            }
        }
    }
}