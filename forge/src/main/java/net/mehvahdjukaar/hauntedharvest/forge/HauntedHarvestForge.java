package net.mehvahdjukaar.hauntedharvest.forge;

import com.mojang.serialization.Codec;
import net.mehvahdjukaar.hauntedharvest.HauntedHarvest;
import net.mehvahdjukaar.hauntedharvest.loot.AddItemModifier;
import net.mehvahdjukaar.hauntedharvest.reg.ClientRegistry;
import net.mehvahdjukaar.moonlight.api.platform.PlatformHelper;
import net.minecraft.world.InteractionResult;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.loot.IGlobalLootModifier;
import net.minecraftforge.event.TagsUpdatedEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.level.LevelEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import vazkii.quark.content.tweaks.module.SimpleHarvestModule;

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

    public static final DeferredRegister<Codec<? extends IGlobalLootModifier>> LOOT_MODIFIERS = DeferredRegister.create(
            ForgeRegistries.Keys.GLOBAL_LOOT_MODIFIER_SERIALIZERS, HauntedHarvest.MOD_ID);

    public static final RegistryObject<Codec<? extends IGlobalLootModifier>> ROTTEN_APPLE_GLM =
            LOOT_MODIFIERS.register("add_item", AddItemModifier.CODEC);


    public static void init(final FMLCommonSetupEvent event) {
        event.enqueueWork(HauntedHarvest::commonSetup);
    }

}
