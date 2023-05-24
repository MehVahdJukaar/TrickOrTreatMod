package net.mehvahdjukaar.hauntedharvest.client;


import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.mehvahdjukaar.moonlight.api.client.model.CustomBakedModel;
import net.mehvahdjukaar.moonlight.api.client.model.CustomGeometry;
import net.mehvahdjukaar.moonlight.api.client.model.CustomModelLoader;
import net.mehvahdjukaar.moonlight.api.platform.ClientHelper;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.resources.ResourceLocation;

import java.util.function.Function;

public class CarvedPumpkinBlockLoader implements CustomModelLoader {

    @Override
    public Geometry deserialize(JsonObject json, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        BlockModel model = ClientHelper.parseBlockModel(json.get("model"));
        return new Geometry(model);
    }

    private record Geometry(BlockModel model) implements CustomGeometry {

        @Override
        public CustomBakedModel bake(ModelBaker bakery, Function<Material, TextureAtlasSprite> spriteGetter, ModelState modelTransform, ResourceLocation modelLocation) {
            BakedModel bakedOverlay = this.model.bake(bakery, model, spriteGetter, modelTransform, modelLocation, true);
            return new CarvedPumpkinBakedModel(model, bakedOverlay, spriteGetter, modelTransform);
        }

    }

}
