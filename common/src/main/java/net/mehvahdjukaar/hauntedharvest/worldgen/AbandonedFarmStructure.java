package net.mehvahdjukaar.hauntedharvest.worldgen;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.mehvahdjukaar.hauntedharvest.reg.ModRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.RandomState;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.minecraft.world.level.levelgen.structure.pools.JigsawPlacement;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;

import java.util.Optional;
import java.util.function.Function;

public final class AbandonedFarmStructure extends Structure {

    public static final Codec<AbandonedFarmStructure> CODEC = RecordCodecBuilder.<AbandonedFarmStructure>mapCodec((instance) -> instance.group(
            settingsCodec(instance),
            StructureTemplatePool.CODEC.fieldOf("start_pool").forGetter((j) -> j.startPool),
            ResourceLocation.CODEC.optionalFieldOf("start_jigsaw_name").forGetter((j) -> j.startJigsawName),
            Codec.intRange(0, 7).fieldOf("size").forGetter((j) -> j.maxDepth),
            Heightmap.Types.CODEC.optionalFieldOf("project_start_to_heightmap").forGetter(j -> j.projectStartToHeightmap),
            Codec.INT.fieldOf("min_y").forGetter(structure -> structure.minY),
            Codec.INT.fieldOf("max_y").forGetter(structure -> structure.maxY)
    ).apply(instance, AbandonedFarmStructure::new)).flatXmap(verifyRange(), verifyRange()).codec();
    private final Holder<StructureTemplatePool> startPool;
    private final Optional<ResourceLocation> startJigsawName;
    private final int maxDepth;
    private final Optional<Heightmap.Types> projectStartToHeightmap;
    private final int maxY;
    private final int minY;

    private static Function<AbandonedFarmStructure, DataResult<AbandonedFarmStructure>> verifyRange() {
        return (jigsawStructure) -> jigsawStructure.maxY < jigsawStructure.minY ? DataResult.error(() -> "MaxY cannot be < MinY") : DataResult.success(jigsawStructure);
    }

    public AbandonedFarmStructure(Structure.StructureSettings config, Holder<StructureTemplatePool> startPool,
                                  Optional<ResourceLocation> startJigsawName, int depth,
                                  Optional<Heightmap.Types> projectStartToHeightmap, int minY, int maxY) {
        super(config);
        this.startPool = startPool;
        this.startJigsawName = startJigsawName;
        this.maxDepth = depth;
        this.minY = minY;
        this.maxY = maxY;
        this.projectStartToHeightmap = projectStartToHeightmap;
    }

    public Optional<Structure.GenerationStub> findGenerationPoint(Structure.GenerationContext context) {
        ChunkPos chunkPos = context.chunkPos();
        BlockPos blockPos = new BlockPos(chunkPos.getMinBlockX(), 0, chunkPos.getMinBlockZ());
        LevelHeightAccessor heightLimitView = context.heightAccessor();
        RandomState randomState = context.randomState();
        ChunkGenerator generator = context.chunkGenerator();

        // Grab height of land. Will stop at first non-air block.
        int y = generator.getFirstOccupiedHeight(blockPos.getX(), blockPos.getZ(), Heightmap.Types.WORLD_SURFACE_WG, heightLimitView, randomState);
        if (y > maxY || y < minY) return Optional.empty();
        return JigsawPlacement.addPieces(context, this.startPool, this.startJigsawName, this.maxDepth,
                blockPos, false, this.projectStartToHeightmap, 20);
    }

    public StructureType<?> type() {
        return ModRegistry.FARM.get();
    }

    public static class Type implements StructureType<AbandonedFarmStructure> {
        @Override
        public Codec<AbandonedFarmStructure> codec() {
            return CODEC;
        }
    }
}