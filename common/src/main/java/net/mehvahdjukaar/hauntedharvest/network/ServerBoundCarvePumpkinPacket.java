package net.mehvahdjukaar.hauntedharvest.network;

import net.mehvahdjukaar.hauntedharvest.HauntedHarvest;
import net.mehvahdjukaar.hauntedharvest.blocks.ModCarvedPumpkinBlock;
import net.mehvahdjukaar.hauntedharvest.blocks.ModCarvedPumpkinBlockTile;
import net.mehvahdjukaar.moonlight.api.platform.network.ChannelHandler;
import net.mehvahdjukaar.moonlight.api.platform.network.Message;
import net.mehvahdjukaar.moonlight.api.util.Utils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.PumpkinBlock;

import java.util.Objects;

public class ServerBoundCarvePumpkinPacket implements Message {
    private final BlockPos pos;
    private final long[] pixels;
    private final Direction dir;

    public ServerBoundCarvePumpkinPacket(FriendlyByteBuf buf) {
        this.pos = buf.readBlockPos();
        this.pixels = buf.readLongArray();
        this.dir = Direction.from2DDataValue(buf.readVarInt());
    }

    public ServerBoundCarvePumpkinPacket(BlockPos pos, boolean[][] pixels, Direction dir) {
        this.pos = pos;
        this.pixels = ModCarvedPumpkinBlockTile.packPixels(pixels);
        this.dir = dir;
    }

    @Override
    public void writeToBuffer(FriendlyByteBuf buf) {
        buf.writeBlockPos(this.pos);
        buf.writeLongArray(this.pixels);
        buf.writeVarInt(this.dir.get2DDataValue());
    }

    @Override
    public void handle(ChannelHandler.Context context) {

        // server world
        Level level = Objects.requireNonNull(context.getSender()).level();

        BlockPos pos = this.pos;
        if (level.getBlockEntity(pos) instanceof ModCarvedPumpkinBlockTile pumpkin) {
            if(pumpkin.isEmpty()){
                level.setBlockAndUpdate(pos, pumpkin.getBlockState().setValue(ModCarvedPumpkinBlock.FACING, dir));
            }
            level.playSound(null, this.pos, SoundEvents.PUMPKIN_CARVE, SoundSource.BLOCKS, 1, 1.2f);
            pumpkin.setPixels(ModCarvedPumpkinBlockTile.unpackPixels(this.pixels));
            //updates client
            //set changed also sends a block update
            pumpkin.setChanged();
        }
    }
}