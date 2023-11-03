package net.mehvahdjukaar.hauntedharvest.fabric;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.CommonLifecycleEvents;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.mehvahdjukaar.hauntedharvest.HauntedHarvest;
import net.mehvahdjukaar.hauntedharvest.blocks.PumpkinType;
import net.mehvahdjukaar.hauntedharvest.reg.ClientRegistry;
import net.mehvahdjukaar.moonlight.api.client.ICustomItemRendererProvider;
import net.mehvahdjukaar.moonlight.api.platform.PlatHelper;
import net.mehvahdjukaar.moonlight.fabric.MLFabricSetupCallbacks;
import net.minecraft.world.level.ItemLike;

public class HauntedHarvestFabric implements ModInitializer {


    @Override
    public void onInitialize() {

        HauntedHarvest.commonInit();

        UseBlockCallback.EVENT.register(HauntedHarvest::onRightClickBlock);

        CommonLifecycleEvents.TAGS_LOADED.register((a, b) -> HauntedHarvest.onTagLoad());
    }


}
