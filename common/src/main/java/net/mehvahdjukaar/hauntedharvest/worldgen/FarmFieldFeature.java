package net.mehvahdjukaar.hauntedharvest.worldgen;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.mehvahdjukaar.hauntedharvest.blocks.AbstractCornBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.HoeItem;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.AttachedStemBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.StemBlock;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.RandomPatchFeature;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;

public class FarmFieldFeature extends Feature<FarmFieldFeature.Config> {

    public FarmFieldFeature(Codec<Config> codec) {
        super(codec);
    }

    @Override
    public boolean place(FeaturePlaceContext<Config> context) {
        Config config = context.config();
        RandomSource random = context.random();
        BlockPos blockPos = context.origin();
        WorldGenLevel level = context.level();
        boolean pumpkin = config.pumpkin();
        boolean scarecrow = config.scarecrow();

        if (scarecrow) placeScarecrow(blockPos, level, random);

        int i = 0;
        BlockPos.MutableBlockPos p = new BlockPos.MutableBlockPos();
        int j = config.xzSpread() + 1;
        int k = config.ySpread() + 1;

        for (int l = 0; l < config.tries(); ++l) {
            p.setWithOffset(
                    blockPos,
                    random.nextInt(j) - random.nextInt(j),
                    random.nextInt(k) - random.nextInt(k),
                    random.nextInt(j) - random.nextInt(j)
            );

            if(level.getBlockState(p.below()).is(BlockTags.DIRT)) {
                if (pumpkin ? placePumpkin(p, level, random) : placeCorn(p, level, random)) {
                    level.setBlock(p.below(), Blocks.FARMLAND.defaultBlockState(), 2);
                    ++i;
                }
            }
        }

        return i > 0;
    }

    private void placeScarecrow(BlockPos blockPos, WorldGenLevel level, RandomSource random) {
        level.setBlock(blockPos, Blocks.DIAMOND_BLOCK.defaultBlockState(),3);
    }

    private boolean placeCorn(BlockPos.MutableBlockPos pos, WorldGenLevel level, RandomSource random) {
        int age = random.nextInt(6);

       return  AbstractCornBlock.spawn(pos, level, age);

    }

    private boolean placePumpkin(BlockPos.MutableBlockPos pos, WorldGenLevel level, RandomSource random) {
        Direction dir = Direction.Plane.HORIZONTAL.getRandomDirection(random);
        BlockPos pumpkinPos = pos.relative(dir);
        if(level.getBlockState(pos).isAir()) {
            if (random.nextBoolean()) {
                if (level.getBlockState(pumpkinPos).isAir() && level.getBlockState(pumpkinPos.below()).is(BlockTags.DIRT)) {
                    level.setBlock(pumpkinPos, Blocks.PUMPKIN.defaultBlockState(), 2);
                    level.setBlock(pos, Blocks.ATTACHED_PUMPKIN_STEM.defaultBlockState().setValue(AttachedStemBlock.FACING, dir), 2);
                    return true;
                }
            } else {
                level.setBlock(pos, Blocks.PUMPKIN_STEM.defaultBlockState().setValue(StemBlock.AGE, random.nextInt(8)), 2);
            }
        }
        return false;
    }

    public record Config(int tries, int xzSpread, int ySpread, boolean pumpkin,
                         boolean scarecrow) implements FeatureConfiguration {
        public static final Codec<Config> CODEC = RecordCodecBuilder.create((instance) -> instance.group(
                ExtraCodecs.POSITIVE_INT.fieldOf("tries").orElse(64).forGetter(Config::tries),
                ExtraCodecs.NON_NEGATIVE_INT.fieldOf("xz_spread").orElse(7).forGetter(Config::xzSpread),
                ExtraCodecs.NON_NEGATIVE_INT.fieldOf("y_spread").orElse(3).forGetter(Config::ySpread),
                Codec.BOOL.optionalFieldOf("is_pumpkin_patch", false).forGetter(Config::pumpkin),
                Codec.BOOL.optionalFieldOf("has_scarecrow", false).forGetter(Config::scarecrow)
        ).apply(instance, Config::new));
    }
}

