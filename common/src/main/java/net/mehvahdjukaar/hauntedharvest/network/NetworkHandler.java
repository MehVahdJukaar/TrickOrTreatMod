package net.mehvahdjukaar.hauntedharvest.network;

import net.mehvahdjukaar.hauntedharvest.HauntedHarvest;
import net.mehvahdjukaar.moonlight.api.platform.network.ChannelHandler;
import net.mehvahdjukaar.moonlight.api.platform.network.NetworkDir;

public class NetworkHandler {

    public static final ChannelHandler CHANNEL = ChannelHandler.createChannel(HauntedHarvest.res("network"));


    public static void registerMessages() {

        CHANNEL.register(NetworkDir.PLAY_TO_SERVER,
                ServerBoundCarvePumpkinPacket.class, ServerBoundCarvePumpkinPacket::new);

        CHANNEL.register(NetworkDir.PLAY_TO_CLIENT,
                ClientBoundCopyCarvingCommand.class, ClientBoundCopyCarvingCommand::new);

    }

}