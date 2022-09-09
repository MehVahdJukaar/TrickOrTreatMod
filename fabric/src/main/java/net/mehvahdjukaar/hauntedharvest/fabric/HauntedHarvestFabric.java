package net.mehvahdjukaar.hauntedharvest.fabric;

import net.fabricmc.api.ModInitializer;
import net.mehvahdjukaar.hauntedharvest.HauntedHarvest;
import net.mehvahdjukaar.hauntedharvest.reg.ClientRegistry;
import net.mehvahdjukaar.moonlight.api.platform.PlatformHelper;
import net.mehvahdjukaar.moonlight.fabric.FabricSetupCallbacks;

public class HauntedHarvestFabric implements ModInitializer {


    @Override
    public void onInitialize() {

        HauntedHarvest.commonInit();

        FabricSetupCallbacks.COMMON_SETUP.add(HauntedHarvestFabric::commonSetup);


        if (PlatformHelper.getEnv().isClient()) {
            FabricSetupCallbacks.CLIENT_SETUP.add(HauntedHarvestFabric::initClient);
        }
    }

    private static void initClient() {
        ClientRegistry.init();
    }

    private static void commonSetup() {
        HauntedHarvest.commonSetup();
    }


}
