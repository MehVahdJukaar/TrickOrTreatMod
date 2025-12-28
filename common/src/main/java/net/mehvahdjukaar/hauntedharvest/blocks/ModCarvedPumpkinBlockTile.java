package net.mehvahdjukaar.hauntedharvest.blocks;

import net.mehvahdjukaar.hauntedharvest.HauntedHarvest;
import net.mehvahdjukaar.hauntedharvest.client.screens.CarvingScreen;
import net.mehvahdjukaar.hauntedharvest.configs.CommonConfigs;
import net.mehvahdjukaar.hauntedharvest.items.components.PumpkinCarvingData;
import net.mehvahdjukaar.hauntedharvest.reg.ModRegistry;
import net.mehvahdjukaar.moonlight.api.block.IOneUserInteractable;
import net.mehvahdjukaar.moonlight.api.block.IWaxable;
import net.mehvahdjukaar.moonlight.api.client.IScreenProvider;
import net.mehvahdjukaar.moonlight.api.client.model.ExtraModelData;
import net.mehvahdjukaar.moonlight.api.client.model.IExtraModelDataProvider;
import net.mehvahdjukaar.moonlight.api.client.model.ModelDataKey;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class ModCarvedPumpkinBlockTile extends BlockEntity implements IScreenProvider, IOneUserInteractable,
        IExtraModelDataProvider, IWaxable {

    public static final ModelDataKey<PumpkinCarvingData> CARVING = new ModelDataKey<>(PumpkinCarvingData.class);

    @Nullable
    private UUID playerWhoMayEdit = null;

    private PumpkinCarvingData data;

    public ModCarvedPumpkinBlockTile(BlockPos pos, BlockState state) {
        super(ModRegistry.MOD_CARVED_PUMPKIN_TILE.get(), pos, state);
        this.data = PumpkinCarvingData.empty(getPumpkinType());
    }

    @Override
    public @Nullable UUID getCurrentUser() {
        return playerWhoMayEdit;
    }

    @Override
    public void setCurrentUser(@Nullable UUID uuid) {
        this.playerWhoMayEdit = uuid;
    }

    public Holder<PumpkinType> getPumpkinType() {
        BlockState state = this.getBlockState();
        return ((ModCarvedPumpkinBlock) state.getBlock()).getType(state);
    }


    @Override
    public void addExtraModelData(ExtraModelData.Builder builder) {
        IExtraModelDataProvider.super.addExtraModelData(builder);
        builder.with(CARVING, data);
    }

    //I need this for when it's changed manually
    @Override
    public void setChanged() {
        if (this.level == null || this.level.isClientSide) return;
        this.level.sendBlockUpdated(this.worldPosition, this.getBlockState(), this.getBlockState(), 3);
        super.setChanged();
    }

    @Override
    public void afterDataPacket(ExtraModelData oldData) {
        refreshType();
        IExtraModelDataProvider.super.afterDataPacket(oldData);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        var ops = registries.createSerializationContext(NbtOps.INSTANCE);
        //Backwards compat
        if (tag.contains("type")) {
            var typeId = ResourceLocation.tryParse(tag.getString("type"));
            if (typeId.getNamespace().equals("minecraft")) {
                tag.putString("type", HauntedHarvest.res(typeId.getPath()).toString());
            }
        }
        var oldType = this.getPumpkinType();

        this.data = PumpkinCarvingData.CODEC.parse(ops, tag).getOrThrow();
        if (oldType != this.data.getType()) {
            this.data = this.data.withType(oldType);
        }
        //backwards compat
        if (tag.contains("Pixels")) {
            this.data = this.data.withPixels(PumpkinCarvingData.unpack(tag.getLongArray("Pixels")));
        }
        this.requestModelReload();
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        var ops = registries.createSerializationContext(NbtOps.INSTANCE);
        tag.merge((CompoundTag) PumpkinCarvingData.CODEC.encodeStart(ops, data).getOrThrow());
    }

    @Override
    protected void collectImplicitComponents(DataComponentMap.Builder components) {
        super.collectImplicitComponents(components);
        if (!this.isEmpty()) {
            components.set(ModRegistry.PUMPKIN_CARVING.get(), data);
        }
    }

    @Override
    protected void applyImplicitComponents(DataComponentInput componentInput) {
        super.applyImplicitComponents(componentInput);
        var data = componentInput.get(ModRegistry.PUMPKIN_CARVING.get());
        if (data != null) {
            this.data = data;
        } else {
            this.clearPixels();
        }
    }

    @Override
    public void removeComponentsFromTag(CompoundTag tag) {
        super.removeComponentsFromTag(tag);
        //same as in the components itself
        tag.remove("values");
        tag.remove("type");
        tag.remove("waxed");
    }


    public void clear() {
        this.data = this.data.makeCleared();
    }

    public boolean isEmpty() {
        return this.data.isEmpty();
    }

    public void clearPixels() {
        this.data = this.data.makeCleared();
    }

    public void setPixel(int x, int y, boolean b) {
        this.data = this.data.withPixel(x, y, b);
    }

    public boolean getPixel(int xx, int yy) {
        return this.data.getPixel(xx, yy);
    }

    public void setPixels(boolean[][] pixels) {
        this.data = this.data.withPixels(pixels);
    }

    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        return this.saveWithoutMetadata(registries);
    }

    public Direction getDirection() {
        return this.getBlockState().getValue(ModCarvedPumpkinBlock.FACING);
    }

    @Override
    public void openScreen(Level level, Player player, Direction direction, Vec3 hitPos) {
        CarvingScreen.open(this, direction);
    }

    @Override
    public void setWaxed(boolean b) {
        this.data = this.data.withWaxed(b);
    }

    @Override
    public boolean isWaxed() {
        return this.data.isWaxed();
    }

    public ModCarvedPumpkinBlock.CarveMode getCarveMode() {
        if (this.getPumpkinType().value().isJackOLantern()) return CommonConfigs.JACK_O_LANTERN_CARVE_MODE.get();
        return CommonConfigs.PUMPKIN_CARVE_MODE.get();
    }

    //unfortunately the type is duplicated and needs refreshing
    public void refreshType() {
        this.data = this.data.withType(getPumpkinType());
    }

    public boolean tryAcceptingClientPixels(ServerPlayer player, boolean[][] pixels, Direction dir) {

        if (!this.canBeUsedBy(this.worldPosition, player) || this.isWaxed() || !CommonConfigs.PUMPKIN_CARVE_MODE.get().canOpenGui()) {
            HauntedHarvest.LOGGER.warn("Player {} just tried to change non-editable carved pumpkin",
                    player.getName().getString());
        }
        if (this.isEmpty()) {
            level.setBlockAndUpdate(worldPosition, this.getBlockState()
                    .setValue(ModCarvedPumpkinBlock.FACING, dir));
        }
        //check if all pixels are non colored
        if (!data.hasSamePixels(pixels)) {
            level.playSound(null, this.worldPosition, SoundEvents.PUMPKIN_CARVE, SoundSource.BLOCKS, 1, 1.2f);

            this.setCurrentUser(null);
            this.setPixels(pixels);
        }
        return true;
    }

    public boolean[][] clonePixels() {
        return data.clonePixels();
    }
}
