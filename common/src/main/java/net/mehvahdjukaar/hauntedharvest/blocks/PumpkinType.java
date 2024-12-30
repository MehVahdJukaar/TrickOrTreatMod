package net.mehvahdjukaar.hauntedharvest.blocks;

import com.google.common.base.Preconditions;
import com.mojang.serialization.Codec;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.mehvahdjukaar.hauntedharvest.reg.ModRegistry;
import net.mehvahdjukaar.moonlight.api.misc.MapRegistry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Map;
import java.util.function.Supplier;

public class PumpkinType {

    private static final MapRegistry<PumpkinType> TYPES = new MapRegistry<>("pumpkin_types");
    private static final Map<Item, PumpkinType> TORCH_MAP = new Object2ObjectOpenHashMap<>();

    public static final Codec<PumpkinType> CODEC = TYPES;
    public static final StreamCodec<FriendlyByteBuf, PumpkinType> STREAM_CODEC = TYPES.getStreamCodec();

    public static final PumpkinType NORMAL = register(new PumpkinType(
            ResourceLocation.withDefaultNamespace("carved_pumpkin"),
            () -> null, ModRegistry.CARVED_PUMPKIN, () -> Blocks.CARVED_PUMPKIN));
    public static final PumpkinType JACK = register(new PumpkinType(
            ResourceLocation.withDefaultNamespace("jack_o_lantern"),
            () -> Items.TORCH, ModRegistry.JACK_O_LANTERN, () -> Blocks.JACK_O_LANTERN));

    private final ResourceLocation name;
    private final Supplier<? extends Item> torch;
    private final Supplier<? extends ModCarvedPumpkinBlock> pumpkin;
    private final Supplier<? extends Block> vanillaPumpkin;

    public PumpkinType(ResourceLocation name, Supplier<? extends Item> torch,
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

    public Block getVanillaPumpkin() {
        return vanillaPumpkin.get();
    }

    public ResourceLocation getName() {
        return name;
    }

    public boolean isJackOLantern() {
        return this != NORMAL;
    }


    public static PumpkinType byName(ResourceLocation type) {
        return TYPES.getValueOrDefault(type, NORMAL);
    }

    @Nullable
    public static PumpkinType getFromTorch(Item torch) {
        return TORCH_MAP.get(torch);
    }

    //safe to call when blocks aren't registered or after
    public static PumpkinType register(PumpkinType pumpkinType) {
        Preconditions.checkArgument(TORCH_MAP.isEmpty(),
                "Pumpkin type must be registered in mod init as it will affect registered blocks");
        TYPES.register(pumpkinType.name, pumpkinType);
        return pumpkinType;
    }

    public static Collection<PumpkinType> getTypes() {
        return TYPES.getValues();
    }

    public static void setup() {
        for (var pumpkinType : TYPES.getValues()) {
            TORCH_MAP.put(pumpkinType.getTorch(), pumpkinType);
        }
    }

}
