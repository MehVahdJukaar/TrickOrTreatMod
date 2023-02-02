package net.mehvahdjukaar.hauntedharvest;


import com.google.common.primitives.Longs;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.mehvahdjukaar.hauntedharvest.blocks.ModCarvedPumpkinBlockTile;
import net.mehvahdjukaar.hauntedharvest.configs.CommonConfigs;
import net.mehvahdjukaar.hauntedharvest.reg.ModRegistry;
import net.mehvahdjukaar.supplementaries.Supplementaries;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.RandomSource;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CarvedPumpkinBlock;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class CarvingsManager extends SimpleJsonResourceReloadListener {

    private static final List<long[]> FACES = new ArrayList<>();
    private static final List<long[]> FANTASY = new ArrayList<>();

    protected static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();

    public static final CarvingsManager RELOAD_INSTANCE = new CarvingsManager();

    public static void placeRandomPumpkin(BlockPos pos, LevelAccessor level, Direction direction,
                                          boolean onlyFaces, float vanillaChance, float lanternChance, int flag) {
        boolean isVanilla = !CommonConfigs.customCarvings() || level.getRandom().nextFloat() < vanillaChance;
        boolean isLantern = level.getRandom().nextFloat() < lanternChance;
        if (isVanilla) {
            level.setBlock(pos, (isLantern ? Blocks.JACK_O_LANTERN : Blocks.CARVED_PUMPKIN)
                    .defaultBlockState().setValue(CarvedPumpkinBlock.FACING, direction), flag);
        } else {
            level.setBlock(pos, (isLantern ? ModRegistry.MOD_JACK_O_LANTERN : ModRegistry.MOD_CARVED_PUMPKIN).get()
                    .defaultBlockState().setValue(CarvedPumpkinBlock.FACING, direction), flag);
            if (level.getBlockEntity(pos) instanceof ModCarvedPumpkinBlockTile tile) {
                tile.acceptPixels(getRandomCarving(level.getRandom(), onlyFaces));
                tile.setChanged();
            }
        }
    }

    public static long[] getRandomCarving(RandomSource randomSource, boolean onlyFaces) {
        long[] l;
        if (FACES.isEmpty()) return new long[]{0, 0, 0, 0};
        if (onlyFaces) l = FACES.get(randomSource.nextInt(FACES.size()));
        else {
            int i = randomSource.nextInt(FACES.size() + FANTASY.size());
            if (i > FACES.size()) {
                l = FANTASY.get(i - FACES.size());
            } else l = FACES.get(i);
        }
        return l;
    }

    public static void debugPlaceAllPumpkins(BlockPos pos, LevelAccessor level){
        var l = new ArrayList<>(FACES);
        l.addAll(FANTASY);
        for(var c : l) {
            level.setBlock(pos, ModRegistry.MOD_CARVED_PUMPKIN.get()
                    .defaultBlockState().setValue(CarvedPumpkinBlock.FACING, Direction.WEST), 3);
            if (level.getBlockEntity(pos) instanceof ModCarvedPumpkinBlockTile tile) {
                tile.acceptPixels(c);
                tile.setChanged();
            }
            pos = pos.relative(Direction.NORTH);
        }
    }

    private CarvingsManager() {
        super(GSON, "pumpkin_carvings");
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> jsons, ResourceManager resourceManager, ProfilerFiller profiler) {
        FACES.clear();
        FANTASY.clear();
        jsons.forEach((key, json) -> {
            var v = CustomCarving.CODEC.parse(JsonOps.INSTANCE, json);
            var data = v.getOrThrow(false, e -> Supplementaries.LOGGER.error("failed to parse pumpkin carving: {}", e));
            if (data.isFace) FACES.add(Longs.toArray(data.pixels));
            else FANTASY.add(Longs.toArray(data.pixels));
        });
    }


    private static JsonObject sortJson(JsonObject jsonObject) {
        try {
            Map<String, JsonElement> joToMap = new TreeMap<>();
            jsonObject.entrySet().forEach(e -> {
                var j = e.getValue();
                if (j instanceof JsonObject jo) j = sortJson(jo);
                joToMap.put(e.getKey(), j);
            });
            JsonObject sortedJSON = new JsonObject();
            joToMap.forEach(sortedJSON::add);
            return sortedJSON;
        } catch (Exception ignored) {
        }
        return jsonObject;
    }

    private record CustomCarving(String author, boolean isFace, List<Long> pixels) {

        private static final Codec<CustomCarving> CODEC = RecordCodecBuilder.create(i -> i.group(
                Codec.STRING.optionalFieldOf("author", "").forGetter(CustomCarving::author),
                Codec.BOOL.optionalFieldOf("is_face", false).forGetter(CustomCarving::isFace),
                Codec.LONG.listOf().fieldOf("pixels").forGetter(CustomCarving::pixels)
        ).apply(i, CustomCarving::new));
    }

}

