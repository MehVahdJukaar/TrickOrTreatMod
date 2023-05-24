package net.mehvahdjukaar.hauntedharvest.forge;

import net.mehvahdjukaar.hauntedharvest.HauntedHarvest;
import net.mehvahdjukaar.hauntedharvest.reg.ClientRegistry;
import net.mehvahdjukaar.moonlight.api.platform.PlatHelper;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.InteractionResult;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TagsUpdatedEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.MissingMappingsEvent;

/**
 * Author: MehVahdJukaar
 */
@Mod(HauntedHarvest.MOD_ID)
public class HauntedHarvestForge {

    public HauntedHarvestForge() {


        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        bus.addListener(HauntedHarvestForge::init);

        HauntedHarvest.commonInit();

        if (PlatHelper.getPhysicalSide().isClient()) {
            ClientRegistry.init();
        }

        MinecraftForge.EVENT_BUS.register(this);
    }

    public static void init(final FMLCommonSetupEvent event) {
        event.enqueueWork(HauntedHarvest::commonSetup);
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public void onUseBlock(PlayerInteractEvent.RightClickBlock event) {
        if (!event.isCanceled()) {
            var ret = HauntedHarvest.onRightClickBlock(event.getEntity(), event.getLevel(), event.getHand(), event.getHitVec());
            if (ret != InteractionResult.PASS) {
                event.setCanceled(true);
                event.setCancellationResult(ret);
            }
        }
    }

    @SubscribeEvent
    public void onTagLoad(TagsUpdatedEvent event) {
        HauntedHarvest.onTagLoad();
    }

    @SubscribeEvent
    public void onRemapBlocks(MissingMappingsEvent event) {
        for (var m : event.getMappings(ForgeRegistries.BLOCKS.getRegistryKey(), "harvestseason")) {
            String name = m.getKey().getPath();
            var o = BuiltInRegistries.BLOCK.getOptional(HauntedHarvest.res(name));
            o.ifPresent(m::remap);
        }
        for (var m : event.getMappings(ForgeRegistries.ITEMS.getRegistryKey(), "harvestseason")) {
            String name = m.getKey().getPath();
            var o = BuiltInRegistries.ITEM.getOptional(HauntedHarvest.res(name));
            o.ifPresent(m::remap);
        }
        for (var m : event.getMappings(ForgeRegistries.BLOCK_ENTITY_TYPES.getRegistryKey(), "harvestseason")) {
            String name = m.getKey().getPath();
            var o = BuiltInRegistries.BLOCK_ENTITY_TYPE.getOptional(HauntedHarvest.res(name));
            o.ifPresent(m::remap);
        }
    }

}
