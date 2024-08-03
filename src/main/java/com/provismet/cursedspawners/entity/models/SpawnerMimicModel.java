package com.provismet.cursedspawners.entity.models;

import com.provismet.cursedspawners.entity.SpawnerMimicEntity;
import com.provismet.cursedspawners.entity.animation.SpawnerMimicAnimations;
import net.minecraft.client.model.Dilation;
import net.minecraft.client.model.ModelData;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.model.ModelPartBuilder;
import net.minecraft.client.model.ModelPartData;
import net.minecraft.client.model.ModelTransform;
import net.minecraft.client.model.TexturedModelData;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.SinglePartEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.MathHelper;

public class SpawnerMimicModel extends SinglePartEntityModel<SpawnerMimicEntity> {
	private final ModelPart root;
	private final ModelPart body;

	public SpawnerMimicModel (ModelPart root) {
		this.root = root.getChild("root");
		this.body = this.root.getChild("body");
	}
	public static TexturedModelData getTexturedModelData () {
		ModelData modelData = new ModelData();
		ModelPartData modelPartData = modelData.getRoot();
		ModelPartData root = modelPartData.addChild("root", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 24.0F, 0.0F));

		root.addChild("body", ModelPartBuilder.create().uv(0, 32).cuboid(-8.0F, -19.0F, -8.0F, 16.0F, 16.0F, 16.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

		root.addChild("legNW", ModelPartBuilder.create().uv(46, 16).mirrored().cuboid(-2.0F, -3.0F, -2.0F, 4.0F, 5.0F, 4.0F, new Dilation(0.0F)).mirrored(false)
		.uv(48, 40).cuboid(-2.0F, 2.0F, -3.0F, 5.0F, 1.0F, 1.0F, new Dilation(0.0F))
		.uv(42, 27).cuboid(2.0F, 2.0F, -2.0F, 1.0F, 1.0F, 4.0F, new Dilation(0.0F))
		.uv(56, 33).cuboid(2.0F, -3.0F, -3.0F, 1.0F, 5.0F, 1.0F, new Dilation(0.0F)), ModelTransform.pivot(8.0F, -3.0F, -8.0F));

		root.addChild("legSW", ModelPartBuilder.create().uv(38, 16).cuboid(-2.0F, -3.0F, -2.0F, 4.0F, 5.0F, 4.0F, new Dilation(0.0F))
		.uv(48, 46).cuboid(-2.0F, 2.0F, 2.0F, 5.0F, 1.0F, 1.0F, new Dilation(0.0F))
		.uv(32, 27).cuboid(2.0F, 2.0F, -2.0F, 1.0F, 1.0F, 4.0F, new Dilation(0.0F))
		.uv(60, 30).cuboid(2.0F, -3.0F, 2.0F, 1.0F, 5.0F, 1.0F, new Dilation(0.0F)), ModelTransform.pivot(8.0F, -3.0F, 8.0F));

		root.addChild("legSE", ModelPartBuilder.create().uv(38, 16).mirrored().cuboid(-2.0F, -3.0F, -2.0F, 4.0F, 5.0F, 4.0F, new Dilation(0.0F)).mirrored(false)
		.uv(48, 44).cuboid(-3.0F, 2.0F, 2.0F, 5.0F, 1.0F, 1.0F, new Dilation(0.0F))
		.uv(48, 28).cuboid(-3.0F, 2.0F, -2.0F, 1.0F, 1.0F, 4.0F, new Dilation(0.0F))
		.uv(60, 36).cuboid(-3.0F, -3.0F, 2.0F, 1.0F, 5.0F, 1.0F, new Dilation(0.0F)), ModelTransform.pivot(-8.0F, -3.0F, 8.0F));

		root.addChild("legNE", ModelPartBuilder.create().uv(46, 16).cuboid(-2.0F, -3.0F, -2.0F, 4.0F, 5.0F, 4.0F, new Dilation(0.0F))
		.uv(48, 42).cuboid(-3.0F, 2.0F, -3.0F, 5.0F, 1.0F, 1.0F, new Dilation(0.0F))
		.uv(48, 35).cuboid(-3.0F, 2.0F, -2.0F, 1.0F, 1.0F, 4.0F, new Dilation(0.0F))
		.uv(60, 42).cuboid(-3.0F, -3.0F, -3.0F, 1.0F, 5.0F, 1.0F, new Dilation(0.0F)), ModelTransform.pivot(-8.0F, -3.0F, -8.0F));
		return TexturedModelData.of(modelData, 64, 64);
	}

	@Override
	public void setAngles (SpawnerMimicEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
		this.getPart().traverse().forEach(ModelPart::resetTransform);

		netHeadYaw = MathHelper.clamp(netHeadYaw, -30, 30);
		this.body.yaw = netHeadYaw * MathHelper.RADIANS_PER_DEGREE;

		this.animateMovement(SpawnerMimicAnimations.WALK, limbSwing, limbSwingAmount, 3f, 50f);
		this.updateAnimation(entity.idleState, SpawnerMimicAnimations.IDLE, ageInTicks);
		this.updateAnimation(entity.attackState, SpawnerMimicAnimations.ATTACK, ageInTicks);
		this.updateAnimation(entity.spawnState, SpawnerMimicAnimations.SPAWN, ageInTicks);
	}

	@Override
	public void render (MatrixStack matrices, VertexConsumer vertices, int light, int overlay, int color) {
		this.root.render(matrices, vertices, light, overlay, color);
	}

	@Override
	public ModelPart getPart () {
		return this.root;
	}
}