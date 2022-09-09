package net.mehvahdjukaar.hauntedharvest.forge;

import com.mojang.serialization.Codec;
import net.mehvahdjukaar.hauntedharvest.HauntedHarvest;
import net.mehvahdjukaar.hauntedharvest.loot.AddItemModifier;
import net.mehvahdjukaar.hauntedharvest.reg.ClientRegistry;
import net.mehvahdjukaar.moonlight.api.platform.PlatformHelper;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.loot.IGlobalLootModifier;
import net.minecraftforge.event.TagsUpdatedEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

/**
 * Author: MehVahdJukaar
 */
@Mod(HauntedHarvest.MOD_ID)
public class HauntedHarvestForge {

    public HauntedHarvestForge() {


        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        bus.addListener(HauntedHarvestForge::init);

        HauntedHarvest.commonInit();

        if (PlatformHelper.getEnv().isClient()) {
            ClientRegistry.init();
        }

        MinecraftForge.EVENT_BUS.register(this);
    }

    public static final DeferredRegister<Codec<? extends IGlobalLootModifier>> LOOT_MODIFIERS = DeferredRegister.create(
            ForgeRegistries.Keys.GLOBAL_LOOT_MODIFIER_SERIALIZERS, HauntedHarvest.MOD_ID);

    public static final RegistryObject<Codec<? extends IGlobalLootModifier>> ROTTEN_APPLE_GLM =
            LOOT_MODIFIERS.register("add_item", AddItemModifier.CODEC);


    public static void init(final FMLCommonSetupEvent event) {
        event.enqueueWork(HauntedHarvest::commonSetup);
    }

    @SubscribeEvent
    public static void onTagLoad(TagsUpdatedEvent event) {
        HauntedHarvest.onTagLoad();
    }
}
