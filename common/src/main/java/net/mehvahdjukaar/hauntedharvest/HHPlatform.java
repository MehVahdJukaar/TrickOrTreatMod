package net.mehvahdjukaar.hauntedharvest;

import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.world.entity.item.ItemEntity;

public class HHPlatform {

    @ExpectPlatform
    public static void setItemLifespan(ItemEntity item, int lifespan){
        throw new AssertionError();
    }
}
