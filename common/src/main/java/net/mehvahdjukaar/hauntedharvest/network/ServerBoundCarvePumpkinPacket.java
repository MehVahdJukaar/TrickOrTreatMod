package net.mehvahdjukaar.hauntedharvest.network;

import net.mehvahdjukaar.hauntedharvest.blocks.ModCarvedPumpkinBlockTile;
import net.mehvahdjukaar.moonlight.api.platform.network.ChannelHandler;
import net.mehvahdjukaar.moonlight.api.platform.network.Message;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.Level;

import java.util.Objects;

public class ServerBoundCarvePumpkinPacket implements Message {
    private final BlockPos pos;
    private final long[] pixels;

    public ServerBoundCarvePumpkinPacket(FriendlyByteBuf buf) {
        this.pos = buf.readBlockPos();
        this.pixels = buf.readLongArray();
    }

    public ServerBoundCarvePumpkinPacket(BlockPos pos, boolean[][] pixels) {
        this.pos = pos;
        this.pixels = ModCarvedPumpkinBlockTile.packPixels(pixels);
    }

    @Override
    public void writeToBuffer(FriendlyByteBuf buf) {
        buf.writeBlockPos(this.pos);
        buf.writeLongArray(this.pixels);
    }

    @Override
    public void handle(ChannelHandler.Context context) {

        // server world
        Level world = Objects.requireNonNull(context.getSender()).level();

        BlockPos pos = this.pos;
        if (world.getBlockEntity(pos) instanceof ModCarvedPumpkinBlockTile board) {
            world.playSound(null, this.pos, SoundEvents.PUMPKIN_CARVE, SoundSource.BLOCKS, 1, 1.2f);
            board.setPixels(ModCarvedPumpkinBlockTile.unpackPixels(this.pixels));
            //updates client
            //set changed also sends a block update
            board.setChanged();
        }
    }
}