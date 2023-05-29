package net.mehvahdjukaar.hauntedharvest.blocks;

import com.google.common.base.Preconditions;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.mehvahdjukaar.hauntedharvest.reg.ModRegistry;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.grower.AzaleaTreeGrower;
import net.minecraft.world.level.levelgen.feature.trunkplacers.TrunkPlacer;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;

public class PumpkinType {

    private static final Map<String, PumpkinType> TYPES = new HashMap<>();
    private static final Map<Item, PumpkinType> TORCH_MAP = new Object2ObjectOpenHashMap<>();

    public static final PumpkinType NORMAL = register(new PumpkinType("carved_pumpkin",
            () -> null, ModRegistry.CARVED_PUMPKIN, ()-> Blocks.CARVED_PUMPKIN));
    public static final PumpkinType JACK = register(new PumpkinType("jack_o_lantern",
            () -> Items.TORCH, ModRegistry.JACK_O_LANTERN, ()-> Blocks.JACK_O_LANTERN));

    private final String name;
    private final Supplier<? extends Item> torch;
    private final Supplier<? extends ModCarvedPumpkinBlock> pumpkin;
    private final Supplier<? extends Block> vanillaPumpkin;

    public PumpkinType(String name, Supplier<? extends Item> torch,
                       Supplier<? extends ModCarvedPumpkinBlock> pumpkin,
                       Supplier<? extends Block> vanillaPumpkin) {
        this.name = name;
        this.torch = torch;
        this.pumpkin = pumpkin;
        this.vanillaPumpkin = vanillaPumpkin;
    }

    public Item getTorch() {
        return torch.get();
    }

    public Block getPumpkin() {
        return pumpkin.get();
    }

    public Block getVanillaPumpkin(){
        return vanillaPumpkin.get();
    }

    public String getName() {
        return name;
    }

    public boolean isJackOLantern() {
        return this != NORMAL;
    }


    public static PumpkinType byName(String type) {
        return TYPES.getOrDefault(type, NORMAL);
    }

    @Nullable
    public static PumpkinType getFromTorch(Item torch) {
        return TORCH_MAP.get(torch);
    }

    //safe to call when blocks aren't registered or after
    public static PumpkinType register(PumpkinType pumpkinType) {
        Preconditions.checkArgument(TORCH_MAP.isEmpty(),
                "Pumpkin type must be registered in mod init as it will affect registered blocks");
        TYPES.put(pumpkinType.name, pumpkinType);
        return pumpkinType;
    }

    public static Collection<PumpkinType> getTypes() {
        return TYPES.values();
    }

    public static void setup(){
        for(var pumpkinType : TYPES.values()) {
            TORCH_MAP.put(pumpkinType.getTorch(), pumpkinType);
        }
    }

}
