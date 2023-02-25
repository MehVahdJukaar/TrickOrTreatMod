package net.mehvahdjukaar.hauntedharvest.mixins;

import net.mehvahdjukaar.hauntedharvest.ai.IHarmlessProjectile;
import net.mehvahdjukaar.hauntedharvest.configs.RegistryConfigs;
import net.mehvahdjukaar.hauntedharvest.entity.SplatteredEggEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.decoration.HangingEntity;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.entity.projectile.ThrownEgg;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ThrownEgg.class)
public abstract class ThrownEggEntityMixin extends ThrowableItemProjectile implements IHarmlessProjectile {

    @Unique
    private boolean hasSpawnedChicken = false;
    @Unique
    private boolean shotFromVillager = false;

    protected ThrownEggEntityMixin(EntityType<? extends ThrowableItemProjectile> aSuper, Level level) {
        super(aSuper, level);
    }

    @Override
    public boolean isHarmless() {
        return shotFromVillager;
    }

    @Override
    public void setHarmless(boolean harmless) {
        this.shotFromVillager = harmless;
    }

    //for villagers
    @Inject(method = "onHit", at = @At("HEAD"), cancellable = true)
    protected void onHit(HitResult pResult, CallbackInfo ci) {
        this.hasSpawnedChicken = false;
        if (this.isHarmless()) {
            if (pResult.getType() == HitResult.Type.BLOCK && RegistryConfigs.SPLATTERED_EGG_ENABLED.get()) {
                SplatteredEggEntity.spawn(pResult,this);
                this.onHitBlock((BlockHitResult) pResult);
                this.discard();
            }
            ci.cancel();
        }
    }//TODO: check if iron golems can be angered

    //from player
    @Inject(method = "onHit", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/EntityType;create(Lnet/minecraft/world/level/Level;)Lnet/minecraft/world/entity/Entity;"))
    protected void onSpawnChicken(HitResult pResult, CallbackInfo ci) {
        this.hasSpawnedChicken = true;
    }

    //from player2
    @Inject(method = "onHit", at = @At(value = "INVOKE", shift = At.Shift.BEFORE,
            target = "Lnet/minecraft/world/entity/projectile/ThrownEgg;discard()V"))
    protected void onHitFromPlayer(HitResult pResult, CallbackInfo ci) {
        if (!this.hasSpawnedChicken) SplatteredEggEntity.spawn(pResult,this);
    }

}