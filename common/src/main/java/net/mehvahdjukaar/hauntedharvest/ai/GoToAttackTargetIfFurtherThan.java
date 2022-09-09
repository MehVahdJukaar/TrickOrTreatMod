package net.mehvahdjukaar.hauntedharvest.ai;

import com.google.common.collect.ImmutableMap;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.behavior.BehaviorUtils;
import net.minecraft.world.entity.ai.behavior.EntityTracker;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.memory.WalkTarget;
import net.minecraft.world.entity.npc.Villager;

public class GoToAttackTargetIfFurtherThan extends Behavior<Villager> {
    private final float speedModifier;
    private final float range;

    public GoToAttackTargetIfFurtherThan(float speedModifier, float range) {
        super(ImmutableMap.of(
                MemoryModuleType.WALK_TARGET, MemoryStatus.REGISTERED,
                MemoryModuleType.LOOK_TARGET, MemoryStatus.REGISTERED,
                MemoryModuleType.ATTACK_TARGET, MemoryStatus.VALUE_PRESENT,
                MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES, MemoryStatus.REGISTERED));
        this.speedModifier = speedModifier;
        this.range  =range;
    }

    @Override
    protected boolean checkExtraStartConditions(ServerLevel pLevel, Villager pOwner) {
        LivingEntity livingentity = pOwner.getBrain().getMemory(MemoryModuleType.ATTACK_TARGET).orElse(null);
        if (livingentity == null || !livingentity.isAlive()) {
            ThrowEggs.clearAnger(pOwner);
            return false;
        }
        return true;
    }

    /*
    @Override
    protected boolean checkExtraStartConditions(ServerLevel pLevel, Mob pOwner) {
        LivingEntity livingentity = pOwner.getBrain().getMemory(MemoryModuleType.ATTACK_TARGET).get();
        return isOutOfRange(pOwner, livingentity, range);
    }*/

    @Override
    protected void start(ServerLevel pLevel, Villager pEntity, long pGameTime) {
        LivingEntity livingentity = pEntity.getBrain().getMemory(MemoryModuleType.ATTACK_TARGET).get();
        if (BehaviorUtils.canSee(pEntity, livingentity) && !isOutOfRange(pEntity, livingentity, range -2)) {
            this.clearWalkTarget(pEntity);
        } else {
            this.setWalkAndLookTarget(pEntity, livingentity);
        }
    }

    private boolean isOutOfRange(LivingEntity entity, LivingEntity other, float range){
        return entity.distanceToSqr(other.getX(), other.getY(), other.getZ()) > range*range;
    }

    private void setWalkAndLookTarget(LivingEntity p_24038_, LivingEntity p_24039_) {
        Brain<?> brain = p_24038_.getBrain();
        brain.setMemory(MemoryModuleType.LOOK_TARGET, new EntityTracker(p_24039_, true));
        WalkTarget walktarget = new WalkTarget(new EntityTracker(p_24039_, false), this.speedModifier, 5);
        brain.setMemory(MemoryModuleType.WALK_TARGET, walktarget);
    }

    private void clearWalkTarget(LivingEntity p_24036_) {
        p_24036_.getBrain().eraseMemory(MemoryModuleType.WALK_TARGET);
    }
}