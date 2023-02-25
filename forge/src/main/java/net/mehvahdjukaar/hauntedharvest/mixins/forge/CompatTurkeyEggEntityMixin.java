package net.mehvahdjukaar.hauntedharvest.mixins.forge;

import net.mehvahdjukaar.hauntedharvest.entity.SplatteredEggEntity;
import net.minecraft.client.KeyMapping;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.client.settings.KeyMappingLookup;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xfacthd.framedblocks.client.util.KeyMappings;

@Mixin(targets = "com.teamabnormals.autumnity.common.entity.projectile.ThrownTurkeyEgg")
public abstract class CompatTurkeyEggEntityMixin extends ThrowableItemProjectile {

    @Unique
    private boolean hasSpawnedChicken = false;

    protected CompatTurkeyEggEntityMixin(EntityType<? extends ThrowableItemProjectile> arg, Level arg2) {
        super(arg, arg2);
    }

    //from player
    @Inject(method = "onHit", require = 0, remap = true,
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/EntityType;create(Lnet/minecraft/world/level/Level;)Lnet/minecraft/world/entity/Entity;"))
    protected void onSpawnChicken(HitResult pResult, CallbackInfo ci) {
        this.hasSpawnedChicken = true;
    }

    //from player2
    @Inject(method = "onHit", require = 0, remap = true,
            at = @At(value = "INVOKE", shift = At.Shift.AFTER,
                    target = "Lnet/minecraft/world/level/Level;broadcastEntityEvent(Lnet/minecraft/world/entity/Entity;B)V"))
    protected void onHitFromPlayer(HitResult pResult, CallbackInfo ci) {
        if (!this.hasSpawnedChicken) SplatteredEggEntity.spawn(pResult, this);
    }

}