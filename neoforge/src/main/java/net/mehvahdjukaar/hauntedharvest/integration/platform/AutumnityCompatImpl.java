package net.mehvahdjukaar.hauntedharvest.integration.platform;

import com.google.common.base.Suppliers;
import net.mehvahdjukaar.hauntedharvest.blocks.ModCarvedPumpkinBlock;
import net.mehvahdjukaar.hauntedharvest.blocks.PumpkinType;
import net.mehvahdjukaar.hauntedharvest.blocks.RedstoneCarvedPumpkinBlock;
import net.mehvahdjukaar.moonlight.api.misc.RegSupplier;
import net.mehvahdjukaar.moonlight.api.platform.PlatHelper;
import net.mehvahdjukaar.moonlight.api.platform.RegHelper;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

import static net.mehvahdjukaar.hauntedharvest.reg.ModRegistry.regPumpkin;

public class AutumnityCompatImpl {

    private static EndergeticCompat ENDERGETIC;
    private static CavesAndChasmCompat CAVERNS_AND_CHASMS;

    public static void init() {
        if (PlatHelper.isModLoaded("endergetic")) {
            ENDERGETIC = new EndergeticCompat();
        }
        if (PlatHelper.isModLoaded("caverns_and_chasms")) {
            CAVERNS_AND_CHASMS = new CavesAndChasmCompat();
        }

        RegHelper.addItemsToTabsRegistration(AutumnityCompatImpl::addItemsToTabs);
    }

    private static void addItemsToTabs(RegHelper.ItemToTabEvent event) {
        //TODO:
    }

    public static final Supplier<ModCarvedPumpkinBlock> SOUL_JACK_O_LANTERN = regPumpkin("soul_jack_o_lantern",
            () -> new ModCarvedPumpkinBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.CARVED_PUMPKIN)
                    .lightLevel(s -> 10), AutumnityCompatImpl.SOUL));

    public static final Supplier<ModCarvedPumpkinBlock> REDSTONE_JACK_O_LANTERN = regPumpkin("redstone_jack_o_lantern",
            () -> new RedstoneCarvedPumpkinBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.CARVED_PUMPKIN),
                    AutumnityCompatImpl.REDSTONE));

    // These compat jack o' lanterns have no plain (non block-entity) vanilla variant, so we light
    // plain carved pumpkins into the modded carvable block itself instead of a missing block (which crashed).
    public static final RegSupplier<PumpkinType> SOUL = PumpkinType.register(res("soul_jack_o_lantern"),
            () -> Items.SOUL_TORCH, SOUL_JACK_O_LANTERN, SOUL_JACK_O_LANTERN);

    public static final RegSupplier<PumpkinType> REDSTONE = PumpkinType.register(res("redstone_jack_o_lantern"),
            () -> Items.REDSTONE_TORCH, REDSTONE_JACK_O_LANTERN, REDSTONE_JACK_O_LANTERN);

    private static class EndergeticCompat {
        private final Supplier<Item> enderTorch = Suppliers.memoize(() -> BuiltInRegistries.ITEM.getOptional(
                ResourceLocation.parse("endergetic:ender_torch")).orElse(null));

        public final Supplier<ModCarvedPumpkinBlock> enderJackOLantern = regPumpkin("ender_jack_o_lantern",
                () -> new ModCarvedPumpkinBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.CARVED_PUMPKIN)
                        .lightLevel(s -> 10), this.ender));

        private final RegSupplier<PumpkinType> ender = PumpkinType.register(res("ender_jack_o_lantern"),
                enderTorch, enderJackOLantern, enderJackOLantern);
    }

    private static class CavesAndChasmCompat {
        private final Supplier<Item> cupricTorch = Suppliers.memoize(() -> BuiltInRegistries.ITEM.getOptional(
                ResourceLocation.parse("caverns_and_chasms:cupric_torch")).orElse(null));

        public final Supplier<ModCarvedPumpkinBlock> cupricJackOLantern = regPumpkin("cupric_jack_o_lantern",
                () -> new ModCarvedPumpkinBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.CARVED_PUMPKIN)
                        .lightLevel(s -> 10), this.cupric));

        private final RegSupplier<PumpkinType> cupric = PumpkinType.register(res("cupric_jack_o_lantern"),
                cupricTorch, cupricJackOLantern, cupricJackOLantern);
    }

    private static @NotNull ResourceLocation res(String name) {
        return ResourceLocation.fromNamespaceAndPath("autumnity", name);
    }
}
