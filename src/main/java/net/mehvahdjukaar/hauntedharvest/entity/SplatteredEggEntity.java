package net.mehvahdjukaar.hauntedharvest.entity;

import net.mehvahdjukaar.hauntedharvest.init.ModRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.decoration.HangingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DiodeBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import org.apache.commons.lang3.Validate;
import org.jetbrains.annotations.Nullable;

public class SplatteredEggEntity extends HangingEntity {

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

    //vanilla client factory
    @Override
    public Packet<?> getAddEntityPacket() {
        return new ClientboundAddEntityPacket(this, this.getType(), this.direction.get3DDataValue(), this.getPos());
    }

    @Override
    public void recreateFromPacket(ClientboundAddEntityPacket p_149626_) {
        super.recreateFromPacket(p_149626_);
        this.setDirection(Direction.from3DDataValue(p_149626_.getData()));
    }

    @Override
    protected float getEyeHeight(Pose pPose, EntityDimensions pSize) {
        return 0.0F;
    }

    /**
     * Updates facing and bounding box based on it
     */
    @Override
    protected void setDirection(Direction pFacingDirection) {
        Validate.notNull(pFacingDirection);
        this.direction = pFacingDirection;
        if (pFacingDirection.getAxis().isHorizontal()) {
            this.setXRot(0.0F);
            this.setYRot((float) (this.direction.get2DDataValue() * 90));
        } else {
            this.setXRot((float) (-90 * pFacingDirection.getAxisDirection().getStep()));
            this.setYRot(0.0F);
        }

        this.xRotO = this.getXRot();
        this.yRotO = this.getYRot();
        this.recalculateBoundingBox();
        this.altTexture = level.getRandom().nextInt(direction.getAxis() == Direction.Axis.Y ? 6 : 2) == 0;
    }

    /**
     * Updates the entity bounding box based on current facing
     */
    @Override
    protected void recalculateBoundingBox() {
        if (this.direction != null) {
            double d1 = (double) this.pos.getX() + 0.5D - (double) this.direction.getStepX() * 0.46875D;
            double d2 = (double) this.pos.getY() + 0.5D - (double) this.direction.getStepY() * 0.46875D;
            double d3 = (double) this.pos.getZ() + 0.5D - (double) this.direction.getStepZ() * 0.46875D;
            this.setPosRaw(d1, d2, d3);
            double d4 = this.getWidth();
            double d5 = this.getHeight();
            double d6 = this.getWidth();
            switch (this.direction.getAxis()) {
                case X -> d4 = 1.0D;
                case Y -> d5 = 1.0D;
                case Z -> d6 = 1.0D;
            }

            d4 = d4 / 32.0D;
            d5 = d5 / 32.0D;
            d6 = d6 / 32.0D;
            this.setBoundingBox(new AABB(d1 - d4, d2 - d5, d3 - d6, d1 + d4, d2 + d5, d3 + d6));
        }
    }

    /**
     * checks to make sure painting can be placed there
     */
    @Override
    public boolean survives() {
        if (!this.level.noCollision(this)) {
            return false;
        } else {
            BlockState blockstate = this.level.getBlockState(this.pos.relative(this.direction.getOpposite()));
            return (blockstate.getMaterial().isSolid() || this.direction.getAxis().isHorizontal() && DiodeBlock.isDiode(blockstate)) &&
                    this.level.getEntities(this, this.getBoundingBox(), HANGING_ENTITY).isEmpty();
        }
    }

    @Override
    public float getPickRadius() {
        return 0.0F;
    }

    @Override
    public int getWidth() {
        return 14;
    }

    @Override
    public int getHeight() {
        return 14;
    }

    @Override
    public void dropItem(@Nullable Entity pBrokenEntity) {

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
    public void addAdditionalSaveData(CompoundTag pCompound) {
        super.addAdditionalSaveData(pCompound);
        pCompound.putByte("Facing", (byte) this.direction.get3DDataValue());
    }

    /**
     * (abstract) Protected helper method to read subclass entity data from NBT.
     */
    public void readAdditionalSaveData(CompoundTag pCompound) {
        super.readAdditionalSaveData(pCompound);
        this.setDirection(Direction.from3DDataValue(pCompound.getByte("Facing")));
    }


    @Override
    public ItemStack getPickResult() {
        return new ItemStack(Items.EGG);
    }

    @Override
    public void tick() {
        super.tick();
        if (!this.level.isClientSide && this.tickCount > 20 * 30) {
            this.discard();
        }
    }

    @Override
    public boolean isInvulnerableTo(DamageSource pSource) {
        if (pSource.isProjectile()) return true;
        return super.isInvulnerableTo(pSource);
    }
}