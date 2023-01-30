package net.mehvahdjukaar.hauntedharvest;


import com.google.common.primitives.Longs;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.mehvahdjukaar.supplementaries.Supplementaries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.RandomSource;
import net.minecraft.util.profiling.ProfilerFiller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class CarvingsManager extends SimpleJsonResourceReloadListener {

    private static final List<long[]> FACES = new ArrayList<>();
    private static final List<long[]> FANTASY = new ArrayList<>();

    protected static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();

    public static final CarvingsManager RELOAD_INSTANCE = new CarvingsManager();

    public static long[] getRandomCarving(RandomSource randomSource, boolean onlyFaces) {
        long[] l;
        if (onlyFaces) l = FACES.get(randomSource.nextInt(FACES.size()));
        else {
            int i = randomSource.nextInt(FACES.size() + FANTASY.size());
            if (i > FACES.size()) {
                l = FANTASY.get(i - FACES.size());
            } else l = FACES.get(i);
        }
        return l;
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

    private record CustomCarving(String name, String author, boolean isFace, List<Long> pixels) {

        private static final Codec<CustomCarving> CODEC = RecordCodecBuilder.create(i -> i.group(
                Codec.STRING.optionalFieldOf("name", "").forGetter(CustomCarving::name),
                Codec.STRING.optionalFieldOf("author", "").forGetter(CustomCarving::name),
                Codec.BOOL.optionalFieldOf("is_face", false).forGetter(CustomCarving::isFace),
                Codec.LONG.listOf().fieldOf("pixels").forGetter(CustomCarving::pixels)
        ).apply(i, CustomCarving::new));
    }

}

