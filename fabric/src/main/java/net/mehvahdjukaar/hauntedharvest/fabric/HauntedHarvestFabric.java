package net.mehvahdjukaar.hauntedharvest.fabric;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.CommonLifecycleEvents;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.mehvahdjukaar.hauntedharvest.HauntedHarvest;

public class HauntedHarvestFabric implements ModInitializer {


    @Override
    public void onInitialize() {

        HauntedHarvest.commonInit();

        UseBlockCallback.EVENT.register(HauntedHarvest::onRightClickBlock);

        CommonLifecycleEvents.TAGS_LOADED.register((a, b) -> HauntedHarvest.onTagLoad());

        ClientEntityEvents.ENTITY_LOAD.register(HauntedHarvest::onClientEntityLoad);
    }


}
