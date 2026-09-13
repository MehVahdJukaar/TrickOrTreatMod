package net.mehvahdjukaar.hauntedharvest.client.model;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.world.entity.LivingEntity;

public class SkinwalkerModel<T extends LivingEntity> extends HumanoidModel<T> {
    public boolean carrying;
    public boolean creepy;

    public SkinwalkerModel(ModelPart root) {
        super(root);
    }

    public static LayerDefinition createBodyLayer() {
        float f = -14.0F;

        MeshDefinition meshDefinition = HumanoidModel.createMesh(CubeDeformation.NONE, -14.0F);
        PartDefinition partDefinition = meshDefinition.getRoot();
        PartPose partPose = PartPose.offset(0.0F, -13.0F, 0.0F);
        partDefinition.addOrReplaceChild("hat", CubeListBuilder.create()
                        .texOffs(0, 44)
                        .addBox(-6.0F, -10.0F, -1.0F, 2.0F, 8.0F, 2.0F)
                        .addBox(6.0F, -10.0F, -1.0F, 2.0F, 8.0F, 2.0F),
                partPose);
        partDefinition.addOrReplaceChild("head", CubeListBuilder.create()
                .texOffs(0, 28)
                .addBox(-5.0F, -8.0F, -4.5F, 10F, 8.0F, 9.0F),
                partPose);
        partDefinition.addOrReplaceChild("body", CubeListBuilder.create()
                .texOffs(0, 0)
                .addBox(-4.0F, 0.0F, -2.0F, 12.0F, 21.0F, 7.0F),
                PartPose.offset(0.0F, -14.0F, 0.0F));
        partDefinition.addOrReplaceChild("right_arm", CubeListBuilder.create().texOffs(56, 0).addBox(-1.0F, -2.0F, -1.0F, 2.0F, 30.0F, 2.0F), PartPose.offset(-5.0F, -12.0F, 0.0F));
        partDefinition.addOrReplaceChild("left_arm", CubeListBuilder.create().texOffs(56, 0).mirror().addBox(-1.0F, -2.0F, -1.0F, 2.0F, 30.0F, 2.0F), PartPose.offset(5.0F, -12.0F, 0.0F));
        partDefinition.addOrReplaceChild("right_leg", CubeListBuilder.create().texOffs(56, 0).addBox(-1.0F, 0.0F, -1.0F, 2.0F, 30.0F, 2.0F), PartPose.offset(-2.0F, -5.0F, 0.0F));
        partDefinition.addOrReplaceChild("left_leg", CubeListBuilder.create().texOffs(56, 0).mirror().addBox(-1.0F, 0.0F, -1.0F, 2.0F, 30.0F, 2.0F), PartPose.offset(2.0F, -5.0F, 0.0F));
        return LayerDefinition.create(meshDefinition, 128, 64);
    }

    public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        super.setupAnim(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
        this.head.visible = true;
        this.body.xRot = 0.0F;
        this.body.y = -14.0F;
        this.body.z = -0.0F;
        ModelPart var10000 = this.rightLeg;
        var10000.xRot -= 0.0F;
        var10000 = this.leftLeg;
        var10000.xRot -= 0.0F;
        var10000 = this.rightArm;
        var10000.xRot *= 0.5F;
        var10000 = this.leftArm;
        var10000.xRot *= 0.5F;
        var10000 = this.rightLeg;
        var10000.xRot *= 0.5F;
        var10000 = this.leftLeg;
        var10000.xRot *= 0.5F;
        float f = 0.4F;
        if (this.rightArm.xRot > 0.4F) {
            this.rightArm.xRot = 0.4F;
        }

        if (this.leftArm.xRot > 0.4F) {
            this.leftArm.xRot = 0.4F;
        }

        if (this.rightArm.xRot < -0.4F) {
            this.rightArm.xRot = -0.4F;
        }

        if (this.leftArm.xRot < -0.4F) {
            this.leftArm.xRot = -0.4F;
        }

        if (this.rightLeg.xRot > 0.4F) {
            this.rightLeg.xRot = 0.4F;
        }

        if (this.leftLeg.xRot > 0.4F) {
            this.leftLeg.xRot = 0.4F;
        }

        if (this.rightLeg.xRot < -0.4F) {
            this.rightLeg.xRot = -0.4F;
        }

        if (this.leftLeg.xRot < -0.4F) {
            this.leftLeg.xRot = -0.4F;
        }

        if (this.carrying) {
            this.rightArm.xRot = -0.5F;
            this.leftArm.xRot = -0.5F;
            this.rightArm.zRot = 0.05F;
            this.leftArm.zRot = -0.05F;
        }

        this.rightLeg.z = 0.0F;
        this.leftLeg.z = 0.0F;
        this.rightLeg.y = -5.0F;
        this.leftLeg.y = -5.0F;
        this.head.z = -0.0F;
        this.head.y = -13.0F;
        this.hat.x = this.head.x;
        this.hat.y = this.head.y;
        this.hat.z = this.head.z;
        this.hat.xRot = this.head.xRot;
        this.hat.yRot = this.head.yRot;
        this.hat.zRot = this.head.zRot;
        if (this.creepy) {
            float g = 1.0F;
            var10000 = this.head;
            var10000.y -= 5.0F;
        }

        this.rightArm.setPos(-5.0F, -12.0F, 0.0F);
        this.leftArm.setPos(5.0F, -12.0F, 0.0F);
    }
}