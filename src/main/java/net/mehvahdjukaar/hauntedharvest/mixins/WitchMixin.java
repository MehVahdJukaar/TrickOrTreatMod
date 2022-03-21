package net.mehvahdjukaar.hauntedharvest.mixins;

import net.mehvahdjukaar.hauntedharvest.Halloween;
import net.mehvahdjukaar.hauntedharvest.ai.GiveCandyWitchGoal;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Witch;
import net.minecraft.world.entity.raid.Raider;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Witch.class)
public abstract class WitchMixin extends Raider {


    protected WitchMixin(EntityType<? extends Raider> p_37839_, Level p_37840_) {
        super(p_37839_, p_37840_);
    }

    @Inject(method = "registerGoals", at = @At("RETURN"))
    public void findTarget(CallbackInfo ci){
        if(Halloween.isHalloweenSeason(this.level))
        this.goalSelector.addGoal(2, new GiveCandyWitchGoal(this));
    }

}