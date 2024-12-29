package net.mehvahdjukaar.hauntedharvest.neoforge;

import net.mehvahdjukaar.hauntedharvest.HauntedHarvest;
import net.mehvahdjukaar.hauntedharvest.configs.CommonConfigs;
import net.mehvahdjukaar.hauntedharvest.integration.neoforge.configured.ModConfigSelectScreen;
import net.mehvahdjukaar.hauntedharvest.reg.ModRegistry;
import net.mehvahdjukaar.moonlight.api.platform.ClientHelper;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraftforge.client.event.RenderNameTagEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

public class HauntedHarvestForgeClient {

    public static void init() {
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        bus.register(HauntedHarvestForgeClient.class);

        ClientHelper.addClientSetup(() -> {
            if (ModList.get().isLoaded("configured")) {
                ModConfigSelectScreen.registerConfigScreen(HauntedHarvest.MOD_ID, ModConfigSelectScreen::new);
            }
        });

        MinecraftForge.EVENT_BUS.addListener(HauntedHarvestForgeClient::onRenderNameTag);

    }

    public static void onRenderNameTag(RenderNameTagEvent event) {
        if (CommonConfigs.PAPER_BAG_NAME_TAG.get() && event.getEntity() instanceof LivingEntity le) {
            Item slot = le.getItemBySlot(EquipmentSlot.HEAD).getItem();
            if (slot == ModRegistry.PAPER_BAG_ITEM.get()) {
                event.setResult(Event.Result.DENY);
            }
        }
    }
}
