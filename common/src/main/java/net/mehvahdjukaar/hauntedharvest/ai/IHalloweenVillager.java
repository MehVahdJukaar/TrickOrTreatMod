package net.mehvahdjukaar.hauntedharvest.ai;

import net.minecraft.world.entity.Entity;

public interface IHalloweenVillager {
    boolean hauntedharvest$isEntityOnCooldown(Entity e);

    void hauntedharvest$setEntityOnCooldown(Entity e);

    void hauntedharvest$setEntityOnCooldown(Entity e, int cooldownSec);

    void hauntedharvest$startConverting();

    boolean hauntedharvest$isConverting();
}
