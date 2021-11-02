package net.mehvahdjukaar.hauntedharvest.mixins;

import com.mojang.serialization.Codec;
import net.mehvahdjukaar.hauntedharvest.Halloween;
import net.minecraft.util.random.WeightedRandomList;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraft.world.level.levelgen.feature.JigsawFeature;
import net.minecraft.world.level.levelgen.feature.VillageFeature;
import net.minecraft.world.level.levelgen.feature.configurations.JigsawConfiguration;
import org.spongepowered.asm.mixin.Mixin;

import java.util.List;

@Mixin(VillageFeature.class)
public abstract class VillageFeatureMixin extends JigsawFeature {

    public VillageFeatureMixin(Codec<JigsawConfiguration> pCodec, int pStartY, boolean pDoExpansionHack, boolean pProjectStartToHeightmap) {
        super(pCodec, pStartY, pDoExpansionHack, pProjectStartToHeightmap);
    }


    /**
     * || ONLY WORKS IN FORGE 34.1.12+ ||
     * <p>
     * This method allows us to have mobs that spawn naturally over time in our structure.
     * No other mobs will spawn in the structure of the same entity classification.
     * The reason you want to match the classifications is so that your structure's mob
     * will contribute to that classification's cap. Otherwise, it may cause a runaway
     * spawning of the mob that will never stop.
     * <p>
     * NOTE: getDefaultSpawnList is for monsters only and getDefaultCreatureSpawnList is
     * for creatures only. If you want to add entities of another classification,
     * use the StructureSpawnListGatherEvent to add water_creatures, water_ambient,
     * ambient, or misc mobs. Use that event to add/remove mobs from structures
     * that are not your own.
     * <p>
     * NOTE 2: getSpecialEnemies and getSpecialAnimals are the vanilla methods that Forge does
     * not hook up. Do not use those methods or else the mobs won't spawn. You would
     * have to manually implement spawning for them. Stick with Forge's Default form
     * as it is easier to use that.
     */


    private static final WeightedRandomList<MobSpawnSettings.SpawnerData> VILLAGE_ENEMIES =
            WeightedRandomList.create(
                    new MobSpawnSettings.SpawnerData(EntityType.WITCH, 18, 1, 1),
                    new MobSpawnSettings.SpawnerData(EntityType.ZOMBIE, 4, 1, 5),
                    new MobSpawnSettings.SpawnerData(EntityType.CREEPER, 8, 2, 5),
                    new MobSpawnSettings.SpawnerData(EntityType.SKELETON, 8, 2, 5),
                    new MobSpawnSettings.SpawnerData(EntityType.SKELETON, 8, 2, 5),
                    new MobSpawnSettings.SpawnerData(EntityType.SPIDER, 8, 2, 5));

    @Override
    public List<MobSpawnSettings.SpawnerData> getDefaultSpawnList() {
        if(Halloween.IS_HALLOWEEN_TIME) return VILLAGE_ENEMIES.unwrap();
        return super.getDefaultSpawnList();
    }

}