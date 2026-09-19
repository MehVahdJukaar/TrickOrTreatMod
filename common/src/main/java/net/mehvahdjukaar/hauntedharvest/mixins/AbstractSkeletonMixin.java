package net.mehvahdjukaar.hauntedharvest.mixins;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.mehvahdjukaar.hauntedharvest.CustomCarvingsManager;
import net.mehvahdjukaar.hauntedharvest.HauntedHarvest;
import net.mehvahdjukaar.hauntedharvest.configs.CommonConfigs;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.monster.skeleton.AbstractSkeleton;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(AbstractSkeleton.class)
public abstract class AbstractSkeletonMixin extends Mob {

    protected AbstractSkeletonMixin(EntityType<? extends Mob> entityType, Level level) {
        super(entityType, level);
    }

    @ModifyExpressionValue(method = "finalizeSpawn", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/SpecialDates;isHalloween()Z"))
    public boolean addCustomPumpkins(boolean isHalloween, @Local(argsOnly = true) ServerLevelAccessor level) {
        if (level instanceof Level l && HauntedHarvest.getSeasonManager().shouldWearCustomPumpkin(l)) {
            if (level.getRandom().nextFloat() < CommonConfigs.WEAR_CHANCE.get()) {
                ItemStack stack = CustomCarvingsManager.getRandomPumpkinItem(level, true,
                        0.5f, 0.1f);

                this.setItemSlot(EquipmentSlot.HEAD, stack);
                this.setDropChance(EquipmentSlot.HEAD, 0.0F);
                return false;
            }
        }
        return isHalloween;
    }
}