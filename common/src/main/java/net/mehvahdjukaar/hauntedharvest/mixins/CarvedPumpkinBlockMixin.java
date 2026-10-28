package net.mehvahdjukaar.hauntedharvest.mixins;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.mehvahdjukaar.hauntedharvest.blocks.ModCarvedPumpkinBlock;
import net.mehvahdjukaar.hauntedharvest.blocks.ModCarvedPumpkinBlockTile;
import net.mehvahdjukaar.hauntedharvest.entity.ICustomPumpkinHolder;
import net.mehvahdjukaar.moonlight.api.util.Utils;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.CarvedPumpkinBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.function.Predicate;

@Mixin(CarvedPumpkinBlock.class)
public abstract class CarvedPumpkinBlockMixin {

    @ModifyExpressionValue(method = {"getOrCreateSnowGolemFull", "getOrCreateIronGolemFull", "getOrCreateCopperGolemFull"},
            at = @At(value = "FIELD", target = "Lnet/minecraft/world/level/block/CarvedPumpkinBlock;PUMPKINS_PREDICATE:Ljava/util/function/Predicate;"))
    private Predicate<BlockState> hauntedharvest$acceptModPumpkins(Predicate<BlockState> original) {
        return original.or(s -> s.getBlock() instanceof ModCarvedPumpkinBlock);
    }

    @ModifyExpressionValue(method = "trySpawnGolem", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/entity/EntityType;create(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/entity/EntitySpawnReason;)Lnet/minecraft/world/entity/Entity;"))
    private Entity hauntedharvest$copyCarvingToGolem(Entity golem, @Local(argsOnly = true) Level level,
                                                     @Local(argsOnly = true) BlockPos pos) {
        if (golem instanceof ICustomPumpkinHolder holder && level.getBlockEntity(pos) instanceof ModCarvedPumpkinBlockTile tile) {
            holder.hauntedharvest$setCustomPumpkin(Utils.saveTileToItem(tile));
        }
        return golem;
    }
}
