package net.mehvahdjukaar.hauntedharvest.ai;

import com.google.common.collect.ImmutableMap;
import net.mehvahdjukaar.hauntedharvest.CarvingsManager;
import net.mehvahdjukaar.hauntedharvest.HauntedHarvest;
import net.mehvahdjukaar.hauntedharvest.blocks.ModCarvedPumpkinBlockTile;
import net.mehvahdjukaar.hauntedharvest.reg.ModRegistry;
import net.mehvahdjukaar.moonlight.api.platform.PlatformHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.GlobalPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.behavior.BlockPosTracker;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.memory.WalkTarget;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CarvedPumpkinBlock;

public class CarvePumpkin extends Behavior<Villager> {
    private final float speedModifier;
    private int ticksSinceReached = 0;
    private int cooldown = 20 * 10;

    public CarvePumpkin(float speed) {

        super(ImmutableMap.of(ModRegistry.NEAREST_PUMPKIN.get(), MemoryStatus.VALUE_PRESENT),
                150, 250);
        this.speedModifier = speed;

    }

    @Override
    protected boolean checkExtraStartConditions(ServerLevel pLevel, Villager pOwner) {
        if (!HauntedHarvest.isHalloweenSeason(pLevel)) return false;
        if (cooldown-- > 0) return false;
        if (!pOwner.isBaby()) return false;
        if (!PlatformHelper.isMobGriefingOn(pLevel, pOwner)) {
            cooldown = 20 * 60;
            return false;
        }
        return true;
    }

    @Override
    protected void start(ServerLevel pLevel, Villager pEntity, long pGameTime) {
        this.cooldown = 20 * (30 + pLevel.random.nextInt(30)) + pLevel.random.nextInt(20);
        this.ticksSinceReached = 0;

        BlockPos targetPos = pEntity.getBrain().getMemory(ModRegistry.NEAREST_PUMPKIN.get()).get().pos();

        pEntity.getBrain().eraseMemory(MemoryModuleType.INTERACTION_TARGET);
        pEntity.getBrain().setMemory(MemoryModuleType.WALK_TARGET, new WalkTarget(targetPos, this.speedModifier, 2));
        AskCandy.displayAsHeldItem(pEntity, new ItemStack(Items.SHEARS));

    }

    @Override
    protected void stop(ServerLevel pLevel, Villager pEntity, long pGameTime) {
        super.stop(pLevel, pEntity, pGameTime);
        AskCandy.clearHeldItem(pEntity);
    }

    @Override
    protected boolean canStillUse(ServerLevel pLevel, Villager pEntity, long pGameTime) {
        return pEntity.getBrain().hasMemoryValue(ModRegistry.NEAREST_PUMPKIN.get()) && isValidPumpkin(pLevel, pEntity);
    }

    @Override
    protected void tick(ServerLevel pLevel, Villager pOwner, long pGameTime) {
        BlockPos targetPos = pOwner.getBrain().getMemory(ModRegistry.NEAREST_PUMPKIN.get()).get().pos();
        //hax
        pOwner.getBrain().eraseMemory(MemoryModuleType.INTERACTION_TARGET);
        pOwner.getBrain().setMemory(MemoryModuleType.WALK_TARGET, new WalkTarget(targetPos, this.speedModifier, 1));

        pOwner.getBrain().setMemory(MemoryModuleType.LOOK_TARGET, new BlockPosTracker(targetPos));
        if (targetPos.closerToCenterThan(pOwner.position(), 2.3)) {
            this.ticksSinceReached++;
            if (ticksSinceReached > 40) {
                Direction dir = pOwner.getDirection().getOpposite();

                pLevel.playSound(null, targetPos, SoundEvents.PUMPKIN_CARVE, SoundSource.BLOCKS, 1.0F, 1.0F);

                Block toPlace = pLevel.random.nextInt(4) == 0 ? Blocks.CARVED_PUMPKIN : ModRegistry.MOD_CARVED_PUMPKIN.get();
                pLevel.setBlock(targetPos, toPlace.defaultBlockState().setValue(CarvedPumpkinBlock.FACING, dir), 11);
                if (pLevel.getBlockEntity(targetPos) instanceof ModCarvedPumpkinBlockTile tile) {
                    tile.acceptPixels(CarvingsManager.getRandomCarving(pLevel.random, false));
                }

                pOwner.getBrain().eraseMemory(ModRegistry.NEAREST_PUMPKIN.get());
                pOwner.getBrain().setMemory(ModRegistry.PUMPKIN_POS.get(), GlobalPos.of(pLevel.dimension(), targetPos));
            }
        }

    }

    public static boolean isValidPumpkin(ServerLevel serverLevel, Villager villager) {
        return serverLevel.getBlockState(
                villager.getBrain().getMemory(ModRegistry.NEAREST_PUMPKIN.get()).get().pos()).is(Blocks.PUMPKIN);
    }
}