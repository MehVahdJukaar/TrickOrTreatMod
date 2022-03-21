package net.mehvahdjukaar.hauntedharvest.ai;

import com.google.common.collect.ImmutableMap;
import net.mehvahdjukaar.hauntedharvest.Halloween;
import net.mehvahdjukaar.hauntedharvest.init.ModRegistry;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.behavior.EntityTracker;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.schedule.Activity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.List;

public class GiveCandyToPlayers extends Behavior<Villager> {

    private boolean hasGivenCandy = false;
    private int tickSinceStarted = 0;
    private int timeToGiveCandy = 0;

    public GiveCandyToPlayers() {
        super(ImmutableMap.of(
                MemoryModuleType.NEAREST_VISIBLE_PLAYER, MemoryStatus.VALUE_PRESENT,
                MemoryModuleType.WALK_TARGET, MemoryStatus.REGISTERED,
                MemoryModuleType.LOOK_TARGET, MemoryStatus.REGISTERED,
                MemoryModuleType.INTERACTION_TARGET, MemoryStatus.VALUE_ABSENT));
    }

    @Override
    protected boolean checkExtraStartConditions(ServerLevel pLevel, Villager pOwner) {
        if (!Halloween.isHalloweenSeason(pLevel)) return false;
        return getValidTarget(pOwner) != null;
    }

    public static boolean isValidTrickOrTreater(LivingEntity self, LivingEntity villagerTarget) {
        if (self.distanceToSqr(villagerTarget) <= 4 * 3 && !(Halloween.isPlayerOnCooldown(self))) {
            Entity lookTarget = villagerTarget.getBrain().getMemory(MemoryModuleType.INTERACTION_TARGET).orElse(null);
            return lookTarget == self;
        }
        return false;
    }

    @Nullable
    private LivingEntity getValidTarget(LivingEntity self) {
        List<LivingEntity> list = self.getBrain().getMemory(MemoryModuleType.VISIBLE_VILLAGER_BABIES).orElse(List.of());
        return list.stream().filter(t -> isValidTrickOrTreater(self, t)).findFirst().orElse(null);
    }

    @Override
    protected boolean canStillUse(ServerLevel pLevel, Villager pEntity, long pGameTime) {
        if (tickSinceStarted < 300 && !hasGivenCandy) {
            var p = pEntity.getBrain().getMemory(MemoryModuleType.INTERACTION_TARGET);
            return p.isPresent() && isValidTrickOrTreater(pEntity, p.get());
        }
        return false;
    }

    @Override
    protected void tick(ServerLevel pLevel, Villager pOwner, long pGameTime) {
        LivingEntity target = this.lookAtTarget(pOwner);
        if (target != null && this.tickSinceStarted > this.timeToGiveCandy) {

            int r = pLevel.getRandom().nextInt(12);

            //give coal
            if (r == 0) {
                ItemStack stack = new ItemStack(Items.CHARCOAL);
                throwCandy(pOwner, target, stack);

                pLevel.broadcastEntityEvent(pOwner, (byte) 13);
            }
            //get scared
            else if (r < 3) {
                spookVillager(pOwner, target);
            } else {
                //TODO: finish
                //ItemStack stack = Halloween.SWEETS.getRandomElement(pLevel.random).getDefaultInstance();
               // throwCandy(pOwner, target, stack);

                pLevel.broadcastEntityEvent(pOwner, (byte) 14);
            }

            this.hasGivenCandy = true;
            if (target instanceof IHalloweenVillager e) {
                e.setEntityOnCooldown(pOwner);
            }
        }
        this.tickSinceStarted++;
    }

    public static void spookVillager(Villager target, LivingEntity cause) {
        ((ServerLevel) target.level).sendParticles(ModRegistry.SPOOKED_PARTICLE.get(), target.getX(), target.getY() + 1.25, target.getZ(), 5,
                target.getBbWidth() / 2f, target.getBbHeight() / 3f, target.getBbWidth() / 2f, 0.02);


        //hax
        target.setLastHurtByMob(cause);
        target.hurt(DamageSource.GENERIC, 0.1f);
        target.heal(0.1f);
        target.getBrain().setActiveActivityIfPossible(Activity.PANIC);
        target.getBrain().setMemory(MemoryModuleType.HURT_BY, DamageSource.GENERIC);
        target.getBrain().setMemory(MemoryModuleType.HURT_BY_ENTITY, cause);
    }

    public static void throwCandy(LivingEntity self, LivingEntity pTarget, ItemStack stack) {


        Vec3 vec3 = pTarget.getDeltaMovement();
        double pX = pTarget.getX() + vec3.x - self.getX();
        double d1 = pTarget.getEyeY() - (double) 1.1F - self.getY();
        double pZ = pTarget.getZ() + vec3.z - self.getZ();
        double d3 = Math.sqrt(pX * pX + pZ * pZ);

        double d0 = self.getEyeY() - (double) 0.3F;
        ItemEntity itementity = new ItemEntity(self.level, self.getX(), d0, self.getZ(), stack);

        float pVelocity = 0.2F;
        double pY = d1 + d3 * 0.7D;

        itementity.setDeltaMovement((new Vec3(pX, pY, pZ)).normalize().scale(pVelocity));

        //this.level.playSound((Player)null, this.getX(), this.getY(), this.getZ(), SoundEvents.WITCH_THROW, this.getSoundSource(), 1.0F, 0.8F + this.random.nextFloat() * 0.4F);

        self.level.addFreshEntity(itementity);

    }

    @Override
    protected void start(ServerLevel pLevel, Villager pEntity, long pGameTime) {
        super.start(pLevel, pEntity, pGameTime);
        pEntity.stopSleeping();
        LivingEntity target = this.lookAtTarget(pEntity);

        this.timeToGiveCandy = 20 + pLevel.random.nextInt(30);
        this.tickSinceStarted = 0;
        this.hasGivenCandy = false;
        //     BehaviorUtils.lockGazeAndWalkToEachOther(pOwner, villager, 0.5F);
    }

    @Nullable
    private LivingEntity lookAtTarget(Villager self) {
        LivingEntity entity = this.getValidTarget(self);
        if (entity != null) {
            Brain<?> brain = self.getBrain();
            brain.setMemory(MemoryModuleType.INTERACTION_TARGET, entity);
            brain.setMemory(MemoryModuleType.LOOK_TARGET, new EntityTracker(entity, true));
            brain.eraseMemory(MemoryModuleType.WALK_TARGET);
        }
        return entity;
    }

    @Override
    protected void stop(ServerLevel pLevel, Villager pEntity, long pGameTime) {
        Brain<?> brain = pEntity.getBrain();
        brain.eraseMemory(MemoryModuleType.LOOK_TARGET);
        brain.eraseMemory(MemoryModuleType.INTERACTION_TARGET);
        this.hasGivenCandy = false;
        this.tickSinceStarted = 0;
        super.stop(pLevel, pEntity, pGameTime);
    }
}
