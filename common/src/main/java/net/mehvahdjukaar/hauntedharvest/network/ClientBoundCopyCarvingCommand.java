package net.mehvahdjukaar.hauntedharvest.network;

import net.mehvahdjukaar.hauntedharvest.HauntedHarvest;
import net.mehvahdjukaar.moonlight.api.platform.network.Message;
import net.minecraft.client.Minecraft;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public class ClientBoundCopyCarvingCommand implements Message {

    public static final TypeAndCodec<RegistryFriendlyByteBuf, ClientBoundCopyCarvingCommand> TYPE =
            Message.makeType(HauntedHarvest.res("copy_carving"), ClientBoundCopyCarvingCommand::new);

    public final String copy;

    public ClientBoundCopyCarvingCommand(String copy) {
        this.copy = copy;
    }

    public ClientBoundCopyCarvingCommand(RegistryFriendlyByteBuf buf) {
        this.copy = buf.readUtf();
    }

    @Override
    public void write(RegistryFriendlyByteBuf buf) {
        buf.writeUtf(copy);
    }

    @Override
    public void handle(Context context) {
        Minecraft.getInstance().keyboardHandler.setClipboard(copy);
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE.type();
    }
}
