package net.mehvahdjukaar.hauntedharvest.mixins;

import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalBooleanRef;
import net.mehvahdjukaar.hauntedharvest.HauntedHarvest;
import net.mehvahdjukaar.hauntedharvest.ai.IHarmlessProjectile;
import net.mehvahdjukaar.hauntedharvest.configs.CommonConfigs;
import net.mehvahdjukaar.hauntedharvest.entity.SplatteredEggEntity;
import net.mehvahdjukaar.moonlight.api.util.Utils;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.entity.projectile.ThrownEgg;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ThrownEgg.class)
public abstract class ThrownEggEntityMixin extends ThrowableItemProjectile implements IHarmlessProjectile {

    @Unique
    private boolean hauntedharvest$shotFromVillager = false;

    protected ThrownEggEntityMixin(EntityType<? extends ThrowableItemProjectile> aSuper, Level level) {
        super(aSuper, level);
    }

    @Override
    public boolean hauntedharvest$isHarmless() {
        return hauntedharvest$shotFromVillager;
    }

    @Override
    public void hauntedharvest$setHarmless(boolean harmless) {
        this.hauntedharvest$shotFromVillager = harmless;
    }

    //for villagers
    @Inject(method = "onHit", at = @At("HEAD"), cancellable = true)
    protected void onHit(HitResult pResult, CallbackInfo ci, @Share("hasSpawnedChicken") LocalBooleanRef spawnedChicken) {
        spawnedChicken.set(false);
        if (this.hauntedharvest$isHarmless()) {
            if (pResult.getType() == HitResult.Type.BLOCK && CommonConfigs.SPLATTERED_EGG_ENABLED.get()) {
                SplatteredEggEntity.spawn(pResult, this);
                this.onHitBlock((BlockHitResult) pResult);
                this.discard();
            }
            ci.cancel();
        }
    }//TODO: check if iron golems can be angered

    //from player
    @Inject(method = "onHit", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/EntityType;create(Lnet/minecraft/world/level/Level;)Lnet/minecraft/world/entity/Entity;"))
    protected void onSpawnChicken(HitResult pResult, CallbackInfo ci,
                                  @Share("hasSpawnedChicken") LocalBooleanRef spawnedChicken) {
        spawnedChicken.set(true);
    }

    //from player2
    @Inject(method = "onHit", at = @At(value = "INVOKE", shift = At.Shift.BEFORE,
            target = "Lnet/minecraft/world/entity/projectile/ThrownEgg;discard()V"))
    protected void onHitFromPlayer(HitResult pResult, CallbackInfo ci,
                                   @Share("hasSpawnedChicken") LocalBooleanRef spawnedChicken) {
        if (!spawnedChicken.get() && CommonConfigs.SPLATTERED_EGG_ENABLED.get()) {
            if(this.getOwner() instanceof ServerPlayer serverPlayer) {
                Utils.awardAdvancement(serverPlayer, HauntedHarvest.res("nether/splatter_eggs_in_nether"));
            }
            SplatteredEggEntity.spawn(pResult, this);
        }
    }

}