package net.mehvahdjukaar.hauntedharvest.ai;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import net.mehvahdjukaar.hauntedharvest.reg.ModRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.sensing.Sensor;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;

import java.util.List;
import java.util.Set;

public class PumpkinPoiSensor extends Sensor<Villager> {

    public PumpkinPoiSensor() {
        super(200);
    }

    @Override
    protected void doTick(ServerLevel pLevel, Villager pEntity) {
        if (pEntity.isBaby()) {
            ResourceKey<Level> resourcekey = pLevel.dimension();
            BlockPos blockpos = pEntity.blockPosition();
            List<GlobalPos> list = Lists.newArrayList();
            int i = 4;

            for (int j = -4; j <= 4; ++j) {
                for (int k = -2; k <= 2; ++k) {
                    for (int l = -4; l <= 4; ++l) {
                        BlockPos blockpos1 = blockpos.offset(j, k, l);
                        if (pLevel.getBlockState(blockpos1).is(Blocks.PUMPKIN)) {
                            list.add(GlobalPos.of(resourcekey, blockpos1));
                            break;
                        }
                    }
                }
            }

            Brain<?> brain = pEntity.getBrain();
            if (!list.isEmpty()) {
                brain.setMemory(ModRegistry.NEAREST_PUMPKIN.get(), list.get(0));
            } else {
                brain.eraseMemory(ModRegistry.NEAREST_PUMPKIN.get());
            }
        }

    }

    @Override
    public Set<MemoryModuleType<?>> requires() {
        return ImmutableSet.of(ModRegistry.NEAREST_PUMPKIN.get());
    }
}