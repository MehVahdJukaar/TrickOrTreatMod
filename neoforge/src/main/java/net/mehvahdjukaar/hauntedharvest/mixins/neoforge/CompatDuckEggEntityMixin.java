package net.mehvahdjukaar.hauntedharvest.mixins.neoforge;

import net.mehvahdjukaar.hauntedharvest.configs.CommonConfigs;
import net.mehvahdjukaar.hauntedharvest.entity.SplatteredEggEntity;
import net.mehvahdjukaar.moonlight.api.misc.OptionalMixin;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@OptionalMixin("com.teamabnormals.environmental.common.entity.projectile.ThrownDuckEgg")
@Mixin(targets = "com.teamabnormals.environmental.common.entity.projectile.ThrownDuckEgg")
public abstract class CompatDuckEggEntityMixin extends ThrowableItemProjectile {

    @Unique
    private boolean hauntedharvest$hasSpawnedDuck = false;

    protected CompatDuckEggEntityMixin(EntityType<? extends ThrowableItemProjectile> arg, Level arg2) {
        super(arg, arg2);
    }

    //from player
    @Inject(method = "onHit", require = 0,
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/EntityType;create(Lnet/minecraft/world/level/Level;)Lnet/minecraft/world/entity/Entity;"))
    protected void onSpawnDuck(HitResult pResult, CallbackInfo ci) {
        this.hauntedharvest$hasSpawnedDuck = true;
    }

    //from player2
    @Inject(method = "onHit", require = 0,
            at = @At(value = "INVOKE", shift = At.Shift.AFTER,
                    target = "Lnet/minecraft/world/level/Level;broadcastEntityEvent(Lnet/minecraft/world/entity/Entity;B)V"))
    protected void onHitFromPlayer(HitResult pResult, CallbackInfo ci) {
        if (!this.hauntedharvest$hasSpawnedDuck && CommonConfigs.SPLATTERED_EGG_ENABLED.get()) {
            SplatteredEggEntity.spawn(pResult, this);
        }
    }

}
