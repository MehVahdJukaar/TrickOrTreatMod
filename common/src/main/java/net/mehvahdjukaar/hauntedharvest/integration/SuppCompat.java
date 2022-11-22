package net.mehvahdjukaar.hauntedharvest.integration;

import net.mehvahdjukaar.supplementaries.common.items.CandyItem;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

public class SuppCompat {

    public static void triggerSweetTooth(Level level, LivingEntity entity) {
        CandyItem.increaseSweetTooth(level, entity, 8 * 20);
    }
}
