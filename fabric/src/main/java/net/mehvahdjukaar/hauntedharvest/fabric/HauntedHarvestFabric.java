package net.mehvahdjukaar.hauntedharvest.fabric;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.mehvahdjukaar.hauntedharvest.HauntedHarvest;
import net.mehvahdjukaar.hauntedharvest.reg.ClientRegistry;
import net.mehvahdjukaar.hauntedharvest.reg.ModRegistry;
import net.mehvahdjukaar.moonlight.api.client.ICustomItemRendererProvider;
import net.mehvahdjukaar.moonlight.api.platform.PlatformHelper;
import net.mehvahdjukaar.moonlight.fabric.FabricSetupCallbacks;
import net.minecraft.world.level.ItemLike;

public class HauntedHarvestFabric implements ModInitializer {


    @Override
    public void onInitialize() {

        HauntedHarvest.commonInit();

        FabricSetupCallbacks.COMMON_SETUP.add(HauntedHarvestFabric::commonSetup);


        if (PlatformHelper.getEnv().isClient()) {
            FabricSetupCallbacks.CLIENT_SETUP.add(HauntedHarvestFabric::clientSetup);
        }

        UseBlockCallback.EVENT.register(HauntedHarvest::onRightClickBlock);
    }

    private static void clientSetup() {
        ClientRegistry.init();

        ClientRegistry.init();

        registerISTER(ModRegistry.MOD_JACK_O_LANTERN_ITEM.get());
        registerISTER(ModRegistry.MOD_CARVED_PUMPKIN_ITEM.get());

        ClientRegistry.setup();
    }

    private static void commonSetup() {
        HauntedHarvest.commonSetup();
    }



    private static void registerISTER(ItemLike itemLike) {
        ((ICustomItemRendererProvider) itemLike.asItem()).registerFabricRenderer();
    }

}
