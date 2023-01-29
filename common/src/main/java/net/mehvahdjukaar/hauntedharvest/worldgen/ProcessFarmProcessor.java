package net.mehvahdjukaar.hauntedharvest.worldgen;

import com.mojang.serialization.Codec;
import net.mehvahdjukaar.hauntedharvest.reg.ModRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LanternBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;

import javax.annotation.Nullable;
import java.util.Set;

public class ProcessFarmProcessor extends StructureProcessor {
    private static final ProcessFarmProcessor INSTANCE = new ProcessFarmProcessor();
    public static final Codec<ProcessFarmProcessor> CODEC = Codec.unit(INSTANCE);
    @Nullable
    private final BlockState copperLantern;
    private final Set<Block> validBlocks;

    private BlockPos lastPos = BlockPos.ZERO;
    private boolean lastPlaced = false;


    public ProcessFarmProcessor() {
        this.copperLantern = Registry.BLOCK.getOptional(new ResourceLocation("supplementaries:copper_lantern"))
                .map(c -> c.defaultBlockState().setValue(LanternBlock.HANGING, true).
                        setValue(BlockStateProperties.LIT, false)).orElse(null);
        this.validBlocks = Set.of(Blocks.OAK_PLANKS, Blocks.OAK_STAIRS, Blocks.OAK_SLAB, Blocks.RED_TERRACOTTA, Blocks.STRIPPED_OAK_LOG, Blocks.OAK_LOG);
    }

    @Nullable
    public StructureTemplate.StructureBlockInfo processBlock(
            LevelReader level, BlockPos arg2, BlockPos pos, StructureTemplate.StructureBlockInfo blockInfo,
            StructureTemplate.StructureBlockInfo relativeBlockInfo, StructurePlaceSettings settings) {
        BlockPos blockPos = relativeBlockInfo.pos;
        BlockState replace = null;

        replace = maybeReplaceLantern(relativeBlockInfo, settings, blockPos);

        if (replace == null) {
            replace = maybePlaceCobweb(relativeBlockInfo, settings);
        }

        if (replace == null) {
            replace = maybePlaceMoss(relativeBlockInfo, settings);
        }
        return replace != null ? new StructureTemplate.StructureBlockInfo(blockPos, replace, relativeBlockInfo.nbt) : relativeBlockInfo;

    }

    private BlockState maybePlaceMoss(StructureTemplate.StructureBlockInfo relativeBlockInfo, StructurePlaceSettings settings) {
        BlockState replace = null;
        if (relativeBlockInfo.state.is(Blocks.COBBLESTONE)) {
            RandomSource randomSource = settings.getRandom(relativeBlockInfo.pos);
            if (randomSource.nextFloat() < 0.25) replace = Blocks.MOSSY_COBBLESTONE.defaultBlockState();
        }
        return replace;
    }

    private BlockState maybeReplaceLantern(StructureTemplate.StructureBlockInfo relativeBlockInfo, StructurePlaceSettings settings,
                                           BlockPos blockPos) {
        BlockState replace = null;
        if (relativeBlockInfo.state.is(Blocks.LANTERN)) {
            RandomSource randomSource = settings.getRandom(relativeBlockInfo.pos);
            if (blockPos.closerThan(lastPos, 10)) {
                if (lastPlaced) {
                    lastPlaced = false;
                    replace = Blocks.AIR.defaultBlockState();
                } else if (copperLantern != null) {
                    replace = copperLantern;
                }
            } else if (randomSource.nextBoolean()) {
                lastPlaced = true;
                if (copperLantern != null) {
                    replace = copperLantern;
                }
            } else {
                lastPlaced = false;
                replace = Blocks.AIR.defaultBlockState();
            }
            lastPos = blockPos;
        }
        return replace;
    }

    private BlockState maybePlaceCobweb(StructureTemplate.StructureBlockInfo relativeBlockInfo, StructurePlaceSettings settings) {

        BlockState replace = null;
        if (validBlocks.contains(relativeBlockInfo.state.getBlock())) {
            RandomSource randomSource = settings.getRandom(relativeBlockInfo.pos);
            if (randomSource.nextFloat() < 0.07) replace = Blocks.COBWEB.defaultBlockState();
        }
        return replace;
    }

    protected StructureProcessorType<?> getType() {
        return ModRegistry.FARM_PROCESSOR.get();
    }



}

