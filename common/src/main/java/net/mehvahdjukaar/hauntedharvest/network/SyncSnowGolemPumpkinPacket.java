package net.mehvahdjukaar.hauntedharvest.network;

import net.mehvahdjukaar.candlelight.api.ClientOnly;
import net.mehvahdjukaar.hauntedharvest.HauntedHarvest;
import net.mehvahdjukaar.hauntedharvest.entity.ICustomPumpkinHolder;
import net.mehvahdjukaar.moonlight.api.platform.network.Message;
import net.mehvahdjukaar.moonlight.api.platform.network.NetworkHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;

public record SyncSnowGolemPumpkinPacket(int entityID, ItemStack pumpkin) implements Message {

    public static final TypeAndCodec<RegistryFriendlyByteBuf, SyncSnowGolemPumpkinPacket> CODEC = Message.makeType(
            HauntedHarvest.res("sync_equipped_quiver"), SyncSnowGolemPumpkinPacket::new);

    public SyncSnowGolemPumpkinPacket(RegistryFriendlyByteBuf buf) {
        this(buf.readVarInt(), ItemStack.OPTIONAL_STREAM_CODEC.decode(buf));
    }

    public <A extends Entity & ICustomPumpkinHolder> SyncSnowGolemPumpkinPacket(A entity) {
        this(entity, entity);
    }

    public SyncSnowGolemPumpkinPacket(Entity entity, ICustomPumpkinHolder qe) {
        this(entity.getId(), qe.hauntedharvest$getCustomPumpkin());
    }

    @Override
    public void write(RegistryFriendlyByteBuf buf) {
        buf.writeVarInt(this.entityID);
        ItemStack.OPTIONAL_STREAM_CODEC.encode(buf, pumpkin);
    }

    @Override
    public void handle(Context context) {
        //client received packet
        if (context.getDirection() == NetworkDir.SERVER_BOUND) {
            //relay actual status to client
            Entity e = context.getPlayer().level().getEntity(entityID);
            if (e instanceof ICustomPumpkinHolder qe && !qe.hauntedharvest$getCustomPumpkin().isEmpty()) {
                NetworkHelper.sendToAllClientPlayersTrackingEntity(e, new SyncSnowGolemPumpkinPacket(e, qe));
            }
        } else handleSyncPumpkin(this);
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return CODEC.type();
    }


    @ClientOnly
    public void handleSyncPumpkin(SyncSnowGolemPumpkinPacket message) {
        var l = Minecraft.getInstance().level;
        Entity e = l.getEntity(message.entityID());
        if (e instanceof ICustomPumpkinHolder qe) {
            qe.hauntedharvest$setCustomPumpkin(message.pumpkin);
        }
    }

}
