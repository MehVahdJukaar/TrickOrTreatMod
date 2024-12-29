package net.mehvahdjukaar.hauntedharvest.items.components;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.ChatFormatting;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipProvider;

import java.util.Arrays;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.stream.LongStream;

public class PumpkinCarvingData implements TooltipComponent, TooltipProvider {

    private static final Component WAXED_TOOLTIP = Component.translatable("message.supplementaries.blackboard").withStyle(ChatFormatting.GRAY);

    public static final Codec<PumpkinCarvingData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.LONG_STREAM.fieldOf("values")
                    .xmap(LongStream::toArray, Arrays::stream)
                    .forGetter(v -> v.values),
            Codec.BOOL.fieldOf("waxed").forGetter(v -> v.waxed)
    ).apply(instance, PumpkinCarvingData::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, long[]> LONG_ARRAY = new StreamCodec<>() {
        @Override
        public long[] decode(RegistryFriendlyByteBuf buffer) {
            int size = buffer.readByte();
            long[] values = new long[size];
            for (int i = 0; i < size; i++) {
                values[i] = buffer.readLong();
            }
            return values;
        }

        @Override
        public void encode(RegistryFriendlyByteBuf buffer, long[] value) {
            buffer.writeByte(value.length);
            for (long l : value) {
                buffer.writeLong(l);
            }
        }
    };

    public static final StreamCodec<RegistryFriendlyByteBuf, PumpkinCarvingData> STREAM_CODEC = StreamCodec.composite(
            LONG_ARRAY, data -> data.values,
            ByteBufCodecs.BOOL, data -> data.waxed,
            PumpkinCarvingData::new
    );

    public static final PumpkinCarvingData DEFAULT = new PumpkinCarvingData(new long[4], false);

    private final long[] values;
    private final boolean waxed;

    PumpkinCarvingData(long[] packed, boolean waxed) {
        this.values = packed;
        this.waxed = waxed;
    }

    public static PumpkinCarvingData pack(boolean[][] pixels, boolean waxed) {
        return new PumpkinCarvingData(packPixels(pixels), waxed);
    }

    public static PumpkinCarvingData of(long[] packPixels, boolean waxed) {
        return new PumpkinCarvingData(packPixels, waxed);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PumpkinCarvingData that)) return false;
        return waxed == that.waxed && Objects.deepEquals(values, that.values);
    }

    @Override
    public int hashCode() {
        return Objects.hash(Arrays.hashCode(values), waxed);
    }

    @Override
    public void addToTooltip(Item.TooltipContext context, Consumer<Component> tooltipAdder, TooltipFlag tooltipFlag) {
        if (waxed) {
            tooltipAdder.accept(WAXED_TOOLTIP);
        }
    }

    public boolean[][] unpackPixels() {
        return unpackPixels(values);
    }

    public boolean waxed() {
        return waxed;
    }

    public static long[] packPixels(boolean[][] pixels) {
        long[] packed = new long[4];  // We need 4 long values, each holding 64 bits

        for (int i = 0; i < 16; i++) {
            for (int j = 0; j < 16; j++) {
                int index = i * 16 + j;  // Calculate the overall index in the 256 bits
                int longIndex = index / 64;  // Determine which long value this bit will go into
                int bitIndex = index % 64;   // Determine which bit in the long value to set

                // Set the bit in the corresponding long value if the pixel is true
                if (pixels[i][j]) {
                    packed[longIndex] |= (1L << (63 - bitIndex));  // Set the bit at the correct position
                }
            }
        }
        return packed;
    }

    public static boolean[][] unpackPixels(long[] packed) {
        boolean[][] pixels = new boolean[16][16];

        for (int i = 0; i < 16; i++) {
            for (int j = 0; j < 16; j++) {
                int index = i * 16 + j;  // Calculate the overall index in the 256 bits
                int longIndex = index / 64;  // Determine which long value this bit comes from
                int bitIndex = index % 64;   // Determine which bit in the long value to check

                // Check if the bit is set in the corresponding long value
                pixels[i][j] = (packed[longIndex] & (1L << (63 - bitIndex))) != 0;
            }
        }
        return pixels;
    }

    public static long[] unpackPixelsFromStringWhiteOnly(String packed) {
        long[] unpacked = new long[16];
        var chars = packed.toCharArray();
        int j = 0;
        for (int i = 0; i + 3 < chars.length; i += 4) {
            long l = 0;
            char c = chars[i];
            for (int k = 0; k < 4; k++) {
                l = l | (((c >> k) & 1) << 4 * k);
            }
            char c2 = chars[i + 1];
            for (int k = 0; k < 4; k++) {
                l = l | ((long) ((c2 >> k) & 1) << (16 + (4 * k)));
            }
            char c3 = chars[i + 2];
            for (int k = 0; k < 4; k++) {
                l = l | ((long) ((c3 >> k) & 1) << (32 + (4 * k)));
            }
            char c4 = chars[i + 3];
            for (int k = 0; k < 4; k++) {
                l = l | ((long) ((c4 >> k) & 1) << (48 + (4 * k)));
            }
            unpacked[j] = l;
            j++;
        }
        return unpacked;
    }

    public static String packPixelsToStringWhiteOnly(long[] packed) {
        StringBuilder builder = new StringBuilder();
        for (var l : packed) {
            char c = 0;
            for (int k = 0; k < 4; k++) {
                byte h = (byte) ((l >> 4 * k) & 1);
                c = (char) (c | (h << k));
            }
            char c1 = 0;
            for (int k = 0; k < 4; k++) {
                byte h = (byte) ((l >> (16 + (4 * k))) & 1);
                c1 = (char) (c1 | (h << k));
            }
            char c2 = 0;
            for (int k = 0; k < 4; k++) {
                byte h = (byte) ((l >> (32 + (4 * k))) & 1);
                c2 = (char) (c2 | (h << k));
            }
            char c3 = 0;
            for (int k = 0; k < 4; k++) {
                byte h = (byte) ((l >> (48 + (4 * k))) & 1);
                c3 = (char) (c3 | (h << k));
            }
            builder.append(c).append(c1).append(c2).append(c3);
        }
        return builder.toString();
    }

    public boolean isEmpty() {
        return false;
    }
}
