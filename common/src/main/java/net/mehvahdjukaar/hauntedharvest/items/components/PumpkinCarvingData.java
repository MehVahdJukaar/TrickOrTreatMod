package net.mehvahdjukaar.hauntedharvest.items.components;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.mehvahdjukaar.hauntedharvest.blocks.PumpkinType;
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

    private static final Component WAXED_TOOLTIP = Component.translatable("message.hauntedharvest.waxed").withStyle(ChatFormatting.GRAY);
    private static final int SIZE = 16;

    public static final Codec<boolean[][]> PIXEL_CODEC = Codec.LONG_STREAM.xmap(LongStream::toArray, Arrays::stream)
            .xmap(PumpkinCarvingData::unpack, PumpkinCarvingData::pack);

    public static final Codec<PumpkinCarvingData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            PIXEL_CODEC.fieldOf("values").forGetter(v -> v.pixels),
            PumpkinType.CODEC.fieldOf("type").forGetter(v -> v.type),
            Codec.BOOL.fieldOf("waxed").forGetter(v -> v.waxed)
    ).apply(instance, PumpkinCarvingData::new));


    private static final StreamCodec<RegistryFriendlyByteBuf, long[]> LONG_ARRAY = new StreamCodec<>() {
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

    private static final StreamCodec<RegistryFriendlyByteBuf, boolean[][]> PIXELS_CODEC = LONG_ARRAY
            .map(PumpkinCarvingData::unpack, PumpkinCarvingData::pack);

    public static final StreamCodec<RegistryFriendlyByteBuf, PumpkinCarvingData> STREAM_CODEC = StreamCodec.composite(
            PIXELS_CODEC, data -> data.pixels,
            PumpkinType.STREAM_CODEC, data -> data.type,
            ByteBufCodecs.BOOL, data -> data.waxed,
            PumpkinCarvingData::new
    );

    private final boolean[][] pixels;
    private final boolean waxed;
    private final PumpkinType type;

    private final int cachedHashCode;

    PumpkinCarvingData(boolean[][] pixels, PumpkinType type, boolean waxed) {
        this.pixels = pixels;
        this.type = type;
        this.waxed = waxed;
        this.cachedHashCode = Objects.hash(Arrays.deepHashCode(pixels), type, waxed);
    }

    public static PumpkinCarvingData of(boolean[][] pixels, PumpkinType pumpkinType, boolean waxed) {
        return new PumpkinCarvingData(pixels, pumpkinType, waxed);
    }

    public static PumpkinCarvingData empty(PumpkinType pumpkinType) {
        return new PumpkinCarvingData(new boolean[SIZE][SIZE], pumpkinType, false);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PumpkinCarvingData that)) return false;
        return type == that.type && waxed == that.waxed && Objects.deepEquals(pixels, that.pixels);
    }

    @Override
    public int hashCode() {
        return cachedHashCode;
    }

    @Override
    public void addToTooltip(Item.TooltipContext context, Consumer<Component> tooltipAdder, TooltipFlag tooltipFlag) {
        if (waxed) {
            tooltipAdder.accept(WAXED_TOOLTIP);
        }
    }

    public boolean isWaxed() {
        return waxed;
    }

    public PumpkinType getType() {
        return type;
    }

    public boolean hasSamePixels(boolean[][] pixels) {
        return Arrays.deepEquals(this.pixels, pixels);
    }

    public boolean isEmpty() {
        for (boolean[] row : pixels) {
            for (boolean value : row) {
                if (value) {
                    return false;
                }
            }
        }
        return true;
    }

    public boolean getPixel(int xx, int yy) {
        return pixels[xx][yy];
    }

    public boolean[][] getPixelsUnsafe() {
        return pixels;
    }

    public PumpkinCarvingData makeCleared() {
        return new PumpkinCarvingData(new boolean[SIZE][SIZE], this.type, this.waxed);
    }

    public PumpkinCarvingData withPixel(int x, int y, boolean b) {
        boolean[][] newPixels = new boolean[SIZE][SIZE];
        for (int i = 0; i < SIZE; i++) {
            System.arraycopy(pixels[i], 0, newPixels[i], 0, SIZE);
        }
        newPixels[x][y] = b;
        return new PumpkinCarvingData(newPixels, this.type, this.waxed);
    }

    public PumpkinCarvingData withWaxed(boolean b) {
        return new PumpkinCarvingData(pixels, this.type, b);
    }

    public PumpkinCarvingData withPixels(boolean[][] pixels) {
        return new PumpkinCarvingData(pixels, this.type, this.waxed);
    }

    public static long[] pack(boolean[][] pixels) {
        long[] packed = new long[4];  // We need 4 long values, each holding 64 bits

        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                int index = i * SIZE + j;  // Calculate the overall index in the 256 bits
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

    public static boolean[][] unpack(long[] packed) {
        boolean[][] pixels = new boolean[SIZE][SIZE];

        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                int index = i * SIZE + j;  // Calculate the overall index in the 256 bits
                int longIndex = index / 64;  // Determine which long value this bit comes from
                int bitIndex = index % 64;   // Determine which bit in the long value to check

                // Check if the bit is set in the corresponding long value
                pixels[i][j] = (packed[longIndex] & (1L << (63 - bitIndex))) != 0;
            }
        }
        return pixels;
    }

/*
    public static boolean[][] unpackPixels(long[] packed) {
        boolean[][] bytes = new boolean[SIZE][SIZE];
        int k = 0;
        for (long l : packed) {
            for (int j = 0; j < 4; j++) {
                for (int i = 0; i < SIZE; i++) {
                    bytes[k][i] = toBoolean((short) ((l >> (i + j * SIZE)) & 1));
                }
                k++;
            }
        }
        return bytes;
    }*/



    public static long[] unpackPixelsFromStringWhiteOnly(String packed) {
        long[] unpacked = new long[SIZE];
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
                l = l | ((long) ((c2 >> k) & 1) << (SIZE + (4 * k)));
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
                byte h = (byte) ((l >> (SIZE + (4 * k))) & 1);
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

    public PumpkinCarvingData withType(PumpkinType pumpkinType) {
        return new PumpkinCarvingData(this.pixels, pumpkinType, this.waxed);
    }
}
