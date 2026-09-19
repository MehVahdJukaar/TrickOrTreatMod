package net.mehvahdjukaar.hauntedharvest.blocks;

import net.mehvahdjukaar.hauntedharvest.entity.ICustomPumpkinHolder;
import net.mehvahdjukaar.hauntedharvest.reg.ModTags;
import net.mehvahdjukaar.moonlight.api.util.Utils;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.golem.IronGolem;
import net.minecraft.world.entity.animal.golem.SnowGolem;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
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

    private final Holder<PumpkinType> type;

    public ModCarvedPumpkinBlock(Properties properties, Holder<PumpkinType> type) {
        super(properties);
        this.type = type;
    }

    public Holder<PumpkinType> getType(BlockState state) {
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
        return stack.is(ModTags.CARVERS) || stack.is(ItemTags.SWORDS);
    }

    @Override
    protected InteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player,
                                          InteractionHand hand, BlockHitResult hit) {
        if (level.getBlockEntity(pos) instanceof ModCarvedPumpkinBlockTile te && !te.isWaxed()) {
            InteractionResult waxingRes = te.tryWaxingWithItem(level, pos, player, stack);

            if (waxingRes.consumesAction()) {
                level.gameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Context.of(player, level.getBlockState(pos)));
                te.setChanged(); //this also sends block update in tile
            }
            if (waxingRes != InteractionResult.TRY_WITH_EMPTY_HAND) return waxingRes;
            //torch is handled by event since it needs to cover vanilla ones aswell

            CarveMode mode = te.getCarveMode();

            if (mode != CarveMode.NONE) {
                if (hit.getDirection() == state.getValue(FACING) && mode.canManualDraw() && isCarverItem(stack)) {

                    Vector2i v = getHitSubPixel(hit);
                    int x = v.x();
                    int y = v.y();

                    te.setPixel(x, y, !te.getPixel(x, y));
                    te.setChanged();
                    return InteractionResult.SUCCESS;
                }
                if (mode.canOpenGui()) {
                    if (player instanceof ServerPlayer serverPlayer) {
                        Utils.openGuiIfPossible(te, serverPlayer, stack, hit.getDirection(), hit.getLocation());
                    }
                }
                return InteractionResult.SUCCESS;
            }
        }
        return InteractionResult.TRY_WITH_EMPTY_HAND;
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
    protected ItemStack getCloneItemStack(LevelReader level, BlockPos pos, BlockState state, boolean includeData) {
        if (level.getBlockEntity(pos) instanceof ModCarvedPumpkinBlockTile te) {
            return Utils.saveTileToItem(te);
        }
        return super.getCloneItemStack(level, pos, state, includeData);
    }

    @Override
    protected void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean isMoving) {
        if (!oldState.is(state.getBlock())) {
            //item components get copied onto the tile after onPlace
            level.scheduleTick(pos, this, 1);
        }
    }

    @Override
    protected void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        this.trySpawnGolemWithCustomPumpkin(level, pos);
    }

    //TODO: copper golems can be built from a carved pumpkin too now. needs the weather state + chest swap from vanilla
    protected void trySpawnGolemWithCustomPumpkin(Level level, BlockPos pos) {
        BlockPattern.BlockPatternMatch snowMatch = SNOW_GOLEM_FULL.find(level, pos);
        if (snowMatch != null) {
            SnowGolem snowGolem = EntityType.SNOW_GOLEM.create(level, EntitySpawnReason.TRIGGERED);
            if (snowGolem != null) {
                if (level.getBlockEntity(pos) instanceof ModCarvedPumpkinBlockTile tile
                        && snowGolem instanceof ICustomPumpkinHolder customPumpkinHolder) {
                    customPumpkinHolder.hauntedharvest$setCustomPumpkin(Utils.saveTileToItem(tile));
                }
                spawnGolemInWorld(level, snowMatch, snowGolem, snowMatch.getBlock(0, 2, 0).getPos());
            }
            return;
        }
        BlockPattern.BlockPatternMatch ironMatch = IRON_GOLEM_FULL.find(level, pos);
        if (ironMatch != null) {
            IronGolem ironGolem = EntityType.IRON_GOLEM.create(level, EntitySpawnReason.TRIGGERED);
            if (ironGolem != null) {
                ironGolem.setPlayerCreated(true);
                spawnGolemInWorld(level, ironMatch, ironGolem, ironMatch.getBlock(1, 2, 0).getPos());
            }
        }
    }

    private static void spawnGolemInWorld(Level level, BlockPattern.BlockPatternMatch match, Entity golem, BlockPos spawnPos) {
        clearPatternBlocks(level, match);
        golem.snapTo(spawnPos.getX() + 0.5, spawnPos.getY() + 0.05, spawnPos.getZ() + 0.5, 0.0F, 0.0F);
        level.addFreshEntity(golem);

        for (ServerPlayer serverPlayer : level.getEntitiesOfClass(ServerPlayer.class, golem.getBoundingBox().inflate(5.0))) {
            CriteriaTriggers.SUMMONED_ENTITY.trigger(serverPlayer, golem);
        }
        updatePatternBlocks(level, match);
    }

    private static final Predicate<BlockState> PUMPKINS_PREDICATE = blockState -> blockState != null
            && blockState.getBlock() instanceof ModCarvedPumpkinBlock;

    private static final BlockPattern SNOW_GOLEM_FULL = BlockPatternBuilder.start()
            .aisle("^", "#", "#")
            .where('^', BlockInWorld.hasState(PUMPKINS_PREDICATE))
            .where('#', BlockInWorld.hasState(BlockStatePredicate.forBlock(Blocks.SNOW_BLOCK)))
            .build();

    private static final BlockPattern IRON_GOLEM_FULL = BlockPatternBuilder.start()
            .aisle("~^~", "###", "~#~")
            .where('^', BlockInWorld.hasState(PUMPKINS_PREDICATE))
            .where('#', BlockInWorld.hasState(BlockStatePredicate.forBlock(Blocks.IRON_BLOCK)))
            .where('~', BlockInWorld.hasState(BlockState::isAir))
            .build();

}
