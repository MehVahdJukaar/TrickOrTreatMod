package net.mehvahdjukaar.hauntedharvest.blocks;

import com.mojang.datafixers.util.Pair;
import net.mehvahdjukaar.hauntedharvest.reg.ModConfigs;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.HoneycombItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.CarvedPumpkinBlock;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;

public class ModCarvedPumpkinBlock extends CarvedPumpkinBlock implements EntityBlock {

    public ModCarvedPumpkinBlock(Properties properties) {
        super(properties);
    }

    public static Pair<Integer, Integer> getHitSubPixel(BlockHitResult hit) {
        Vec3 v2 = hit.getLocation();
        Vec3 v = v2.yRot((float) ((hit.getDirection().toYRot()) * Math.PI / 180f));
        double fx = ((v.x % 1) * 16);
        if (fx < 0) fx += 16;
        int x = Mth.clamp((int) fx, -15, 15);

        int y = 15 - (int) Mth.clamp(Math.abs((v.y % 1) * 16), 0, 15);
        if (v2.y < 0) y = 15 - y; //crappy logic
        return new Pair<>(x, y);
    }

    public static boolean isCarverItem(ItemStack mainHandItem) {
        return true;
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand handIn,
                                 BlockHitResult hit) {
        if (level.getBlockEntity(pos) instanceof ModCarvedPumpkinBlockTile te &&
                te.isAccessibleBy(player) && !te.isWaxed()) {
            ItemStack stack = player.getItemInHand(handIn);
            Item i = stack.getItem();
            if (i instanceof HoneycombItem) {
                if (player instanceof ServerPlayer serverPlayer) {
                    CriteriaTriggers.ITEM_USED_ON_BLOCK.trigger( serverPlayer, pos, stack);
                }
                if (!player.isCreative()) {
                    stack.shrink(1);
                }
                //TODO use better particles shape
                level.levelEvent(player, 3003, pos, 0);
                te.setWaxed(true);
                return InteractionResult.sidedSuccess(level.isClientSide);
            }

            UseMode mode = ModConfigs.CARVE_MODE.get();

            if (hit.getDirection() == state.getValue(FACING) && mode.canManualDraw()) {

                Pair<Integer, Integer> pair = getHitSubPixel(hit);
                int x = pair.getFirst();
                int y = pair.getSecond();

                te.setPixel(x, y, !te.getPixel(x, y));
                te.setChanged();
                return InteractionResult.sidedSuccess(level.isClientSide);
            }
            if (!level.isClientSide && mode.canOpenGui()) {
                te.sendOpenGuiPacket(level, pos, player);
            }
            return InteractionResult.sidedSuccess(level.isClientSide);
        }
        return InteractionResult.PASS;
    }

    public enum UseMode {
        BOTH, GUI, MANUAL;

        public boolean canOpenGui() {
            return this != MANUAL;
        }

        public boolean canManualDraw() {
            return this != GUI;
        }
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return new ModCarvedPumpkinBlockTile(pPos, pState);
    }

    public ItemStack getBlackboardItem(ModCarvedPumpkinBlockTile te) {
        ItemStack itemstack = new ItemStack(this);
        if (!te.isEmpty()) {
            CompoundTag tag = te.savePixels(new CompoundTag());
            if (!tag.isEmpty()) {
                itemstack.addTagElement("BlockEntityTag", tag);
            }
        }
        return itemstack;
    }

    @Override
    public ItemStack getCloneItemStack(BlockGetter level, BlockPos pos, BlockState state) {
        if (level.getBlockEntity(pos) instanceof ModCarvedPumpkinBlockTile te) {
            return this.getBlackboardItem(te);
        }
        return super.getCloneItemStack(level, pos, state);
    }
}
