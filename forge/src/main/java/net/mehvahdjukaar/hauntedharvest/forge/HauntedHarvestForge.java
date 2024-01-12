package net.mehvahdjukaar.hauntedharvest.forge;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.mehvahdjukaar.hauntedharvest.HauntedHarvest;
import net.mehvahdjukaar.hauntedharvest.reg.ClientRegistry;
import net.mehvahdjukaar.hauntedharvest.reg.ModRegistry;
import net.mehvahdjukaar.moonlight.api.client.texture_renderer.FrameBufferBackedDynamicTexture;
import net.mehvahdjukaar.moonlight.api.client.texture_renderer.RenderedTexturesManager;
import net.mehvahdjukaar.moonlight.api.platform.PlatHelper;
import net.mehvahdjukaar.moonlight.api.util.Utils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Screenshot;
import net.minecraft.client.gui.screens.inventory.MerchantScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FlowerPotBlock;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TagsUpdatedEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.MissingMappingsEvent;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.function.Consumer;

/**
 * Author: MehVahdJukaar
 */
@Mod(HauntedHarvest.MOD_ID)
public class HauntedHarvestForge {

    public HauntedHarvestForge() {

        HauntedHarvest.commonInit();

        MinecraftForge.EVENT_BUS.register(this);

        PlatHelper.addCommonSetup(()->{
            ((FlowerPotBlock) Blocks.FLOWER_POT)
                    .addPlant(Utils.getID(ModRegistry.CORN_BASE.get()), ModRegistry.CORN_POT);
        });
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
    public void onServerTick(TickEvent.ServerTickEvent event) {
        if( event.phase == TickEvent.Phase.END){
            ServerLevel overworld = event.getServer().overworld();
            if(overworld.getGameTime() % 10000 == 0){
                HauntedHarvest.getSeasonManager().refresh();
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
