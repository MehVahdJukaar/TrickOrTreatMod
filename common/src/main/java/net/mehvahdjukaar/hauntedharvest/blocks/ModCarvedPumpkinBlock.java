package net.mehvahdjukaar.hauntedharvest.blocks;

import net.mehvahdjukaar.hauntedharvest.HHPlatformStuff;
import net.mehvahdjukaar.hauntedharvest.entity.ICustomPumpkinHolder;
import net.mehvahdjukaar.hauntedharvest.reg.ModTags;
import net.mehvahdjukaar.moonlight.api.util.Utils;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.AbstractGolem;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.animal.SnowGolem;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CarvedPumpkinBlock;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.pattern.BlockInWorld;
import net.minecraft.world.level.block.state.pattern.BlockPattern;
import net.minecraft.world.level.block.state.pattern.BlockPatternBuilder;
import net.minecraft.world.level.block.state.predicate.BlockStatePredicate;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector2i;

import java.util.function.Predicate;

public class ModCarvedPumpkinBlock extends CarvedPumpkinBlock implements EntityBlock {

    private final PumpkinType type;

    public ModCarvedPumpkinBlock(Properties properties, PumpkinType type) {
        super(properties);
        this.type = type;
    }

    public PumpkinType getType(BlockState state) {
        return type;
    }

    public static Vector2i getHitSubPixel(BlockHitResult hit) {
        Vec3 pos = hit.getLocation();
        Vec3 v = pos.yRot((float) ((hit.getDirection().toYRot()) * Math.PI / 180f));
        double fx = ((v.x % 1) * 16);
        if (fx < 0) fx += 16;
        int x = Mth.clamp((int) fx, -15, 15);

        int y = 15 - (int) Mth.clamp(Math.abs((v.y % 1) * 16), 0, 15);
        if (pos.y < 0) y = 15 - y; //crappy logic
        return new Vector2i(x, y);
    }

    public static boolean isCarverItem(ItemStack stack) {
        return stack.is(ModTags.CARVERS) || stack.getItem() instanceof SwordItem;
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player,
                                              InteractionHand hand, BlockHitResult hit) {
        if (level.getBlockEntity(pos) instanceof ModCarvedPumpkinBlockTile te && !te.isWaxed()) {
            ItemInteractionResult waxingRes = te.tryWaxingWithItem(level, pos, player, stack);

            if (waxingRes.consumesAction()) {
                level.gameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Context.of(player, level.getBlockState(pos)));
                te.setChanged(); //this also sends block update in tile
            }
            if (waxingRes != ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION) return waxingRes;
            //torch is handled by event since it needs to cover vanilla ones aswell

            CarveMode mode = te.getCarveMode();

            if (mode != CarveMode.NONE) {
                if (hit.getDirection() == state.getValue(FACING) && mode.canManualDraw() && isCarverItem(stack)) {

                    Vector2i v = getHitSubPixel(hit);
                    int x = v.x();
                    int y = v.y();

                    te.setPixel(x, y, !te.getPixel(x, y));
                    te.setChanged();
                    return ItemInteractionResult.sidedSuccess(level.isClientSide);
                }
                if (mode.canOpenGui()) {
                    if (player instanceof ServerPlayer serverPlayer) {
                        te.tryOpeningEditGui(serverPlayer, pos, stack);
                    }
                }
                return ItemInteractionResult.sidedSuccess(level.isClientSide);
            }
        }
        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    public enum CarveMode {
        NONE, BOTH, GUI, MANUAL;

        public boolean canOpenGui() {
            return this != MANUAL && this != NONE;
        }

        public boolean canManualDraw() {
            return this != GUI && this != NONE;
        }

    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return new ModCarvedPumpkinBlockTile(pPos, pState);
    }

    @Override
    public ItemStack getCloneItemStack(LevelReader level, BlockPos pos, BlockState state) {
        if (level.getBlockEntity(pos) instanceof ModCarvedPumpkinBlockTile te) {
            return Utils.saveTileToItem(te);
        }
        return super.getCloneItemStack(level, pos, state);
    }

    @Override
    public void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean isMoving) {
        if (!oldState.is(state.getBlock())) {
            this.trySpawnGolemWithCustomPumpkin(level, pos);
        }
    }

    protected void trySpawnGolemWithCustomPumpkin(Level level, BlockPos pos) {
        BlockPattern.BlockPatternMatch blockPatternMatch = SNOW_GOLEM_FULL.find(level, pos);
        if (blockPatternMatch != null) {

            SnowGolem snowGolem = EntityType.SNOW_GOLEM.create(level);
            if (level.getBlockEntity(pos) instanceof ModCarvedPumpkinBlockTile tile) {
                if (snowGolem instanceof ICustomPumpkinHolder customPumpkinHolder) {
                    ItemStack stack =  Utils.saveTileToItem(tile);
                    customPumpkinHolder.hauntedharvest$setCustomPumpkin(stack);
                }
            }

            for (int i = 0; i < SNOW_GOLEM_FULL.getHeight(); ++i) {
                BlockInWorld blockInWorld = blockPatternMatch.getBlock(0, i, 0);
                level.setBlock(blockInWorld.getPos(), Blocks.AIR.defaultBlockState(), 2);
                level.levelEvent(2001, blockInWorld.getPos(), Block.getId(blockInWorld.getState()));
            }

            BlockPos blockPos = blockPatternMatch.getBlock(0, 2, 0).getPos();
            spawnToLocation(level, blockPos, snowGolem);

            for (int j = 0; j < SNOW_GOLEM_FULL.getHeight(); ++j) {
                BlockInWorld blockInWorld2 = blockPatternMatch.getBlock(0, j, 0);
                level.blockUpdated(blockInWorld2.getPos(), Blocks.AIR);
            }
        } else {
            blockPatternMatch = this.getOrCreateIronGolemFull().find(level, pos);
            if (blockPatternMatch != null) {
                for (int i = 0; i < this.getOrCreateIronGolemFull().getWidth(); ++i) {
                    for (int k = 0; k < this.getOrCreateIronGolemFull().getHeight(); ++k) {
                        BlockInWorld blockInWorld3 = blockPatternMatch.getBlock(i, k, 0);
                        level.setBlock(blockInWorld3.getPos(), Blocks.AIR.defaultBlockState(), 2);
                        level.levelEvent(2001, blockInWorld3.getPos(), Block.getId(blockInWorld3.getState()));
                    }
                }

                BlockPos blockPos2 = blockPatternMatch.getBlock(1, 2, 0).getPos();
                IronGolem ironGolem = EntityType.IRON_GOLEM.create(level);
                ironGolem.setPlayerCreated(true);
                spawnToLocation(level, blockPos2, ironGolem);

                for (int j = 0; j < this.getOrCreateIronGolemFull().getWidth(); ++j) {
                    for (int l = 0; l < this.getOrCreateIronGolemFull().getHeight(); ++l) {
                        BlockInWorld blockInWorld4 = blockPatternMatch.getBlock(j, l, 0);
                        level.blockUpdated(blockInWorld4.getPos(), Blocks.AIR);
                    }
                }
            }
        }
    }

    private static void spawnToLocation(Level level, BlockPos blockPos2, AbstractGolem ironGolem) {
        ironGolem.moveTo(blockPos2.getX() + 0.5, blockPos2.getY() + 0.05, blockPos2.getZ() + 0.5, 0.0F, 0.0F);
        level.addFreshEntity(ironGolem);

        for (ServerPlayer serverPlayer : level.getEntitiesOfClass(ServerPlayer.class, ironGolem.getBoundingBox().inflate(5.0))) {
            CriteriaTriggers.SUMMONED_ENTITY.trigger(serverPlayer, ironGolem);
        }
    }

    private static final Predicate<BlockState> PUMPKINS_PREDICATE = blockState -> blockState != null
            && blockState.getBlock() instanceof ModCarvedPumpkinBlock;

    private static final BlockPattern SNOW_GOLEM_FULL = BlockPatternBuilder.start()
            .aisle("^", "#", "#")
            .where('^', BlockInWorld.hasState(PUMPKINS_PREDICATE))
            .where('#', BlockInWorld.hasState(BlockStatePredicate.forBlock(Blocks.SNOW_BLOCK)))
            .build();

}
