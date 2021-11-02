package net.mehvahdjukaar.hauntedharvest.ai;

import com.google.common.collect.ImmutableMap;
import net.mehvahdjukaar.hauntedharvest.Halloween;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.behavior.BehaviorUtils;
import net.minecraft.world.entity.ai.behavior.EntityTracker;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.schedule.Activity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class AskCandy extends Behavior<Villager> {

    private int lookTime;
    private boolean gotCandy = false;
    private LivingEntity currentVillager = null;

    public AskCandy(int minDuration, int maxDuration) {
        super(ImmutableMap.of(
                        MemoryModuleType.INTERACTION_TARGET, MemoryStatus.VALUE_PRESENT),
                minDuration, maxDuration);
    }

    @Override
    public boolean checkExtraStartConditions(ServerLevel pLevel, Villager pOwner) {
        Brain<?> brain = pOwner.getBrain();
        LivingEntity livingentity = brain.getMemory(MemoryModuleType.INTERACTION_TARGET).orElse(null);
        return livingentity != null && Halloween.isTrickOrTreatTime(pLevel) &&
                (livingentity.getType() == EntityType.VILLAGER || livingentity.getType() == EntityType.WITCH)
                && pOwner.isAlive() && livingentity.isAlive() && pOwner.isBaby() && pOwner.distanceToSqr(livingentity) <= 5 * 3;
    }

    @Override
    public boolean canStillUse(ServerLevel pLevel, Villager pEntity, long pGameTime) {

        return !this.gotCandy && this.checkExtraStartConditions(pLevel, pEntity) && this.lookTime > 0 && pEntity.getBrain().getMemory(MemoryModuleType.INTERACTION_TARGET).isPresent();
    }

    @Override
    public void start(ServerLevel pLevel, Villager pEntity, long pGameTime) {
        super.start(pLevel, pEntity, pGameTime);
        LivingEntity target = this.lookAtTarget(pEntity);
        currentVillager = target;
        this.keepVillageAwake(target);

        target.getBrain().eraseMemory(MemoryModuleType.WALK_TARGET);
        displayAsHeldItem(pEntity, new ItemStack(Items.BUNDLE));
        BehaviorUtils.lockGazeAndWalkToEachOther(pEntity, target, 0.5F);
        this.lookTime = 200;
        this.gotCandy = false;
    }

    @Override
    public void tick(ServerLevel pLevel, Villager pOwner, long pGameTime) {
        LivingEntity target = this.lookAtTarget(pOwner);
        --this.lookTime;

        this.keepVillageAwake(target);

        //if it doesn't have bundle it means it got removed by picked up candy action. stopping
        if (!pOwner.getMainHandItem().is(Items.BUNDLE)) {
            this.gotCandy = true;
        }
    }

    private void keepVillageAwake(LivingEntity target) {
        //hacky
        if (target instanceof Villager) {
            target.getBrain().setMemory(MemoryModuleType.LAST_WOKEN, target.level.getGameTime()-1);
            if (target.isSleeping()) {
                target.stopSleeping();
                //frick your bed. for some reason all this isn't enough, and they keep going in and out constantly
                target.getBrain().eraseMemory(MemoryModuleType.NEAREST_BED);
                target.level.broadcastEntityEvent(target, (byte) 13);
                target.getBrain().setActiveActivityIfPossible(Activity.REST);
            }
        }
    }

    @Override
    public void stop(ServerLevel pLevel, Villager pEntity, long pGameTime) {
        super.stop(pLevel, pEntity, pGameTime);
        clearHeldItem(pEntity);
        if (!this.gotCandy) {
            Brain<?> brain = pEntity.getBrain();
            //using local target instead cause this seem to mess it up when first switching back
            //LivingEntity livingentity = brain.getMemory(MemoryModuleType.INTERACTION_TARGET).orElse(null);

            //trick
            //only have a chance cause they like to spam eggs a bit too much. it skips witches
            if (currentVillager instanceof Villager && currentVillager.isAlive()
                    && !currentVillager.isBaby() && pLevel.random.nextInt(10) < 7 ) {
                //egg time
                pLevel.broadcastEntityEvent(pEntity, (byte) 13);
                brain.setMemory(MemoryModuleType.ATTACK_TARGET, currentVillager);
                if (pEntity instanceof IHalloweenVillager c) {
                    c.setEntityOnCooldown(currentVillager);
                }
            }
        }
        pEntity.getBrain().eraseMemory(MemoryModuleType.LOOK_TARGET);
        pEntity.getBrain().eraseMemory(MemoryModuleType.INTERACTION_TARGET);
    }

    public static void clearHeldItem(Villager self) {
        self.setItemSlot(EquipmentSlot.MAINHAND, ItemStack.EMPTY);
        self.setDropChance(EquipmentSlot.MAINHAND, 0.085F);
    }

    public static void displayAsHeldItem(Villager self, ItemStack stack) {
        self.setItemSlot(EquipmentSlot.MAINHAND, stack);
        self.setDropChance(EquipmentSlot.MAINHAND, 0.0F);
    }

    private LivingEntity lookAtTarget(Villager self) {
        Brain<?> brain = self.getBrain();
        LivingEntity livingentity = brain.getMemory(MemoryModuleType.INTERACTION_TARGET).get();
        brain.setMemory(MemoryModuleType.LOOK_TARGET, new EntityTracker(livingentity, true));
        return livingentity;
    }

}