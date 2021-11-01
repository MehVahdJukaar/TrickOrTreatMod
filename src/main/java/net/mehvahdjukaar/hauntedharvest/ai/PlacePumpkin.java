package net.mehvahdjukaar.hauntedharvest.ai;

import com.google.common.collect.ImmutableMap;
import net.mehvahdjukaar.hauntedharvest.Halloween;
import net.mehvahdjukaar.hauntedharvest.init.ModRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.behavior.BlockPosTracker;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.memory.WalkTarget;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraftforge.event.ForgeEventFactory;

import javax.annotation.Nullable;
import java.util.Random;

public class PlacePumpkin extends Behavior<Villager> {
    private final float speedModifier;
    private BlockPos targetPos;
    private int ticksSinceReached = 0;
    private int cooldown = 20 * 30;

    public PlacePumpkin(float speed) {
        super(ImmutableMap.of(
                        MemoryModuleType.INTERACTION_TARGET, MemoryStatus.VALUE_ABSENT,
                        ModRegistry.PUMPKIN_POS.get(), MemoryStatus.VALUE_ABSENT,
                        MemoryModuleType.WALK_TARGET, MemoryStatus.VALUE_ABSENT),
                170, 270);
        this.speedModifier = speed;

    }

    @Override
    protected boolean checkExtraStartConditions(ServerLevel pLevel, Villager pOwner) {
        if (!Halloween.IS_HALLOWEEN_TIME) return false;
        if (cooldown-- > 0) return false;
        if (pOwner.isBaby()) return false;
        if (!ForgeEventFactory.getMobGriefingEvent(pLevel, pOwner)) {
            cooldown = 20 * 60;
            return false;
        }
        return true;
    }

    @Override
    protected void start(ServerLevel pLevel, Villager pEntity, long pGameTime) {
        this.cooldown = 20 * (10 + pLevel.random.nextInt(10)) + pLevel.random.nextInt(20);
        this.ticksSinceReached = 0;
        targetPos = getValidPumpkinPos(pLevel, pEntity);

        if (targetPos != null) {
            pEntity.getBrain().eraseMemory(MemoryModuleType.INTERACTION_TARGET);
            pEntity.getBrain().setMemory(MemoryModuleType.WALK_TARGET, new WalkTarget(targetPos, this.speedModifier, 1));
            AskCandy.displayAsHeldItem(pEntity, new ItemStack(Items.PUMPKIN));
        }
    }

    @Override
    protected void stop(ServerLevel pLevel, Villager pEntity, long pGameTime) {
        super.stop(pLevel, pEntity, pGameTime);
        AskCandy.clearHeldItem(pEntity);
        targetPos = null;
    }

    @Override
    protected boolean canStillUse(ServerLevel pLevel, Villager pEntity, long pGameTime) {
        return targetPos != null && isValidPlacementSpot(pLevel, targetPos);
    }

    @Override
    protected void tick(ServerLevel pLevel, Villager pOwner, long pGameTime) {
        if (targetPos != null) {
            //hax
            pOwner.getBrain().eraseMemory(MemoryModuleType.INTERACTION_TARGET);
            pOwner.getBrain().setMemory(MemoryModuleType.WALK_TARGET, new WalkTarget(targetPos, this.speedModifier, 2));

            pOwner.getBrain().setMemory(MemoryModuleType.LOOK_TARGET, new BlockPosTracker(targetPos));
            if (targetPos.closerThan(pOwner.position(), 2.3)) {
                this.ticksSinceReached++;
                if (ticksSinceReached > 20) {
                    BlockState state = Blocks.PUMPKIN.defaultBlockState();
                    pLevel.setBlockAndUpdate(targetPos, state);
                    SoundType soundtype = state.getSoundType(pLevel, targetPos, null);
                    pLevel.playSound(null, targetPos, soundtype.getPlaceSound(), SoundSource.BLOCKS, (soundtype.getVolume() + 1.0F) / 2.0F, soundtype.getPitch() * 0.8F);
                    pOwner.getBrain().setMemory(ModRegistry.PUMPKIN_POS.get(), GlobalPos.of(pLevel.dimension(), targetPos));
                    targetPos = null;
                }
            }
        }
    }

    @Nullable
    private static BlockPos getValidPumpkinPos(ServerLevel pLevel, LivingEntity pWalker) {
        Random random = pWalker.getRandom();
        BlockPos blockpos = pWalker.blockPosition();

        //10 tries
        for (int i = 0; i < 6; ++i) {
            BlockPos pos = blockpos.offset(random.nextInt(20) - 10, random.nextInt(6) - 3, random.nextInt(20) - 10);
            if (isValidPlacementSpot(pLevel, pos)) {
                return pos;
            }
        }

        return null;
    }

    public static boolean isValidPlacementSpot(ServerLevel serverLevel, BlockPos pos) {
        if (serverLevel.canSeeSky(pos) && (double) serverLevel.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING, pos).getY() >= 64) {
            BlockState state = serverLevel.getBlockState(pos);
            if (state.getMaterial().isReplaceable() && state.getFluidState().isEmpty()) {

                BlockState below = serverLevel.getBlockState(pos.below());
                if (below.is(Halloween.PUMPKIN_SUPPORT)) {
                    return true;
                }
            }
        }
        ;
        return false;
    }
}