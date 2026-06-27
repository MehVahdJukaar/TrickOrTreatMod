package net.mehvahdjukaar.hauntedharvest.integration.platform;

import com.google.common.base.Suppliers;
import net.mehvahdjukaar.hauntedharvest.blocks.ModCarvedPumpkinBlock;
import net.mehvahdjukaar.hauntedharvest.blocks.PumpkinType;
import net.mehvahdjukaar.hauntedharvest.blocks.RedstoneCarvedPumpkinBlock;
import net.mehvahdjukaar.moonlight.api.misc.OptRegSupplier;
import net.mehvahdjukaar.moonlight.api.misc.RegSupplier;
import net.mehvahdjukaar.moonlight.api.platform.PlatHelper;
import net.mehvahdjukaar.moonlight.api.platform.RegHelper;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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

    private static final Supplier<Block> SOUL_JACK_O_LANTERN_BLOCK = block("soul_jack_o_lantern");
    private static final Supplier<Block> REDSTONE_JACK_O_LANTERN_BLOCK = block("redstone_jack_o_lantern");


    public static final Supplier<ModCarvedPumpkinBlock> SOUL_JACK_O_LANTERN = regPumpkin("soul_jack_o_lantern",
            () -> new ModCarvedPumpkinBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.CARVED_PUMPKIN)
                    .lightLevel(s -> 10), AutumnityCompatImpl.SOUL));

    public static final Supplier<ModCarvedPumpkinBlock> REDSTONE_JACK_O_LANTERN = regPumpkin("redstone_jack_o_lantern",
            () -> new RedstoneCarvedPumpkinBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.CARVED_PUMPKIN),
                    AutumnityCompatImpl.REDSTONE));

    public static final RegSupplier<PumpkinType> SOUL = PumpkinType.register(res("soul_jack_o_lantern"),
            () -> Items.SOUL_TORCH, SOUL_JACK_O_LANTERN, SOUL_JACK_O_LANTERN_BLOCK);

    public static final RegSupplier<PumpkinType> REDSTONE = PumpkinType.register(res("redstone_jack_o_lantern"),
            () -> Items.REDSTONE_TORCH, REDSTONE_JACK_O_LANTERN, REDSTONE_JACK_O_LANTERN_BLOCK);

    private static class EndergeticCompat {
        private static final Supplier<Block> ENDER_JACK_O_LANTERN_BLOCK = block("ender_jack_o_lantern");


        private final Supplier<Item> enderTorch = Suppliers.memoize(() -> BuiltInRegistries.ITEM.getOptional(
                ResourceLocation.parse("endergetic:ender_torch")).orElse(null));

        public final Supplier<ModCarvedPumpkinBlock> enderJackOLantern = regPumpkin("ender_jack_o_lantern",
                () -> new ModCarvedPumpkinBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.CARVED_PUMPKIN)
                        .lightLevel(s -> 10), this.ender));

        private final RegSupplier<PumpkinType> ender = PumpkinType.register(res("ender_jack_o_lantern"),
                enderTorch, enderJackOLantern, ENDER_JACK_O_LANTERN_BLOCK);
    }

    private static class CavesAndChasmCompat {
        private static final Supplier<Block> CUPRIC_JACK_O_LANTERN_BLOCK = block("green_jack_o_lantern");

        private  final Supplier<Item> greenTorch = Suppliers.memoize(() -> BuiltInRegistries.ITEM.getOptional(
                ResourceLocation.parse("caverns_and_chasms:cupric_torch")).orElse(null));

        public final Supplier<ModCarvedPumpkinBlock> greenJackOLantern = regPumpkin("green_jack_o_lantern",
                () -> new ModCarvedPumpkinBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.CARVED_PUMPKIN)
                        .lightLevel(s -> 10), this.green));

        private final  RegSupplier<PumpkinType> green = PumpkinType.register(res("green_jack_o_lantern"),
                greenTorch, greenJackOLantern, CUPRIC_JACK_O_LANTERN_BLOCK);

    }

    private static Supplier<Block> block(String name) {
       return OptRegSupplier.of(res(name), BuiltInRegistries.BLOCK);
    }

    private static @NotNull ResourceLocation res(String name) {
        return ResourceLocation.fromNamespaceAndPath("autumnity", name);
    }
}
