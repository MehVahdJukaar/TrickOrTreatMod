package net.mehvahdjukaar.hauntedharvest.ai;

import com.google.common.collect.ImmutableMap;
import net.mehvahdjukaar.hauntedharvest.Halloween;
import net.mehvahdjukaar.hauntedharvest.init.ModRegistry;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.Vec3;

public class EatCandy extends Behavior<Villager> {

    private int eatingTime;

    private int cooldown = 0;

    public EatCandy(int minDur, int maxDur) {
        super(ImmutableMap.of(), minDur, maxDur);
    }

    @Override
    protected boolean checkExtraStartConditions(ServerLevel pLevel, Villager pOwner) {
        //TODO: find a way to clear this so they don't throw eggs as soon as night falls
        if (pOwner.getBrain().hasMemoryValue(MemoryModuleType.ATTACK_TARGET)) {
            pOwner.getBrain().eraseMemory(MemoryModuleType.ATTACK_TARGET);
        }
        if (cooldown-- > 0) return false;
        return pOwner.getInventory().hasAnyOf(Halloween.EATABLE);
    }

    @Override
    protected void start(ServerLevel pLevel, Villager pEntity, long pGameTime) {
        super.start(pLevel, pEntity, pGameTime);
        this.cooldown = 20 * (3 + pLevel.random.nextInt(4)) + pLevel.random.nextInt(20);
        //stay still
        pEntity.getBrain().eraseMemory(MemoryModuleType.WALK_TARGET);
        //hax
        pEntity.getNavigation().stop();

        this.eatingTime = 80;
        SimpleContainer inventory = pEntity.getInventory();
        for (int i = 0; i < inventory.getContainerSize(); ++i) {
            ItemStack itemstack = inventory.getItem(i);
            if (Halloween.EATABLE.contains(itemstack.getItem())) {
                ItemStack s = itemstack.split(1);
                if (itemstack.getCount() == 0) inventory.setItem(i, ItemStack.EMPTY);
                pEntity.setItemInHand(InteractionHand.MAIN_HAND, s);
            }
        }
    }

    @Override
    protected boolean canStillUse(ServerLevel pLevel, Villager pEntity, long pGameTime) {
        return eatingTime > 0 && Halloween.EATABLE.contains(pEntity.getMainHandItem().getItem());
    }

    @Override
    protected void tick(ServerLevel pLevel, Villager pOwner, long pGameTime) {
        if (this.eatingTime-- < 50) {
            if (eatingTime % 5 == 0) {
                ItemStack stack = pOwner.getMainHandItem();

                if (stack.isEmpty()) return;

                Vec3 pos = new Vec3(0, 0, 0.09D);
                //pos = pos.xRot(pOwner.getXRot() * ((float) Math.PI / 180F));
                pos = pos.yRot(-pOwner.getYRot() * ((float) Math.PI / 180F));
                pos = pos.add(pOwner.getX(), pOwner.getEyeY(), pOwner.getZ());

                pLevel.sendParticles(new ItemParticleOption(ParticleTypes.ITEM, stack),
                        pos.x, pos.y - 0.2, pos.z, 5,
                        0.03, 0.05, 0.03, 0.0D);


                pOwner.playSound(pOwner.getEatingSound(stack), 0.3F + 0.4F * (float) pLevel.random.nextInt(2),
                        (pLevel.random.nextFloat() - pLevel.random.nextFloat()) * 0.2F + 1.3F);
            }
        }
    }

    @Override
    protected void stop(ServerLevel pLevel, Villager pEntity, long pGameTime) {
        super.stop(pLevel, pEntity, pGameTime);

        if (pEntity.isBaby()) {
            Item item = pEntity.getMainHandItem().getItem();
            if (item != Items.AIR) {
                pEntity.setAge(pEntity.getAge() - (20 * 120));
                pEntity.heal(0.5f);

                if (item == ModRegistry.DEATH_APPLE.get()) {
                    if (pEntity instanceof IHalloweenVillager v) v.startConverting();
                } else if (item == ModRegistry.ROTTEN_APPLE.get() && pLevel.random.nextInt(5) == 0) {
                    pEntity.addEffect(new MobEffectInstance(MobEffects.CONFUSION, 60, 1));
                }
            }
        }
        pEntity.setItemInHand(InteractionHand.MAIN_HAND, ItemStack.EMPTY);
    }
}
