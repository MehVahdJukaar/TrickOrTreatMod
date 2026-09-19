package net.mehvahdjukaar.hauntedharvest.client.model;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.client.model.npc.VillagerModel;

public class HalloweenMaskModel extends VillagerModel {

    public HalloweenMaskModel(ModelPart root) {
        super(root);
    }

    public static LayerDefinition createMesh() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();
        PartDefinition head = root.addOrReplaceChild("head", CubeListBuilder.create(), PartPose.offset(0, 16, 0));
        head.addOrReplaceChild("mask", CubeListBuilder.create().texOffs(32, 0)
                .addBox(-4, -9, -4, 8, 10, 8, new CubeDeformation(0.3f, -0.7f, -0.2f)), PartPose.ZERO);
        root.addOrReplaceChild("arms", CubeListBuilder.create(), PartPose.ZERO);
        root.addOrReplaceChild("right_leg", CubeListBuilder.create(), PartPose.ZERO);
        root.addOrReplaceChild("left_leg", CubeListBuilder.create(), PartPose.ZERO);
        return LayerDefinition.create(mesh, 64, 64);
    }
}
