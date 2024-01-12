package net.mehvahdjukaar.hauntedharvest.ai;

import net.mehvahdjukaar.hauntedharvest.HauntedHarvest;
import net.mehvahdjukaar.hauntedharvest.reg.ModRegistry;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;

import java.util.EnumSet;

public class GiveCandyWitchGoal extends Goal {
    protected final int randomInterval;

    protected final Mob witch;

    protected Villager target;

    private boolean hasGivenCandy = false;
    private int tickSinceStarted = 0;
    private int timeToGiveCandy = 0;

    public GiveCandyWitchGoal(Mob pMob) {
        this.randomInterval = 5;
        this.witch = pMob;
        this.setFlags(EnumSet.of(Flag.LOOK, Flag.MOVE));
    }

    /**
     * Returns whether execution should begin. You can also read and cache any state necessary for execution in this
     * method as well.
     */
    @Override
    public boolean canUse() {
        if (!HauntedHarvest.isTrickOrTreatTime(this.witch.level())) return false;
        if (this.randomInterval > 0 && this.witch.getRandom().nextInt(this.randomInterval) != 0) {
            return false;
        } else {
            this.findTarget();
            return this.target != null;
        }
    }

    protected AABB getTargetSearchArea(double pTargetDistance) {
        return this.witch.getBoundingBox().inflate(pTargetDistance, 4.0D, pTargetDistance);
    }

    protected void findTarget() {
        this.target = this.witch.level().getEntitiesOfClass(
                        Villager.class, this.getTargetSearchArea(4), this::isValidTarget)
                .stream().findAny().orElse(null);
    }

    private boolean isValidTarget(Villager target) {
        return GiveCandyToBabies.isValidTrickOrTreater(this.witch, target);
    }

    /**
     * Execute a one shot task or start executing a continuous task
     */
    @Override
    public void start() {
        this.witch.lookAt(this.target, 45, 80);

        super.start();

        this.timeToGiveCandy = 20 + this.witch.level().random.nextInt(30);
        this.tickSinceStarted = 0;
        this.hasGivenCandy = false;
    }

    @Override
    public void stop() {
        super.stop();
        this.hasGivenCandy = false;
        this.tickSinceStarted = 0;
    }

    @Override
    public boolean canContinueToUse() {
        if (tickSinceStarted < 300 && !hasGivenCandy) {
            return target != null && target.isAlive() && this.isValidTarget(target) &&
                    this.witch.getSensing().hasLineOfSight(target);

        }
        return false;
    }

    @Override
    public void tick() {

        this.witch.getLookControl().setLookAt(this.target.getX(), this.target.getEyeY(), this.target.getZ());
        //this.mob.lookAt(this.target, 45, 80);
        if (this.target != null && this.tickSinceStarted > this.timeToGiveCandy) {

            Level level = this.witch.level();
            int r = level.getRandom().nextInt(12);

            if (r == 0) {
                ItemStack stack = new ItemStack(ModRegistry.GRIM_APPLE.get());
                GiveCandyToBabies.throwCandy(this.witch, target, stack);
                level.broadcastEntityEvent(this.witch, (byte) 15);
            }
            //scare villager
            else if (r < 5) {
                GiveCandyToBabies.spookVillager(target, this.witch);
                level.broadcastEntityEvent(this.witch, (byte) 15);
            } else {
                ItemStack stack = new ItemStack(ModRegistry.ROTTEN_APPLE.get());
                GiveCandyToBabies.throwCandy(this.witch, target, stack);
            }

            this.hasGivenCandy = true;
            if (target instanceof IHalloweenVillager e) {
                e.hauntedharvest$setEntityOnCooldown(this.witch, 140);
            }
        }
        this.tickSinceStarted++;

    }
}