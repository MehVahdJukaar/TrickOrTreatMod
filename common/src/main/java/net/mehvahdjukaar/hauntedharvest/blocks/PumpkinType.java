package net.mehvahdjukaar.hauntedharvest.blocks;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.mehvahdjukaar.hauntedharvest.HauntedHarvest;
import net.mehvahdjukaar.hauntedharvest.reg.ModRegistry;
import net.mehvahdjukaar.moonlight.api.misc.RegSupplier;
import net.mehvahdjukaar.moonlight.api.platform.RegHelper;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.function.Supplier;

import static net.mehvahdjukaar.hauntedharvest.HauntedHarvest.res;

public class PumpkinType {

    public static final Registry<PumpkinType> REGISTRY = RegHelper.registerRegistry(
            res("pumpkin_type"), true);

    private static final Map<Item, PumpkinType> TORCH_MAP = new Object2ObjectOpenHashMap<>();

    public static final RegSupplier<PumpkinType> NORMAL = register(
            HauntedHarvest.res("carved_pumpkin"),
            () -> null, ModRegistry.CARVED_PUMPKIN, () -> Blocks.CARVED_PUMPKIN);

    public static final RegSupplier<PumpkinType> JACK = register(
            HauntedHarvest.res("jack_o_lantern"),
            () -> Items.TORCH, ModRegistry.JACK_O_LANTERN, () -> Blocks.JACK_O_LANTERN);

    private final String textureKey;
    private final Supplier<? extends Item> torch;
    private final Supplier<? extends ModCarvedPumpkinBlock> pumpkin;
    private final Supplier<? extends Block> vanillaPumpkin;

    public PumpkinType(String textureKey, Supplier<? extends Item> torch,
                       Supplier<? extends ModCarvedPumpkinBlock> pumpkin,
                       Supplier<? extends Block> vanillaPumpkin) {
        this.torch = torch;
        this.pumpkin = pumpkin;
        this.vanillaPumpkin = vanillaPumpkin;
        this.textureKey = textureKey;
    }

    public static Holder<PumpkinType> fromPumpkinItem(Item item) {
        if (item instanceof BlockItem bi && bi.getBlock() instanceof ModCarvedPumpkinBlock block) {
            return block.getType(block.defaultBlockState());
        }
        throw new IllegalArgumentException("Item is not a pumpkin");
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

    public String getTextureKey() {
        return textureKey;
    }

    public final boolean isJackOLantern() {
        return this != NORMAL.get();
    }


    @Nullable
    public static PumpkinType getFromTorch(Item torch) {
        return TORCH_MAP.get(torch);
    }


    public static void init() {
    }

    public static void setup() {
        for (var pumpkinType : REGISTRY) {
            TORCH_MAP.put(pumpkinType.getTorch(), pumpkinType);
        }
    }

    public static RegSupplier<PumpkinType> register(ResourceLocation name,
                                                    Supplier<? extends Item> torch,
                                                    Supplier<? extends ModCarvedPumpkinBlock> pumpkin,
                                                    Supplier<? extends Block> vanillaPumpkin) {
        String textureKey = name.getNamespace().equals("hauntedharvest") ? name.getPath() : name.getNamespace() + "/" + name.getPath();
        return RegHelper.register(name,
                () -> new PumpkinType(textureKey, torch, pumpkin, vanillaPumpkin),
                REGISTRY.key());
    }


}
