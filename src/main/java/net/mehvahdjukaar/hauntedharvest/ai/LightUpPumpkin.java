package net.mehvahdjukaar.hauntedharvest.ai;

import com.google.common.collect.ImmutableMap;
import net.mehvahdjukaar.hauntedharvest.Halloween;
import net.mehvahdjukaar.hauntedharvest.init.ModRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.behavior.BlockPosTracker;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.memory.WalkTarget;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CarvedPumpkinBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.event.ForgeEventFactory;

public class LightUpPumpkin extends Behavior<Villager> {
    private final float speedModifier;
    private int ticksSinceReached = 0;
    private int cooldown = 20 * 5;
    protected int lastBreakProgress = -1;

    public LightUpPumpkin(float speed) {
        super(ImmutableMap.of(
                        MemoryModuleType.INTERACTION_TARGET, MemoryStatus.VALUE_ABSENT,
                        ModRegistry.PUMPKIN_POS.get(), MemoryStatus.VALUE_PRESENT),
                200, 250);
        this.speedModifier = speed;

    }

    @Override
    protected boolean checkExtraStartConditions(ServerLevel pLevel, Villager pOwner) {
        if (!Halloween.IS_HALLOWEEN_TIME) return false;
        if (cooldown-- > 0) return false;
        if (!ForgeEventFactory.getMobGriefingEvent(pLevel, pOwner)) {
            cooldown = 20 * 60;
            return false;
        }
        GlobalPos globalpos = pOwner.getBrain().getMemory(ModRegistry.PUMPKIN_POS.get()).get();
        return globalpos.dimension() == pLevel.dimension() && pOwner.isBaby() &&
                globalpos.pos().closerThan(pOwner.position(), 28);
    }

    @Override
    protected void start(ServerLevel pLevel, Villager pEntity, long pGameTime) {
        this.cooldown = 20 * (20 + pLevel.random.nextInt(20)) + pLevel.random.nextInt(20);
        this.ticksSinceReached = 0;
        this.lastBreakProgress = -1;
        GlobalPos globalpos = pEntity.getBrain().getMemory(ModRegistry.PUMPKIN_POS.get()).get();

        pEntity.getBrain().eraseMemory(MemoryModuleType.INTERACTION_TARGET);
        pEntity.getBrain().setMemory(MemoryModuleType.WALK_TARGET, new WalkTarget(globalpos.pos(), this.speedModifier, 1));
        AskCandy.displayAsHeldItem(pEntity, new ItemStack(Items.TORCH));
    }

    @Override
    protected void stop(ServerLevel pLevel, Villager pEntity, long pGameTime) {
        super.stop(pLevel, pEntity, pGameTime);
        AskCandy.clearHeldItem(pEntity);
    }

    @Override
    protected boolean canStillUse(ServerLevel pLevel, Villager pEntity, long pGameTime) {
        return pEntity.getBrain().hasMemoryValue(ModRegistry.PUMPKIN_POS.get());
    }

    @Override
    protected void tick(ServerLevel pLevel, Villager pOwner, long pGameTime) {
        BlockPos pos = pOwner.getBrain().getMemory(ModRegistry.PUMPKIN_POS.get()).get().pos();

        //hax
        pOwner.getBrain().eraseMemory(MemoryModuleType.INTERACTION_TARGET);
        pOwner.getBrain().setMemory(MemoryModuleType.WALK_TARGET, new WalkTarget(pos, this.speedModifier, 1));

        pOwner.getBrain().setMemory(MemoryModuleType.LOOK_TARGET, new BlockPosTracker(pos));
        if (pos.closerThan(pOwner.position(), 1.73D)) {
            this.ticksSinceReached++;

            BlockState state = pLevel.getBlockState(pos);
            if (!state.is(Blocks.CARVED_PUMPKIN)) {
                pOwner.getBrain().eraseMemory(ModRegistry.PUMPKIN_POS.get());
            } else {

                if (ticksSinceReached > 40) {

                    SoundType soundtype = Blocks.TORCH.defaultBlockState().getSoundType(pLevel, pos, null);
                    pLevel.playSound(null, pos, soundtype.getBreakSound(), SoundSource.BLOCKS, (soundtype.getVolume() + 1.0F) / 2.0F, soundtype.getPitch() * 0.8F);
                    pOwner.getBrain().eraseMemory(ModRegistry.PUMPKIN_POS.get());
                    pLevel.setBlockAndUpdate(pos, Blocks.JACK_O_LANTERN.defaultBlockState()
                            .setValue(CarvedPumpkinBlock.FACING, state.getValue(CarvedPumpkinBlock.FACING)));
                }
            }

        }
    }
}