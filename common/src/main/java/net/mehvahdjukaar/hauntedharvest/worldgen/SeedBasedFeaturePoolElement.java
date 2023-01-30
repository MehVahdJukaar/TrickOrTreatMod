package net.mehvahdjukaar.hauntedharvest.worldgen;//

import com.google.common.collect.Lists;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.mehvahdjukaar.hauntedharvest.reg.ModRegistry;
import net.minecraft.core.*;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.RandomSource;
import net.minecraft.util.random.SimpleWeightedRandomList;
import net.minecraft.util.random.Weight;
import net.minecraft.util.random.WeightedEntry;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.JigsawBlock;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.JigsawBlockEntity.JointType;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.pools.StructurePoolElement;
import net.minecraft.world.level.levelgen.structure.pools.StructurePoolElementType;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool.Projection;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate.StructureBlockInfo;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;

import java.util.List;

public class SeedBasedFeaturePoolElement extends StructurePoolElement {
    public static final Codec<SeedBasedFeaturePoolElement> CODEC = RecordCodecBuilder.create(i -> i.group(
                    SimpleWeightedRandomList.wrappedCodec(PlacedFeature.CODEC).fieldOf("features").forGetter(e -> e.features),
                    projectionCodec())
            .apply(i, SeedBasedFeaturePoolElement::new)
    );
    private final SimpleWeightedRandomList<Holder<PlacedFeature>> features;
    private final CompoundTag defaultJigsawNBT;

    protected SeedBasedFeaturePoolElement(SimpleWeightedRandomList<Holder<PlacedFeature>> features, Projection projection) {
        super(projection);
        this.defaultJigsawNBT = this.fillDefaultJigsawNBT();
        features = this.removeDisabledHack(features);
        this.features = features;
    }

    private SimpleWeightedRandomList<Holder<PlacedFeature>> removeDisabledHack(SimpleWeightedRandomList<Holder<PlacedFeature>> original) {
        var newList = new SimpleWeightedRandomList.Builder<Holder<PlacedFeature>>();
        for (var v : original.unwrap()) {
            if (v.getData().value().feature().value().config() instanceof FarmFieldFeature.Config c) {
                if (!c.crop().isEnabled()) continue;
            }
            newList.add(v.getData(), v.getWeight().asInt());
        }
        return newList.build();
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
        return (this.features.getRandom(RandomSource.create(centerPos.asLong())).get().getData().value())
                .place(level, generator, random, blockPos);
    }

    public StructurePoolElementType<?> getType() {
        return ModRegistry.RANDOM_FEATURE_POOL.get();
    }

    public String toString() {
        return "Features[" + this.features + "]";
    }
}
