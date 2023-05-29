package net.mehvahdjukaar.hauntedharvest.integration.forge;

import com.google.common.base.Suppliers;
import com.teamabnormals.autumnity.core.registry.AutumnityBlocks;
import net.mehvahdjukaar.hauntedharvest.blocks.ModCarvedPumpkinBlock;
import net.mehvahdjukaar.hauntedharvest.blocks.PumpkinType;
import net.mehvahdjukaar.hauntedharvest.blocks.RedstoneCarvedPumpkinBlock;
import net.mehvahdjukaar.hauntedharvest.reg.ModRegistry;
import net.mehvahdjukaar.moonlight.api.platform.PlatHelper;
import net.mehvahdjukaar.moonlight.api.platform.RegHelper;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;

import java.util.function.Supplier;

import static net.mehvahdjukaar.hauntedharvest.reg.ModRegistry.regPumpkin;

public class AutumnityCompatImpl {

    private static EndergeticCompat eg;
    private static CavesAndChasmCompat cc;

    public static void init() {
        if (PlatHelper.isModLoaded("endergetic")) {
            eg = new EndergeticCompat();
        }
        if (PlatHelper.isModLoaded("caverns_and_chasms")) {
            cc = new CavesAndChasmCompat();
        }

        RegHelper.addItemsToTabsRegistration(AutumnityCompatImpl::addItemsToTabs);
    }

    private static void addItemsToTabs(RegHelper.ItemToTabEvent event) {
        //TODO:
    }


    public static final Supplier<ModCarvedPumpkinBlock> SOUL_JACK_O_LANTERN = regPumpkin("soul_jack_o_lantern",
            () -> new ModCarvedPumpkinBlock(BlockBehaviour.Properties.copy(Blocks.CARVED_PUMPKIN)
                    .lightLevel(s -> 10), AutumnityCompatImpl.SOUL));

    public static final Supplier<ModCarvedPumpkinBlock> REDSTONE_JACK_O_LANTERN = regPumpkin("redstone_jack_o_lantern",
            () -> new RedstoneCarvedPumpkinBlock(BlockBehaviour.Properties.copy(Blocks.CARVED_PUMPKIN),
                    AutumnityCompatImpl.REDSTONE));

    public static final PumpkinType SOUL = PumpkinType.register(new PumpkinType("soul_jack_o_lantern",
            () -> Items.SOUL_TORCH, SOUL_JACK_O_LANTERN, AutumnityBlocks.SOUL_JACK_O_LANTERN));

    public static final PumpkinType REDSTONE = PumpkinType.register(new PumpkinType("redstone_jack_o_lantern",
            () -> Items.REDSTONE_TORCH, REDSTONE_JACK_O_LANTERN, AutumnityBlocks.REDSTONE_JACK_O_LANTERN));

    private static class EndergeticCompat {

        private final Supplier<Item> enderTorch = Suppliers.memoize(() -> BuiltInRegistries.ITEM.getOptional(
                new ResourceLocation("endergetic:ender_torch")).orElse(null));

        public final Supplier<ModCarvedPumpkinBlock> enderJackOLantern = regPumpkin("ender_jack_o_lantern",
                () -> new ModCarvedPumpkinBlock(BlockBehaviour.Properties.copy(Blocks.CARVED_PUMPKIN)
                        .lightLevel(s -> 10), this.ender));

        private final PumpkinType ender = PumpkinType.register(new PumpkinType("ender_jack_o_lantern",
                enderTorch, enderJackOLantern, AutumnityBlocks.ENDER_JACK_O_LANTERN));
    }

    private static class CavesAndChasmCompat {

        private final Supplier<Item> greenTorch = Suppliers.memoize(() -> BuiltInRegistries.ITEM.getOptional(
                new ResourceLocation("caverns_and_chasms:cupric_torch")).orElse(null));

        public final Supplier<ModCarvedPumpkinBlock> greenJackOLantern = regPumpkin("green_jack_o_lantern",
                () -> new ModCarvedPumpkinBlock(BlockBehaviour.Properties.copy(Blocks.CARVED_PUMPKIN)
                        .lightLevel(s -> 10), this.green));

        private final PumpkinType green = PumpkinType.register(new PumpkinType("green_jack_o_lantern",
                greenTorch, greenJackOLantern, AutumnityBlocks.CUPRIC_JACK_O_LANTERN));

    }


}
