package net.mehvahdjukaar.hauntedharvest.integration;

import net.mehvahdjukaar.hauntedharvest.blocks.PumpkinType;
import net.mehvahdjukaar.polytone.api.VoxelVolumeApi;
import net.minecraft.world.item.BlockItem;

public class PolytoneCompat {

    public static void setupClient() {
        VoxelVolumeApi.registerCallback(VoxelVolumeApi.Activation.ONLY_WITH_COLORED_LIGHT_PACKS, event -> {
            for (var type : PumpkinType.REGISTRY) {
                if (type.getPumpkin() != type.getVanillaPumpkin()) {
                    event.addBlockAlias(type.getPumpkin(), type.getVanillaPumpkin());
                } else if (type.getTorch() instanceof BlockItem bi) {
                    event.addBlockAlias(type.getPumpkin(), bi.getBlock());
                }
            }
        });
    }
}
