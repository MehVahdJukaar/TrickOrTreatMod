package net.mehvahdjukaar.hauntedharvest.network;

import net.mehvahdjukaar.moonlight.api.platform.network.ChannelHandler;
import net.mehvahdjukaar.moonlight.api.platform.network.Message;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;

public class ClientBoundCopyCarvingCommand implements Message {

    public final String copy;

    public ClientBoundCopyCarvingCommand(String copy) {
        this.copy = copy;
    }

    public ClientBoundCopyCarvingCommand(FriendlyByteBuf buf){
        this.copy = buf.readUtf();
    }

    @Override
    public void writeToBuffer(FriendlyByteBuf buf) {
        buf.writeUtf(copy);
    }

    @Override
    public void handle(ChannelHandler.Context context) {
        Minecraft.getInstance().keyboardHandler.setClipboard(copy);
    }
}
