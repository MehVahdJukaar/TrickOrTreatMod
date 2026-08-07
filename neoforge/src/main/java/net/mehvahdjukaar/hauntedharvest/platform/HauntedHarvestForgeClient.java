package net.mehvahdjukaar.hauntedharvest.platform;

import net.mehvahdjukaar.hauntedharvest.configs.CommonConfigs;
import net.mehvahdjukaar.hauntedharvest.reg.ModRegistry;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.client.event.RenderNameTagEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.common.util.TriState;

public class HauntedHarvestForgeClient {

    public static void init(IEventBus bus) {
        NeoForge.EVENT_BUS.addListener(HauntedHarvestForgeClient::onRenderNameTag);
    }

    public static void onRenderNameTag(RenderNameTagEvent event) {
        if (CommonConfigs.PAPER_BAG_NAME_TAG.get() && event.getEntity() instanceof LivingEntity le) {
            Item slot = le.getItemBySlot(EquipmentSlot.HEAD).getItem();
            if (slot == ModRegistry.PAPER_BAG_ITEM.get()) {
                event.setCanRender(TriState.FALSE);
            }
        }
    }
}
