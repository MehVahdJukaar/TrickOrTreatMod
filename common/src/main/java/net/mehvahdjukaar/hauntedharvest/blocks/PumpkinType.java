package net.mehvahdjukaar.hauntedharvest.blocks;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.mehvahdjukaar.hauntedharvest.reg.ModRegistry;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;

public class PumpkinType {

    private static final Map<String, PumpkinType> TYPES = new HashMap<>();
    private static final Map<Item, Block> TORCH_MAP = new Object2ObjectOpenHashMap<>();

    public static final PumpkinType NORMAL = register(new PumpkinType("carved_pumpkin", () -> null, ModRegistry.CARVED_PUMPKIN));
    public static final PumpkinType JACK = register(new PumpkinType("jack_o_lantern", () -> Items.TORCH, ModRegistry.JACK_O_LANTERN));
    public static final PumpkinType SOUL = register(new PumpkinType("soul_jack_o_lantern", () -> Items.SOUL_TORCH, ModRegistry.SOUL_JACK_O_LANTERN));
    public static final PumpkinType REDSTONE = register(new PumpkinType("redstone_jack_o_lantern", () -> Items.REDSTONE_TORCH, ModRegistry.REDSTONE_JACK_O_LANTERN));

    private final String name;
    private final Supplier<? extends Item> torch;
    private final Supplier<? extends ModCarvedPumpkinBlock> pumpkin;

    public PumpkinType(String name, Supplier<? extends Item> torch, Supplier<? extends ModCarvedPumpkinBlock> pumpkin) {
        this.name = name;
        this.torch = torch;
        this.pumpkin = Objects.requireNonNull(pumpkin, "pumpkin cannot be null");
    }

    public static PumpkinType byName(String type) {
        return TYPES.getOrDefault(type, NORMAL);
    }

    @Nullable
    public static Block getFromTorch(Item torch) {
        return TORCH_MAP.get(torch);
    }

    public Item getTorch() {
        return torch.get();
    }

    public Block getPumpkin() {
        return pumpkin.get();
    }

    public String getName() {
        return name;
    }

    public boolean isGlowing() {
        return this != NORMAL;
    }

    //call during mod init
    public static PumpkinType register(PumpkinType pumpkinType) {
        TYPES.put(pumpkinType.name, pumpkinType);
        return pumpkinType;
    }

    //call during mod setup
    public static void setup() {
        for (var t : TYPES.values()) {
            TORCH_MAP.put(t.getTorch(), t.getPumpkin());
        }
    }

    public static Collection<PumpkinType> getTypes() {
        return TYPES.values();
    }

}
