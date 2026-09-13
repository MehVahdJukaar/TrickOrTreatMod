package net.mehvahdjukaar.hauntedharvest.entity;

import net.mehvahdjukaar.hauntedharvest.reg.ModRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.decoration.HangingEntity;
import net.minecraft.world.entity.projectile.ThrowableProjectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DiodeBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public class SplatteredEggEntity extends HangingEntity {

    //client
    public boolean altTexture = false;

    public SplatteredEggEntity(EntityType<? extends SplatteredEggEntity> type, Level level) {
        super(type, level);
    }

    public SplatteredEggEntity(Level level, BlockPos pos, Direction direction) {
        this(ModRegistry.SPLATTERED_EGG_ENTITY.get(), level, pos, direction);
    }

    public SplatteredEggEntity(EntityType<? extends SplatteredEggEntity> type, Level level, BlockPos pos, Direction direction) {
        super(type, level, pos);
        this.setDirection(direction);
    }

    public static void spawn(HitResult pResult, ThrowableProjectile egg) {
        var type = pResult.getType();
        if (type == HitResult.Type.BLOCK) {
            BlockHitResult hit = (BlockHitResult) pResult;

            BlockPos blockpos = hit.getBlockPos();
            Direction direction = hit.getDirection();
            BlockPos relative = blockpos.relative(direction);
            Level level = egg.level();
            HangingEntity hangingentity = new SplatteredEggEntity(level, relative, direction);

            if (hangingentity.survives()) {
                if (!level.isClientSide()) {
                    hangingentity.playPlacementSound();
                    level.gameEvent(egg.getOwner(), GameEvent.ENTITY_PLACE, blockpos);
                    level.addFreshEntity(hangingentity);
                }
            }
        }
    }

    /**
     * Updates facing and bounding box based on it. Can't call super, it rejects vertical faces
     */
    @Override
    protected void setDirection(Direction pFacingDirection) {
        this.setDirectionRaw(pFacingDirection);
        if (pFacingDirection.getAxis().isHorizontal()) {
            this.setXRot(0.0F);
            this.setYRot((pFacingDirection.get2DDataValue() * 90));
        } else {
            this.setXRot((-90 * pFacingDirection.getAxisDirection().getStep()));
            this.setYRot(0.0F);
        }

        this.xRotO = this.getXRot();
        this.yRotO = this.getYRot();
        this.recalculateBoundingBox();
        RandomSource ran = RandomSource.create(pos.asLong());
        this.altTexture = ran.nextInt(pFacingDirection.getAxis() == Direction.Axis.Y ? 6 : 2) == 0;
    }

    @Override
    protected AABB calculateBoundingBox(BlockPos blockPos, Direction direction) {
        float size = 14/16f;
        Vec3 vec3 = Vec3.atCenterOf(blockPos).relative(direction, -0.46875F);
        Direction.Axis axis = direction.getAxis();
        double d = axis == Direction.Axis.X ? 0.0625 : size;
        double e = axis == Direction.Axis.Y ? 0.0625 : size;
        double g = axis == Direction.Axis.Z ? 0.0625 : size;
        return AABB.ofSize(vec3, d, e, g);
    }

    /**
     * checks to make sure painting can be placed there
     */
    @Override
    public boolean survives() {
        Level level = this.level();
        if (!level.noCollision(this)) {
            return false;
        } else {
            Direction dir = this.getDirection();
            BlockState blockstate = level.getBlockState(this.pos.relative(dir.getOpposite()));
            return (blockstate.isSolid() || dir.getAxis().isHorizontal() && DiodeBlock.isDiode(blockstate))
                    && this.canCoexist(false);
        }
    }


    @Override
    public void dropItem(ServerLevel level, @Nullable Entity brokenBy) {

    }

    @Override
    public void playPlacementSound() {
        this.playSound(SoundEvents.HONEY_BLOCK_PLACE, 1.0F, 1.2F);
    }

    /**
     * Checks if the entity is in range to render.
     */
    @Override
    public boolean shouldRenderAtSqrDistance(double pDistance) {
        double d0 = 16.0D;
        d0 = d0 * 64.0D * getViewScale();
        return pDistance < d0 * d0;
    }

    @Override
    protected void addAdditionalSaveData(ValueOutput output) {
        super.addAdditionalSaveData(output);
        output.putByte("Facing", (byte) this.getDirection().get3DDataValue());
    }

    @Override
    protected void readAdditionalSaveData(ValueInput input) {
        super.readAdditionalSaveData(input);
        this.setDirection(Direction.from3DDataValue(input.getByteOr("Facing", (byte) 0)));
    }


    @Override
    public ItemStack getPickResult() {
        return new ItemStack(Items.EGG);
    }

    @Override
    public void tick() {
        super.tick();
        if (!this.level().isClientSide() && this.tickCount > 20 * 30) {
            this.discard();
        }
    }

    @Override
    public boolean hurtServer(ServerLevel level, DamageSource source, float damage) {
        if (source.is(DamageTypeTags.IS_PROJECTILE)) return false;
        return super.hurtServer(level, source, damage);
    }

    @Override
    public boolean hurtClient(DamageSource source) {
        if (source.is(DamageTypeTags.IS_PROJECTILE)) return false;
        return super.hurtClient(source);
    }
}