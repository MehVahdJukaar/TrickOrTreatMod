package net.mehvahdjukaar.hauntedharvest.forge;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.mehvahdjukaar.hauntedharvest.HauntedHarvest;
import net.mehvahdjukaar.hauntedharvest.client.CarvingManager;
import net.mehvahdjukaar.hauntedharvest.integration.forge.configured.ModConfigSelectScreen;
import net.mehvahdjukaar.hauntedharvest.reg.ClientRegistry;
import net.mehvahdjukaar.moonlight.api.client.texture_renderer.FrameBufferBackedDynamicTexture;
import net.mehvahdjukaar.moonlight.api.client.texture_renderer.RenderedTexturesManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Screenshot;
import net.minecraft.client.gui.screens.inventory.MerchantScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterShadersEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

@Mod.EventBusSubscriber(modid = HauntedHarvest.MOD_ID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class HauntedHarvestForgeClient {


    @SubscribeEvent
    public static void setup(final FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            ClientRegistry.setup();

            if (ModList.get().isLoaded("configured")) {
                ModConfigSelectScreen.registerConfigScreen(HauntedHarvest.MOD_ID, ModConfigSelectScreen::new);
            }
        });
    }

    private static ShaderInstance blur;

    @SubscribeEvent
    public static void registerShader(RegisterShadersEvent event) {
        try {
            var blur = new ShaderInstance(event.getResourceProvider(), HauntedHarvest.res("blur"),
                    DefaultVertexFormat.POSITION_TEX);

            event.registerShader(blur, s -> HauntedHarvestForgeClient.blur = s);
        } catch (Exception e) {
            HauntedHarvest.LOGGER.error("Failed to parse blur shader: " + e);
        }
    }

    public static ShaderInstance getBlur() {
        // blur.getUniform("Radius").set(8f);
        return blur;
    }


}
