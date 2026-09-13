package net.mehvahdjukaar.hauntedharvest.integration.platform;

//TODO: body commented out until quark has a 26.1.2 build
public class QuarkCompatImpl {

    public static void init() {
        //NeoForge.EVENT_BUS.register(QuarkCompatImpl.class);
    }

    //@SubscribeEvent
    //public static void onSimpleHarvest(SimpleHarvestEvent event) {
    //    Block b = event.blockState.getBlock();
    //    if (b instanceof AbstractCornBlock c) {
    //        if (!c.isPlantFullyGrown(event.blockState, event.pos, event.level)) {
    //            event.setCanceled(true);
    //        } else event.setTargetPos(event.pos.below(c.getHeight()));
    //    }
    //}
}
