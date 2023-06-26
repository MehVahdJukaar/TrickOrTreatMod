package net.mehvahdjukaar.hauntedharvest.blocks;

import net.mehvahdjukaar.hauntedharvest.reg.ModRegistry;
import net.mehvahdjukaar.hauntedharvest.reg.ModTags;
import net.mehvahdjukaar.moonlight.api.util.Utils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Registry;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class CandyBagBlock extends Block implements EntityBlock {

    public static final EnumProperty<Content> CONTENT = EnumProperty.create("content", Content.class);
    public static final IntegerProperty FILL_LEVEL = IntegerProperty.create("fill_level", 1, 6);
    private static final int POPCORN_COOK_TIME = 20 * 10;

    public CandyBagBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.defaultBlockState().setValue(CONTENT, Content.POPCORN).setValue(FILL_LEVEL, 1));
    }

    public static boolean tryFilling(Player player, Level level, BlockPos pos, ItemStack stack) {
        Content content = Content.get(stack);
        if (content != null) {
            ItemStack remove = player.isCreative() ? stack.copy() : stack.split(1);
            remove.setCount(1);
            BlockState state = ModRegistry.CANDY_BAG.get().defaultBlockState().setValue(CONTENT, content);
            level.setBlockAndUpdate(pos, state);
            playSound(level, pos);
            schedulePopTickIfPossible(state, level, pos);
            if (content == Content.OTHER_CANDY) {
                if (level.getBlockEntity(pos) instanceof CandyBagTile tile) {
                    tile.setDisplayedItem(remove);
                }
            }
            return true;
        }
        return false;
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        super.setPlacedBy(level, pos, state, placer, stack);
        schedulePopTickIfPossible(state, level, pos);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return PaperBagBlock.SHAPE;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(CONTENT, FILL_LEVEL);
    }

    @Override
    public List<ItemStack> getDrops(BlockState state, LootParams.Builder builder) {
        List<ItemStack> list = super.getDrops(state, builder);
        if (builder.getOptionalParameter(LootContextParams.BLOCK_ENTITY) instanceof CandyBagTile tile) {
            list.add(tile.getDisplayedItem().copy());
        }
        var i = getContent(state);
        if (i != null) {
            list.add(new ItemStack(i, state.getValue(FILL_LEVEL)));
        }
        return list;
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand,
                                 BlockHitResult hit) {
        int fill = state.getValue(FILL_LEVEL);
        ItemStack item;
        if (level.getBlockEntity(pos) instanceof CandyBagTile tile) {
            item = tile.getDisplayedItem();
            if (item.isEmpty()) item = null;
        } else {
            item = new ItemStack(getContent(state), fill);
        }
        if (item == null) return InteractionResult.PASS;


        int delta = 0;
        ItemStack held = player.getItemInHand(hand);
        if (player.isShiftKeyDown() && held.isEmpty()) {
            ItemStack extracted = item.copy().split(1);
            if (!extracted.isEmpty()) {
                Utils.swapItem(player, hand, extracted);
                delta = -1;
            }
        } else if (fill != 6 && Content.get(held) == state.getValue(CONTENT)) {
            if (!player.isCreative()) held.shrink(1);
            playSound(level, pos);
            delta += 1;
        } else {
            if (item.isEdible() && player.canEat(false) && !player.isCreative()) {
                //eat cookies
                player.eat(level, item.copy());
                delta = -1;
                if (level.isClientSide) {
                    ParticleOptions particleOptions = new ItemParticleOption(ParticleTypes.ITEM, item);
                    double dy = 0.005 + fill / 16d;
                    double power = 0.2;
                    for (int i = 0; i < 12; ++i) {
                        level.addParticle(particleOptions,
                                pos.getX() + 2 / 16f + level.random.nextFloat() * 12 / 16f,
                                pos.getY() + dy,
                                pos.getZ() + 2 / 16f + level.random.nextFloat() * 12 / 16f,
                                (level.random.nextFloat() - 0.5) * power,
                                (level.random.nextFloat()) * power * 0.7,
                                (level.random.nextFloat() - 0.5) * power);
                    }
                }
            }
        }
        if (delta != 0) {

            int newFill = fill + delta;
            if (newFill == 0) {
                level.setBlockAndUpdate(pos, ModRegistry.PAPER_BAG.get().defaultBlockState());
            } else {
                level.setBlockAndUpdate(pos, state.setValue(FILL_LEVEL, newFill));
            }
            return InteractionResult.sidedSuccess(level.isClientSide);
        }
        return InteractionResult.PASS;
    }

    private static void playSound(Level level, BlockPos pos) {
        level.playSound(null, pos, SoundEvents.CROP_PLANTED, SoundSource.PLAYERS, 1, 1.2f);
    }

    @Nullable
    public Item getContent(BlockState state) {
        var c = state.getValue(CONTENT);
        if (c.drop != null) {
            var i = BuiltInRegistries.ITEM.getOptional(new ResourceLocation(c.drop));
            if (i.isPresent()) return i.get();
        }
        return null;
    }

    @Override
    public void neighborChanged(BlockState state, Level level, BlockPos pos, Block neighbor, BlockPos fromPos, boolean isMoving) {
        super.neighborChanged(state, level, pos, neighbor, fromPos, isMoving);
        if (state.getValue(CONTENT) == Content.KERNELS && canCook(level.getBlockState(fromPos))) {
            level.scheduleTick(pos, this, POPCORN_COOK_TIME);
        }
    }

    @Override
    public void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (state.getValue(CONTENT) == Content.KERNELS) {
            var canPop = Arrays.stream(Direction.values()).anyMatch(d -> canCook(level.getBlockState(pos.relative(d))));
            if (canPop) {
                popCorn(state, level, pos);
                level.scheduleTick(pos, this, 3);
            }
        }
    }

    private static void schedulePopTickIfPossible(BlockState state, Level level, BlockPos pos) {
        var canPop = Arrays.stream(Direction.values()).anyMatch(d -> canCook(level.getBlockState(pos.relative(d))));
        if (canPop) {
            level.scheduleTick(pos, state.getBlock(), POPCORN_COOK_TIME);
        }
    }

    private void popCorn(BlockState state, ServerLevel level, BlockPos pos) {
        int fill = state.getValue(FILL_LEVEL);
        ItemStack item = new ItemStack(ModRegistry.POP_CORN.get());
        level.blockEvent(pos, this, 1, 0);
        if (fill == 1) {
            level.setBlockAndUpdate(pos, ModRegistry.PAPER_BAG.get().defaultBlockState());
        } else {
            level.setBlockAndUpdate(pos, state.setValue(FILL_LEVEL, fill - 1));
        }

        ItemEntity itemEntity = new ItemEntity(level, pos.getX() + 0.5, pos.getY() + 0.6, pos.getZ() + 0.5, item);

        itemEntity.setDeltaMovement(level.random.nextDouble() * 0.02,
                0.08 + level.random.nextDouble() * 0.2,
                level.random.nextDouble() * 0.02);
        level.addFreshEntity(itemEntity);

        level.playSound(null, pos, SoundEvents.FIREWORK_ROCKET_BLAST, SoundSource.BLOCKS, 0.2f, 2f);
    }

    @Override
    public boolean triggerEvent(BlockState state, Level level, BlockPos pos, int id, int param) {
        if (id == 1) {
            if (level.isClientSide) {
                int fill = state.getValue(FILL_LEVEL);
                ItemStack item = new ItemStack(ModRegistry.POP_CORN.get());
                ParticleOptions particleOptions = new ItemParticleOption(ParticleTypes.ITEM, item);
                double dy = 0.005 + fill / 16d;
                double power = 0.3;
                for (int i = 0; i < 7; ++i) {
                    level.addParticle(particleOptions,
                            pos.getX() + 0.5,
                            pos.getY() + dy,
                            pos.getZ() + 0.5,
                            (level.random.nextFloat() - 0.5) * power,
                            (level.random.nextFloat()) * power + 0.2,
                            (level.random.nextFloat() - 0.5) * power);
                }
            }
            return true;
        }
        return super.triggerEvent(state, level, pos, id, param);
    }

    private static boolean canCook(BlockState neighbor) {
        return (neighbor.is(BlockTags.CAMPFIRES) && neighbor.getValue(CampfireBlock.LIT))
                || neighbor.getBlock() instanceof FireBlock;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return state.getValue(CONTENT) == Content.OTHER_CANDY ? new CandyBagTile(pos, state) : null;
    }

    @Override
    public ItemStack getCloneItemStack(BlockGetter level, BlockPos pos, BlockState state) {
        return ModRegistry.PAPER_BAG.get().asItem().getDefaultInstance();
    }

    public enum Content implements StringRepresentable {
        CANDY("supplementaries:candy"),
        CANDY_CANE("snowyspirit:candy_cane"),
        CANDY_CORN("hauntedharvest:candy_corn"),
        POPCORN("hauntedharvest:popcorn"),
        KERNELS("hauntedharvest:kernels"),
        OTHER_CANDY(null);

        private final String drop;

        Content(String drop) {
            this.drop = drop;
        }

        @Nullable
        public static Content get(ItemStack item) {
            if (item.isEmpty()) return null;
            String name = Utils.getID(item.getItem()).toString();
            for (var c : Content.values()) {
                if (c.drop != null && c.drop.equals(name)) return c;
            }
            if (item.is(ModTags.MODDED_CANDIES)) return OTHER_CANDY;
            return null;
        }

        @Override
        public String getSerializedName() {
            return this.toString().toLowerCase(Locale.ROOT);
        }
    }
}
