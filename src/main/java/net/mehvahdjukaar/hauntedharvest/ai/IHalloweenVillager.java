package net.mehvahdjukaar.hauntedharvest.ai;

import net.minecraft.world.entity.Entity;

public interface IHalloweenVillager {
    boolean isEntityOnCooldown(Entity e);

    void setEntityOnCooldown(Entity e);

    void setEntityOnCooldown(Entity e, int cooldownSec);

    void startConverting();

    boolean isConverting();
}
