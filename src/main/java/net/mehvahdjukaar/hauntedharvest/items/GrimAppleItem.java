package net.mehvahdjukaar.hauntedharvest.items;

import net.mehvahdjukaar.hauntedharvest.ai.IHalloweenVillager;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;

public class GrimAppleItem extends Item {
    public GrimAppleItem(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public ItemStack finishUsingItem(ItemStack pStack, Level pLevel, LivingEntity entity) {
        if (entity instanceof IHalloweenVillager v){
            v.startConverting();
            pLevel.gameEvent(entity, GameEvent.EAT, entity.eyeBlockPosition());
            pLevel.playSound((Player)null, entity.getX(), entity.getY(), entity.getZ(), entity.getEatingSound(pStack), SoundSource.NEUTRAL, 1.0F, 1.0F + (pLevel.random.nextFloat() - pLevel.random.nextFloat()) * 0.4F);
            pStack.shrink(1);

            entity.gameEvent(GameEvent.EAT);
            return pStack;
        }
        return super.finishUsingItem(pStack, pLevel, entity);
    }
}
