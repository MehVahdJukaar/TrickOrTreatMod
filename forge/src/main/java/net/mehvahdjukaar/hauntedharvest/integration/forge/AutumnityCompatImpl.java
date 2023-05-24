package net.mehvahdjukaar.hauntedharvest.integration.forge;

import com.google.common.base.Suppliers;
import com.teamabnormals.autumnity.common.item.TurkeyEggItem;
import com.teamabnormals.autumnity.core.other.AutumnityCompat;
import com.teamabnormals.autumnity.core.registry.AutumnityBlocks;
import com.teamabnormals.autumnity.core.registry.AutumnityFeatures;
import net.mehvahdjukaar.hauntedharvest.blocks.ModCarvedPumpkinBlock;
import net.mehvahdjukaar.hauntedharvest.blocks.PumpkinType;
import net.mehvahdjukaar.moonlight.api.platform.RegHelper;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraftforge.fml.ModList;

import java.util.function.Supplier;

import static net.mehvahdjukaar.hauntedharvest.reg.ModRegistry.regPumpkin;

public class AutumnityCompatImpl {

    public static final boolean ENDERGEtIC = ModList.get().isLoaded("endergetic");

    private static final Supplier<Item> ENDER_TORCH = Suppliers.memoize(() -> BuiltInRegistries.ITEM.getOptional(
            new ResourceLocation("endergetic:ender_torch")).orElse(null));


    public static void init() {
        RegHelper.addItemsToTabsRegistration(AutumnityCompatImpl::addItemsToTabs);
    }

    private static void addItemsToTabs(RegHelper.ItemToTabEvent event) {
        //TODO:
    }

    public static void setup() {
        PumpkinType.register(ENDER);
        PumpkinType.register(GREEN);
    }


    public static final Supplier<ModCarvedPumpkinBlock> ENDER_JACK_O_LANTERN = regPumpkin("ender_jack_o_lantern",
            () -> new ModCarvedPumpkinBlock(BlockBehaviour.Properties.copy(Blocks.CARVED_PUMPKIN)
                    .lightLevel(s -> 10), AutumnityCompatImpl.ENDER));


    private static final PumpkinType ENDER = PumpkinType.register(new PumpkinType("ender_jack_o_lantern",
            ENDER_TORCH, ENDER_JACK_O_LANTERN));

    public static final Supplier<ModCarvedPumpkinBlock> GREEN_JACK_O_LANTERN = regPumpkin("green_jack_o_lantern",
            () -> new ModCarvedPumpkinBlock(BlockBehaviour.Properties.copy(Blocks.CARVED_PUMPKIN)
                    .lightLevel(s -> 10), AutumnityCompatImpl.GREEN));


    private static final PumpkinType GREEN = PumpkinType.register(new PumpkinType("green_jack_o_lantern",
            () -> null, GREEN_JACK_O_LANTERN));
}
