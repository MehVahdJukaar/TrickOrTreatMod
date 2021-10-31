package net.mehvahdjukaar.hauntedharvest.mixins;

import net.mehvahdjukaar.hauntedharvest.ai.IHarmlessProjectile;
import net.mehvahdjukaar.hauntedharvest.entity.SplatteredEggEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.Chicken;
import net.minecraft.world.entity.decoration.HangingEntity;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.entity.projectile.ThrownEgg;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(ThrownEgg.class)
public abstract class ThrownEggMixin extends ThrowableItemProjectile implements IHarmlessProjectile {

    private boolean shotFromVillager = false;

    public ThrownEggMixin(EntityType<? extends ThrowableItemProjectile> p_37442_, Level p_37443_) {
        super(p_37442_, p_37443_);
    }

    //@Inject(method = "<init>(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/entity/LivingEntity;)V", at = @At("RETURN"))
    //public void init(CallbackInfo ci){
    //    if(this.getOwner() instanceof Villager villager && villager.isBaby()) this.shotFromVillager = true;
   // }

    @Override
    public boolean isHarmless() {
        return shotFromVillager;
    }

    @Override
    public void setHarmless(boolean harmless) {
        this.shotFromVillager = harmless;
    }

    /**
     * @author MehVahdJukaar
     * adding new splattered egg behavior. Don't have time to figure out the non overwriting way
     */
    @Overwrite()
    protected void onHit(HitResult pResult) {
        if(this.isHarmless()){
            if (pResult.getType() == HitResult.Type.BLOCK) {
                this.onHitBlock((BlockHitResult)pResult);
                this.spawnSplatteredEgg(pResult);
            }
            return;
        }
        super.onHit(pResult);
        if (!this.level.isClientSide) {
            if (this.random.nextInt(8) == 0) {
                int i = 1;
                //bruh why
                if (this.random.nextInt(32) == 0) {
                    i = 4;
                }

                for(int j = 0; j < i; ++j) {
                    Chicken chicken = EntityType.CHICKEN.create(this.level);
                    chicken.setAge(-24000);
                    chicken.moveTo(this.getX(), this.getY(), this.getZ(), this.getYRot(), 0.0F);
                    this.level.addFreshEntity(chicken);
                }
            }
            else{
                this.spawnSplatteredEgg(pResult);
            }

            this.level.broadcastEntityEvent(this, (byte)3);
            this.discard();
        }
    }

    protected void spawnSplatteredEgg(HitResult pResult){
        var type = pResult.getType();
        if(type == HitResult.Type.BLOCK){
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