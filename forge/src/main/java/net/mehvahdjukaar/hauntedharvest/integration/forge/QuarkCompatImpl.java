package net.mehvahdjukaar.hauntedharvest.integration.forge;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.crafting.conditions.ICondition;
import net.minecraftforge.fml.ModList;
import org.apache.maven.artifact.versioning.DefaultArtifactVersion;

public class QuarkCompatImpl {


    public static void init() {
        var v = ModList.get().getModContainerById("quark").get().getModInfo().getVersion();
        if (v.compareTo(new DefaultArtifactVersion("3.4-390")) > 0) {
            MinecraftForge.EVENT_BUS.register(QuarkStuff2.class);
        }
    }


}
