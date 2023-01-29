package net.mehvahdjukaar.hauntedharvest.worldgen;//

import com.google.common.collect.Lists;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.mehvahdjukaar.hauntedharvest.reg.ModRegistry;
import net.minecraft.core.*;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.RandomSource;
import net.minecraft.util.random.Weight;
import net.minecraft.util.random.WeightedEntry;
import net.minecraft.util.random.WeightedRandomList;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.JigsawBlock;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.JigsawBlockEntity.JointType;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.pools.FeaturePoolElement;
import net.minecraft.world.level.levelgen.structure.pools.StructurePoolElement;
import net.minecraft.world.level.levelgen.structure.pools.StructurePoolElementType;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool.Projection;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate.StructureBlockInfo;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;

import java.util.List;

public class SeedBasedFeaturePoolElement extends StructurePoolElement {
    public static final Codec<SeedBasedFeaturePoolElement> CODEC = RecordCodecBuilder.create(i -> i.group(
                    WeightedRandomList.codec(FeatureEntry.CODEC).fieldOf("features").forGetter(e -> e.features),
                    projectionCodec())
            .apply(i, SeedBasedFeaturePoolElement::new)
    );
    private final WeightedRandomList<FeatureEntry> features;
    private final CompoundTag defaultJigsawNBT;

    protected SeedBasedFeaturePoolElement(WeightedRandomList<FeatureEntry> features, Projection projection) {
        super(projection);
        this.features = features;
        this.defaultJigsawNBT = this.fillDefaultJigsawNBT();
    }

    private CompoundTag fillDefaultJigsawNBT() {
        CompoundTag compoundTag = new CompoundTag();
        compoundTag.putString("name", "minecraft:bottom");
        compoundTag.putString("final_state", "minecraft:air");
        compoundTag.putString("pool", "minecraft:empty");
        compoundTag.putString("target", "minecraft:empty");
        compoundTag.putString("joint", JointType.ROLLABLE.getSerializedName());
        return compoundTag;
    }

    public Vec3i getSize(StructureTemplateManager structureTemplateManager, Rotation rotation) {
        return Vec3i.ZERO;
    }

    @Override
    public List<StructureBlockInfo> getShuffledJigsawBlocks(
            StructureTemplateManager structureTemplateManager, BlockPos blockPos, Rotation rotation, RandomSource randomSource
    ) {
        List<StructureBlockInfo> list = Lists.newArrayList();
        list.add(
                new StructureBlockInfo(
                        blockPos,
                        Blocks.JIGSAW.defaultBlockState().setValue(JigsawBlock.ORIENTATION, FrontAndTop.fromFrontAndTop(Direction.DOWN, Direction.SOUTH)),
                        this.defaultJigsawNBT
                )
        );
        return list;
    }

    @Override
    public BoundingBox getBoundingBox(StructureTemplateManager structureTemplateManager, BlockPos blockPos, Rotation rotation) {
        Vec3i vec3i = this.getSize(structureTemplateManager, rotation);
        return new BoundingBox(
                blockPos.getX(), blockPos.getY(), blockPos.getZ(), blockPos.getX() + vec3i.getX(), blockPos.getY() + vec3i.getY(), blockPos.getZ() + vec3i.getZ()
        );
    }

    @Override
    public boolean place(
            StructureTemplateManager structureTemplateManager,
            WorldGenLevel level,
            StructureManager structureManager,
            ChunkGenerator generator,
            BlockPos blockPos,
            BlockPos centerPos,
            Rotation rotation,
            BoundingBox box,
            RandomSource random,
            boolean bl
    ) {
        return (this.features.getRandom(RandomSource.create(centerPos.asLong())).get().feature.value())
                .place(level, generator, random, blockPos);
    }

    public StructurePoolElementType<?> getType() {
        return ModRegistry.RANDOM_FEATURE_POOL.get();
    }

    public String toString() {
        return "Features[" + this.features + "]";
    }

    private static class FeatureEntry extends WeightedEntry.IntrusiveBase {
        public static final Codec<FeatureEntry> CODEC = RecordCodecBuilder.create(i -> i.group(
                        PlacedFeature.CODEC.fieldOf("feature").forGetter(e -> e.feature),
                        Weight.CODEC.fieldOf("weight").forGetter(IntrusiveBase::getWeight)
                ).apply(i, FeatureEntry::new)
        );

        private final Holder<PlacedFeature> feature;

        public FeatureEntry(Holder<PlacedFeature> feature, Weight i) {
            super(i);
            this.feature = feature;
        }
    }
}
