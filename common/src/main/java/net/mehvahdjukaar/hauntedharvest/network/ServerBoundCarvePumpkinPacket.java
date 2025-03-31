package net.mehvahdjukaar.hauntedharvest.network;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.DecoderException;
import net.mehvahdjukaar.hauntedharvest.HauntedHarvest;
import net.mehvahdjukaar.hauntedharvest.blocks.ModCarvedPumpkinBlock;
import net.mehvahdjukaar.hauntedharvest.blocks.ModCarvedPumpkinBlockTile;
import net.mehvahdjukaar.moonlight.api.platform.network.Message;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.VarInt;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;

import java.util.Objects;

public class ServerBoundCarvePumpkinPacket implements Message {

    public static final TypeAndCodec<RegistryFriendlyByteBuf, ServerBoundCarvePumpkinPacket> TYPE =
            Message.makeType(HauntedHarvest.res("carve_pumpkin"), ServerBoundCarvePumpkinPacket::new);


    private final BlockPos pos;
    private final boolean[][] pixels;
    private final Direction dir;

    public ServerBoundCarvePumpkinPacket(RegistryFriendlyByteBuf buf) {
        this.pos = buf.readBlockPos();
        this.dir = Direction.from2DDataValue(buf.readVarInt());
        this.pixels = new boolean[16][16];
        for (int i = 0; i < this.pixels.length; i++) {
            this.pixels[i] = readBoolArray(buf);
        }
    }

    private static boolean[] readBoolArray(ByteBuf buffer) {
        int i = VarInt.read(buffer);
        int maxSize = buffer.readableBytes();
        if (i > maxSize) {
            throw new DecoderException("ByteArray with size " + i + " is bigger than allowed " + maxSize);
        } else {
            boolean[] bs = new boolean[i];
            for (int j = 0; j < i; j++) {
                bs[j] = buffer.readBoolean();
            }
            return bs;
        }
    }

    public ServerBoundCarvePumpkinPacket(BlockPos pos, boolean[][] pixels, Direction dir) {
        this.pos = pos;
        this.pixels = pixels;
        this.dir = dir;
    }


    @Override
    public void write(RegistryFriendlyByteBuf buf) {
        buf.writeBlockPos(this.pos);
        buf.writeVarInt(this.dir.get2DDataValue());
        //manually writes this. be sure it matches the read one
        for (boolean[] pixel : this.pixels) {
            writeBoolArray(buf, pixel);
        }
    }

    private static void writeBoolArray(ByteBuf buffer, boolean[] bs) {
        VarInt.write(buffer, bs.length);
        for (boolean b : bs) {
            buffer.writeBoolean(b);
        }
    }

    @Override
    public void handle(Context context) {
        // server world
        if(context.getPlayer() instanceof ServerPlayer player) {
            Level level = player.level();

            BlockPos pos = this.pos;
            if (level.hasChunkAt(pos) && level.getBlockEntity(pos) instanceof ModCarvedPumpkinBlockTile pumpkin) {
                if (pumpkin.tryAcceptingClientPixels(player, this.pixels, this.dir)) {

                    pumpkin.setChanged();
                    level.gameEvent(player, GameEvent.BLOCK_CHANGE, pos);
                }
            }
        }
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE.type();
    }
}