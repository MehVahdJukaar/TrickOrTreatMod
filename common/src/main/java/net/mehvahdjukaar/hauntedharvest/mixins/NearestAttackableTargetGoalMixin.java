package net.mehvahdjukaar.hauntedharvest.mixins;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(NearestAttackableTargetGoal.class)
public abstract class NearestAttackableTargetGoalMixin {

    @Shadow protected LivingEntity target;

    @Inject(method = "findTarget", at = @At("RETURN"))
    public void findTarget(CallbackInfo ci){
        if(this.target instanceof AbstractVillager villager && villager.isBaby()){
            //makes babies immune but not during raid
            if(target.level() instanceof ServerLevel serer){
                this.target = null;
                //Raid raid = serer.getRaidAt(target.getOnPos());
               // if(raid == null || !raid.isActive()) this.target = null;
            }
        }
    }

}