package net.mehvahdjukaar.hauntedharvest.forge;

import net.minecraft.world.entity.item.ItemEntity;

public class HHPlatformImpl {
    public static void setItemLifespan(ItemEntity item, int lifespan) {
        item.lifespan = lifespan;
    }
}
