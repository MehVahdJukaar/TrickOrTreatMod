package net.mehvahdjukaar.hauntedharvest.worldgen;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.mehvahdjukaar.hauntedharvest.CarvingsManager;
import net.mehvahdjukaar.hauntedharvest.HauntedHarvest;
import net.mehvahdjukaar.hauntedharvest.blocks.AbstractCornBlock;
import net.mehvahdjukaar.hauntedharvest.blocks.ModCarvedPumpkinBlockTile;
import net.mehvahdjukaar.hauntedharvest.configs.CommonConfigs;
import net.mehvahdjukaar.hauntedharvest.configs.RegistryConfigs;
import net.mehvahdjukaar.hauntedharvest.integration.FDCompat;
import net.mehvahdjukaar.hauntedharvest.integration.SuppCompat;
import net.mehvahdjukaar.hauntedharvest.reg.ModRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.RandomSource;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;

import java.util.Locale;

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
        CropType crop = config.crop();
        if (!crop.isEnabled()) return false;
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

            if (level.getBlockState(p.below()).is(BlockTags.DIRT)) {
                if (switch (crop) {
                    default -> placePumpkin(p, level, random);
                    case CORN -> placeCorn(p, level, random);
                    case FLAX -> SuppCompat.placeFlax(p, level, random);
                    case TOMATOES -> placeTomatoes(p, level, random);
                }) {
                    level.setBlock(p.below(), Blocks.FARMLAND.defaultBlockState(), 2);
                    ++i;
                }
            }
        }

        return i > 0;
    }

    private boolean placeTomatoes(BlockPos.MutableBlockPos p, WorldGenLevel level, RandomSource random) {
        if (level.getBlockState(p).isAir()) {
            level.setBlock(p, FDCompat.getTomato(random), 2);
            return true;
        }
        return false;
    }

    private void placeScarecrow(BlockPos blockPos, WorldGenLevel level, RandomSource random) {

        level.setBlock(blockPos, Blocks.SPRUCE_FENCE.defaultBlockState(), 2);
        BlockPos above = blockPos.above();
        Direction dir = Direction.Plane.HORIZONTAL.getRandomDirection(random);
        level.setBlock(above, Blocks.HAY_BLOCK.defaultBlockState(), 3);
        BlockPos left = above.relative(dir);
        level.setBlock(left, Block.updateFromNeighbourShapes(Blocks.SPRUCE_FENCE.defaultBlockState(), level, left), 2);
        BlockPos right = above.relative(dir.getOpposite());
        level.setBlock(right, Block.updateFromNeighbourShapes(Blocks.SPRUCE_FENCE.defaultBlockState(), level, right), 2);

        Block toPlace;
        if (CommonConfigs.CUSTOM_CARVINGS.get()) {
            toPlace = (random.nextInt(7) == 0 ? ModRegistry.MOD_JACK_O_LANTERN : ModRegistry.MOD_CARVED_PUMPKIN).get();
        } else {
            toPlace = (random.nextInt(7) == 0 ? Blocks.JACK_O_LANTERN : Blocks.CARVED_PUMPKIN);
        }
        level.setBlock(above.above(), toPlace.defaultBlockState()
                .setValue(CarvedPumpkinBlock.FACING, dir.getClockWise()), 2);

        if (level.getBlockEntity(above.above()) instanceof ModCarvedPumpkinBlockTile tile) {
            tile.acceptPixels(CarvingsManager.getRandomCarving(random, true));
            tile.setChanged();
        }
    }

    private boolean placeCorn(BlockPos.MutableBlockPos pos, WorldGenLevel level, RandomSource random) {
        int age = random.nextInt(7);
        return AbstractCornBlock.spawn(pos, level, age);
    }

    private boolean placePumpkin(BlockPos.MutableBlockPos pos, WorldGenLevel level, RandomSource random) {
        Direction dir = Direction.Plane.HORIZONTAL.getRandomDirection(random);
        BlockPos pumpkinPos = pos.relative(dir);
        if (level.getBlockState(pos).isAir()) {
            if (random.nextBoolean()) {
                if (level.getBlockState(pumpkinPos).isAir() && level.getBlockState(pumpkinPos.below()).is(BlockTags.DIRT)) {
                    level.setBlock(pumpkinPos, Blocks.PUMPKIN.defaultBlockState(), 2);
                    level.setBlock(pos, Blocks.ATTACHED_PUMPKIN_STEM.defaultBlockState().setValue(AttachedStemBlock.FACING, dir), 2);
                    return true;
                }
            } else {
                level.setBlock(pos, Blocks.PUMPKIN_STEM.defaultBlockState().setValue(StemBlock.AGE, random.nextInt(8)), 2);
                return true;
            }
        }
        return false;
    }

    public enum CropType implements StringRepresentable {
        PUMPKIN, CORN, FLAX, TOMATOES;

        public boolean isEnabled() {
            return switch (this) {
                case CORN -> RegistryConfigs.CORN_ENABLED.get();
                case FLAX -> HauntedHarvest.SUPP_INSTALLED && SuppCompat.isFlaxOn();
                case PUMPKIN -> true;
                case TOMATOES -> HauntedHarvest.FD_INSTALLED;
            };
        }

        @Override
        public String getSerializedName() {
            return this.toString().toLowerCase(Locale.ROOT);
        }
    }

    public record Config(int tries, int xzSpread, int ySpread, CropType crop,
                         boolean scarecrow) implements FeatureConfiguration {

        public static final Codec<Config> CODEC = RecordCodecBuilder.create((instance) -> instance.group(
                ExtraCodecs.POSITIVE_INT.fieldOf("tries").orElse(64).forGetter(Config::tries),
                ExtraCodecs.NON_NEGATIVE_INT.fieldOf("xz_spread").orElse(7).forGetter(Config::xzSpread),
                ExtraCodecs.NON_NEGATIVE_INT.fieldOf("y_spread").orElse(3).forGetter(Config::ySpread),
                StringRepresentable.fromEnum(CropType::values).fieldOf("crop").forGetter(Config::crop),
                Codec.BOOL.optionalFieldOf("has_scarecrow", false).forGetter(Config::scarecrow)
        ).apply(instance, Config::new));
    }
}

