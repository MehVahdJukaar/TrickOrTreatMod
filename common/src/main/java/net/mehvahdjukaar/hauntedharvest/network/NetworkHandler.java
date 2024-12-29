package net.mehvahdjukaar.hauntedharvest.network;

import net.mehvahdjukaar.moonlight.api.platform.network.NetworkHelper;

public class NetworkHandler {


    public static void init() {
        NetworkHelper.addNetworkRegistration(NetworkHandler::registerMessages, 0);
    }

    private static void registerMessages(NetworkHelper.RegisterMessagesEvent event) {

        event.registerServerBound(ServerBoundCarvePumpkinPacket.TYPE);
        event.registerClientBound(ClientBoundCopyCarvingCommand.TYPE);
    }

}