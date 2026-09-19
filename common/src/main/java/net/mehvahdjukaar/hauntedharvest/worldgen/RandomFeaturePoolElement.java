package net.mehvahdjukaar.hauntedharvest.worldgen;//

import com.google.common.base.Suppliers;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.mehvahdjukaar.hauntedharvest.reg.ModRegistry;
import net.minecraft.core.*;
import net.minecraft.data.worldgen.Pools;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.Identifier;
import net.minecraft.util.RandomSource;
import net.minecraft.util.random.WeightedList;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.JigsawBlock;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.JigsawBlockEntity;
import net.minecraft.world.level.block.entity.JigsawBlockEntity.JointType;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.pools.StructurePoolElement;
import net.minecraft.world.level.levelgen.structure.pools.StructurePoolElementType;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool.Projection;
import net.minecraft.world.level.levelgen.structure.templatesystem.LiquidSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate.JigsawBlockInfo;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate.StructureBlockInfo;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;

import java.util.List;
import java.util.function.Supplier;

public class RandomFeaturePoolElement extends StructurePoolElement {
    public static final MapCodec<RandomFeaturePoolElement> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
                    WeightedList.codec(PlacedFeature.CODEC).fieldOf("features").forGetter(e -> e.features),
                    projectionCodec())
            .apply(i, RandomFeaturePoolElement::new)
    );
    private final WeightedList<Holder<PlacedFeature>> features;
    private final Supplier<WeightedList<Holder<PlacedFeature>>> enabledFeatures;
    private final CompoundTag defaultJigsawNBT;

    protected RandomFeaturePoolElement(WeightedList<Holder<PlacedFeature>> features, Projection projection) {
        super(projection);
        this.defaultJigsawNBT = this.fillDefaultJigsawNBT();
        this.features = features;
        this.enabledFeatures = Suppliers.memoize(() -> removeDisabledHack(features));
    }

    private static WeightedList<Holder<PlacedFeature>> removeDisabledHack(WeightedList<Holder<PlacedFeature>> original) {
        var newList = WeightedList.<Holder<PlacedFeature>>builder();
        for (var v : original.unwrap()) {
            //hack. Use data conditions instead
            if (v.value().value().feature().value().config() instanceof FarmFieldFeature.Config c) {
                if (!c.crop().isEnabled()) continue;
            }
            newList.add(v.value(), v.weight());
        }
        return newList.build();
    }

    private CompoundTag fillDefaultJigsawNBT() {
        CompoundTag tag = new CompoundTag();
        tag.store("name", Identifier.CODEC, Identifier.withDefaultNamespace("bottom"));
        tag.putString("final_state", "minecraft:air");
        tag.store("pool", JigsawBlockEntity.POOL_CODEC, Pools.EMPTY);
        tag.store("target", Identifier.CODEC, JigsawBlockEntity.EMPTY_ID);
        tag.store("joint", JointType.CODEC, JointType.ROLLABLE);
        return tag;
    }

    @Override
    public Vec3i getSize(StructureTemplateManager structureTemplateManager, Rotation rotation) {
        return Vec3i.ZERO;
    }

    @Override
    public List<JigsawBlockInfo> getShuffledJigsawBlocks(
            StructureTemplateManager structureTemplateManager, BlockPos blockPos, Rotation rotation, RandomSource randomSource
    ) {
        return List.of(JigsawBlockInfo.of(new StructureBlockInfo(
                blockPos,
                Blocks.JIGSAW.defaultBlockState().setValue(JigsawBlock.ORIENTATION, FrontAndTop.fromFrontAndTop(Direction.DOWN, Direction.SOUTH)),
                this.defaultJigsawNBT)));
    }

    @Override
    public BoundingBox getBoundingBox(StructureTemplateManager structureTemplateManager, BlockPos blockPos, Rotation rotation) {
        Vec3i vec3i = this.getSize(structureTemplateManager, rotation);
        return new BoundingBox(
                blockPos.getX(), blockPos.getY(), blockPos.getZ(), blockPos.getX() + vec3i.getX(), blockPos.getY() + vec3i.getY(), blockPos.getZ() + vec3i.getZ()
        );
    }

    @Override
    public boolean place(StructureTemplateManager structureTemplateManager, WorldGenLevel level, StructureManager structureManager,
                         ChunkGenerator generator, BlockPos blockPos, BlockPos centerPos, Rotation rotation,
                         BoundingBox boundingBox, RandomSource random, LiquidSettings liquidSettings, boolean bl) {
        return this.enabledFeatures.get().getRandomOrThrow(RandomSource.create(centerPos.asLong())).value()
                .place(level, generator, random, blockPos);
    }

    @Override
    public StructurePoolElementType<?> getType() {
        return ModRegistry.RANDOM_FEATURE_POOL.get();
    }

    @Override
    public String toString() {
        return "Features[" + this.features + "]";
    }
}
