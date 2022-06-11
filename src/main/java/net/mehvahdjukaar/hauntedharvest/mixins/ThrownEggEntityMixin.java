package net.mehvahdjukaar.hauntedharvest.mixins;

import net.mehvahdjukaar.hauntedharvest.ai.IHarmlessProjectile;
import net.mehvahdjukaar.hauntedharvest.entity.SplatteredEggEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.decoration.HangingEntity;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.entity.projectile.ThrownEgg;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ThrownEgg.class)
public abstract class ThrownEggEntityMixin extends ThrowableItemProjectile implements IHarmlessProjectile {

    private boolean hasSpawnedChicken = false;
    private boolean shotFromVillager = false;

    public ThrownEggEntityMixin(EntityType<? extends ThrowableItemProjectile> aSuper, Level level) {
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
            if (pResult.getType() == HitResult.Type.BLOCK) {
                this.spawnSplatteredEgg(pResult);
                this.onHitBlock((BlockHitResult) pResult);
                this.discard();
            }
            ci.cancel();
        }
    }

    //from player
    @Inject(method = "onHit", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/EntityType;create(Lnet/minecraft/world/level/Level;)Lnet/minecraft/world/entity/Entity;"))
    protected void onSpawnChicken(HitResult pResult, CallbackInfo ci) {
        this.hasSpawnedChicken = true;
    }

    //from player2
    @Inject(method = "onHit", at = @At(value = "INVOKE", shift = At.Shift.BEFORE,
            target = "Lnet/minecraft/world/entity/projectile/ThrownEgg;discard()V"))
    protected void onHitFromPlayer(HitResult pResult, CallbackInfo ci) {
        if (!this.hasSpawnedChicken) this.spawnSplatteredEgg(pResult);
    }


    protected void spawnSplatteredEgg(HitResult pResult) {
        var type = pResult.getType();
        if (type == HitResult.Type.BLOCK) {
            BlockHitResult hit = (BlockHitResult) pResult;

            BlockPos blockpos = hit.getBlockPos();
            Direction direction = hit.getDirection();
            BlockPos relative = blockpos.relative(direction);

            HangingEntity hangingentity = new SplatteredEggEntity(level, relative, direction);

            if (hangingentity.survives()) {
                if (!level.isClientSide) {
                    hangingentity.playPlacementSound();
                    level.gameEvent(this.getOwner(), GameEvent.ENTITY_PLACE, blockpos);
                    level.addFreshEntity(hangingentity);
                }
            }
        }
    }


}