package net.mehvahdjukaar.hauntedharvest.blocks;

import net.mehvahdjukaar.hauntedharvest.client.CarvingManager.Key;
import net.mehvahdjukaar.hauntedharvest.client.gui.CarvingGui;
import net.mehvahdjukaar.hauntedharvest.configs.CommonConfigs;
import net.mehvahdjukaar.hauntedharvest.reg.ModRegistry;
import net.mehvahdjukaar.moonlight.api.block.IOwnerProtected;
import net.mehvahdjukaar.moonlight.api.client.IScreenProvider;
import net.mehvahdjukaar.moonlight.api.client.model.ExtraModelData;
import net.mehvahdjukaar.moonlight.api.client.model.IExtraModelDataProvider;
import net.mehvahdjukaar.moonlight.api.client.model.ModelDataKey;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class ModCarvedPumpkinBlockTile extends BlockEntity implements IOwnerProtected,
        IScreenProvider, IExtraModelDataProvider {

    public static final ModelDataKey<Key> CARVING = new ModelDataKey<>(Key.class);

    private UUID owner = null;
    private boolean waxed = false;
    private boolean[][] pixels = new boolean[16][16];

    //client side
    private Key textureKey = null;

    public ModCarvedPumpkinBlockTile(BlockPos pos, BlockState state) {
        super(ModRegistry.MOD_CARVED_PUMPKIN_TILE.get(), pos, state);
        this.clear();
    }

    public PumpkinType getPumpkinType() {
        BlockState state = this.getBlockState();
        return ((ModCarvedPumpkinBlock) state.getBlock()).getType(state);
    }
    @Override
    public ExtraModelData getExtraModelData() {
        return ExtraModelData.builder()
                .with(CARVING, getTextureKey())
                .build();
    }

    public Key getTextureKey() {
        if (textureKey == null) refreshTextureKey();
        return textureKey;
    }

    public void refreshTextureKey() {
        this.textureKey = Key.of(packPixels(this.pixels), this.getPumpkinType());
    }

    @Override
    public void afterDataPacket(ExtraModelData oldData) {
        refreshTextureKey();
        IExtraModelDataProvider.super.afterDataPacket(oldData);
    }

    //I need this for when it's changed manually
    @Override
    public void setChanged() {
        if (this.level == null || this.level.isClientSide) return;
        this.level.sendBlockUpdated(this.worldPosition, this.getBlockState(), this.getBlockState(), 3);
        super.setChanged();
    }

    @Override
    public void load(CompoundTag compound) {
        super.load(compound);
        this.loadOwner(compound);
        this.waxed = compound.contains("Waxed") && compound.getBoolean("Waxed");
        acceptPixels(compound.getLongArray("Pixels"));
    }

    public void acceptPixels(long[] p) {
        this.pixels = new boolean[16][16];
        if (p.length != 0) {
            this.pixels = unpackPixels(p);
        }
    }

    @Override
    public void saveAdditional(CompoundTag compound) {
        super.saveAdditional(compound);
        this.savePixels(compound);
        this.saveOwner(compound);
    }

    public CompoundTag savePixels(CompoundTag compound) {
        if (this.waxed) compound.putBoolean("Waxed", true);
        compound.putLongArray("Pixels", packPixels(pixels));
        return compound;
    }

    public static long[] packPixels(boolean[][] pixels) {
        long[] packed = new long[4];
        long n = 0;
        int ind = 0;
        for (int a = 0; a < pixels.length; a++) {
            int s = 0;
            for (int i = 0; i < pixels.length; i++) {
                s = (s | ((toShort(pixels[a][i]) & 1) << i));
            }
            n = n | (long) s << ((a % 4) * 16);
            if ((a + 1) % 4 == 0) {
                packed[ind] = n;
                n = 0;
                ind++;
            }
        }
        return packed;
    }

    private static short toShort(boolean b) {
        return (short) (b ? 1 : 0);
    }

    private static boolean toBoolean(short b) {
        return b == 1;
    }


    public static boolean[][] unpackPixels(long[] packed) {
        boolean[][] bytes = new boolean[16][16];
        int k = 0;
        for (long l : packed) {
            for (int j = 0; j < 4; j++) {
                for (int i = 0; i < 16; i++) {
                    bytes[k][i] = toBoolean((short) ((l >> (i + j * 16)) & 1));
                }
                k++;
            }
        }
        return bytes;
    }

    public void clear() {
        for (int x = 0; x < pixels.length; x++) {
            for (int y = 0; y < pixels[x].length; y++) {
                this.pixels[x][y] = false;
            }
        }
    }

    public boolean isEmpty() {
        for (boolean[] pixel : pixels) {
            for (boolean b : pixel) {
                if (b) return false;
            }
        }
        return true;
    }

    public void setPixel(int x, int y, boolean b) {
        this.pixels[x][y] = b;
    }

    public boolean getPixel(int xx, int yy) {
        return this.pixels[xx][yy];
    }

    public void setPixels(boolean[][] pixels) {
        this.pixels = pixels;
    }

    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag getUpdateTag() {
        return this.saveWithoutMetadata();
    }

    public Direction getDirection() {
        return this.getBlockState().getValue(ModCarvedPumpkinBlock.FACING);
    }

    @Nullable
    @Override
    public UUID getOwner() {
        return owner;
    }

    @Override
    public void setOwner(UUID owner) {
        this.owner = owner;
    }

    @Override
    public void openScreen(Level level, BlockPos pos, Player player) {
        CarvingGui.open(this);
    }


    public void setWaxed(boolean b) {
        this.waxed = b;
    }

    public boolean isWaxed() {
        return this.waxed;
    }

    public ModCarvedPumpkinBlock.CarveMode getCarveMode() {
        if (this.getPumpkinType().isGlowing()) return CommonConfigs.JACK_O_LANTERN_CARVE_MODE.get();
        return CommonConfigs.PUMPKIN_CARVE_MODE.get();
    }

    public ItemStack getItemWithNBT() {
        ItemStack itemstack = new ItemStack(this.getBlockState().getBlock());
        if (!this.isEmpty()) {
            CompoundTag tag = this.savePixels(new CompoundTag());
            if (!tag.isEmpty()) {
                itemstack.addTagElement("BlockEntityTag", tag);
            }
        }
        return itemstack;
    }

}
