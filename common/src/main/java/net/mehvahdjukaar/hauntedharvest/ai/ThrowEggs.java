package net.mehvahdjukaar.hauntedharvest.ai;

import com.google.common.collect.ImmutableMap;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.behavior.BehaviorUtils;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.projectile.ThrownEgg;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import org.jetbrains.annotations.Nullable;

public class ThrowEggs extends Behavior<Villager> {
    private int cooldownBetweenAttacks;
    private int eggs;
    private final int maxRange;
    private int duration = 20 * 20;

    public ThrowEggs(int range) {
        super(ImmutableMap.of(
                MemoryModuleType.LOOK_TARGET, MemoryStatus.REGISTERED,
                MemoryModuleType.ATTACK_TARGET, MemoryStatus.VALUE_PRESENT));
        this.maxRange = range;
    }

    @Override
    protected boolean checkExtraStartConditions(ServerLevel pLevel, Villager pOwner) {
        LivingEntity livingentity = this.getAttackTarget(pOwner);
        if (livingentity == null || !livingentity.isAlive()) {
            clearAnger(pOwner);
            return false;
        }
        return BehaviorUtils.canSee(pOwner, livingentity) && livingentity.distanceToSqr(pOwner.getX(), pOwner.getY(), pOwner.getZ()) < maxRange * maxRange;
    }

    @Override
    protected boolean canStillUse(ServerLevel pLevel, Villager pEntity, long pGameTime) {
        return this.eggs == 0 || this.checkExtraStartConditions(pLevel, pEntity);
    }

    @Override
    protected void start(ServerLevel pLevel, Villager pEntity, long pGameTime) {

        LivingEntity livingentity = this.getAttackTarget(pEntity);
        BehaviorUtils.lookAtEntity(pEntity, livingentity);
        displayAsHeldItem(pEntity, new ItemStack(Items.EGG));
        if (eggs == 0) {
            this.duration = 20 * 20;
            this.eggs = pLevel.random.nextInt(2) + 1;
            this.cooldownBetweenAttacks = 35 + pLevel.random.nextInt(30);
        }
    }

    @Override
    protected void tick(ServerLevel pLevel, Villager pOwner, long pGameTime) {
        LivingEntity target = this.getAttackTarget(pOwner);
        if (target == null) return;
        BehaviorUtils.lookAtEntity(pOwner, target);
        if (this.cooldownBetweenAttacks-- == 0) {
            this.cooldownBetweenAttacks = 20 + pLevel.random.nextInt(30);
            this.eggs--;

            //this is always server side
            ThrownEgg egg = new ThrownEgg(pLevel, pOwner);
            if (egg instanceof IHarmlessProjectile e) e.setHarmless(true);
            double d0 = target.getY() - 0.5;
            double d1 = target.getX() - pOwner.getX();
            double d2 = d0 - egg.getY();
            double d3 = target.getZ() - pOwner.getZ();
            double distFactor = Math.sqrt(d1 * d1 + d3 * d3) * (double) 0.2F;
            egg.shoot(d1, (d2 + distFactor) * 0.5, d3, 1.1F, 8);
            pLevel.playSound(null, pOwner.getX(), pOwner.getY(), pOwner.getZ(), SoundEvents.EGG_THROW, SoundSource.NEUTRAL, 0.5F, 0.4F / (pLevel.getRandom().nextFloat() * 0.4F + 0.8F));
            pLevel.addFreshEntity(egg);

            if (this.eggs <= 0) {
                clearAnger(pOwner);
            }
        }
        if(this.duration--<=0){
            clearAnger(pOwner);
        }
    }

    public static void clearAnger(Villager pOwner) {
        pOwner.getBrain().eraseMemory(MemoryModuleType.ATTACK_TARGET);
        displayAsHeldItem(pOwner, ItemStack.EMPTY);
    }


    private static void displayAsHeldItem(Villager self, ItemStack p_182372_) {
        self.setItemSlot(EquipmentSlot.MAINHAND, p_182372_);
        self.setDropChance(EquipmentSlot.MAINHAND, 0.0F);
    }

    @Override
    protected void stop(ServerLevel pLevel, Villager pEntity, long pGameTime) {
        super.stop(pLevel, pEntity, pGameTime);

    }

    @Nullable
    private LivingEntity getAttackTarget(LivingEntity pMob) {
        return pMob.getBrain().getMemory(MemoryModuleType.ATTACK_TARGET).orElse(null);
    }
}