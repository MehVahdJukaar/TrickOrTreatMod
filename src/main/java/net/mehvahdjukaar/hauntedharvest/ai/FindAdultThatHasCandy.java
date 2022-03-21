package net.mehvahdjukaar.hauntedharvest.ai;

import com.google.common.collect.ImmutableMap;
import net.mehvahdjukaar.hauntedharvest.Halloween;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.behavior.EntityTracker;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.memory.WalkTarget;

import java.util.List;

public class FindAdultThatHasCandy extends Behavior<LivingEntity> {
    //walk target distance at which it will stop walking
    private final float speedModifier;
    private final int interactionRangeSqr;
    private final MemoryModuleType<LivingEntity> memory = MemoryModuleType.INTERACTION_TARGET;

    public FindAdultThatHasCandy(int range, float speed) {
        super(ImmutableMap.of(
                MemoryModuleType.WALK_TARGET, MemoryStatus.REGISTERED,
                MemoryModuleType.ATTACK_TARGET, MemoryStatus.VALUE_ABSENT,
                MemoryModuleType.LOOK_TARGET, MemoryStatus.VALUE_ABSENT,
                MemoryModuleType.NEAREST_LIVING_ENTITIES, MemoryStatus.VALUE_PRESENT));
        this.speedModifier = speed;
        this.interactionRangeSqr = range * range;
    }

    @Override
    protected boolean checkExtraStartConditions(ServerLevel pLevel, LivingEntity pOwner) {
        //do not search while trick or treating
        if(Halloween.IS_TRICK_OR_TREATING.test(pOwner))return false;
        List<LivingEntity> list = pOwner.getBrain().getMemory(MemoryModuleType.NEAREST_LIVING_ENTITIES).get();
        return list.stream().anyMatch(t -> this.isTargetValid(pOwner, t));
    }

    private boolean isTargetValid(LivingEntity self, LivingEntity target) {
        if((target.getType() == EntityType.VILLAGER && !target.isBaby()) || target.getType() == EntityType.WITCH){
            return self instanceof IHalloweenVillager e && !e.isEntityOnCooldown(target);
        }
        return false;
    }

    @Override
    protected void start(ServerLevel pLevel, LivingEntity pEntity, long pGameTime) {
        Brain<?> brain = pEntity.getBrain();
        brain.getMemory(MemoryModuleType.NEAREST_LIVING_ENTITIES)
                .flatMap(t -> t.stream()
                .filter((villager) -> villager.distanceToSqr(pEntity) <= (double) this.interactionRangeSqr)
                .filter(v -> this.isTargetValid(pEntity, v))
                .findFirst()).ifPresent((foundTarget) -> {
            brain.setMemory(this.memory, foundTarget);
            brain.setMemory(MemoryModuleType.LOOK_TARGET, new EntityTracker(foundTarget, true));
            brain.setMemory(MemoryModuleType.WALK_TARGET, new WalkTarget(new EntityTracker(foundTarget, false), this.speedModifier, 2));
        });
    }
}
