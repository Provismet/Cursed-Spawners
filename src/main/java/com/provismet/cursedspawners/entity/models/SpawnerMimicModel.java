package com.provismet.cursedspawners.entity.models;

import com.provismet.cursedspawners.entity.animation.SpawnerMimicAnimations;
import com.provismet.cursedspawners.entity.renderers.states.SpawnerMimicRenderState;
import net.minecraft.client.animation.KeyframeAnimation;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;

public class SpawnerMimicModel extends EntityModel<SpawnerMimicRenderState> {
	private final KeyframeAnimation idleAnimation;
	private final KeyframeAnimation walkAnimation;
	private final KeyframeAnimation spawnAnimation;
	private final KeyframeAnimation attackAnimation;

	public SpawnerMimicModel (ModelPart root) {
        super(root.getChild("root"));
		this.idleAnimation = SpawnerMimicAnimations.IDLE.bake(root);
		this.walkAnimation = SpawnerMimicAnimations.WALK.bake(root);
		this.spawnAnimation = SpawnerMimicAnimations.SPAWN.bake(root);
		this.attackAnimation = SpawnerMimicAnimations.ATTACK.bake(root);
	}

	public static LayerDefinition getTexturedModelData () {
		MeshDefinition modelData = new MeshDefinition();
		PartDefinition modelPartData = modelData.getRoot();
		PartDefinition root = modelPartData.addOrReplaceChild("root", CubeListBuilder.create(), PartPose.offset(0.0F, 24.0F, 0.0F));

		root.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 32).addBox(-8.0F, -19.0F, -8.0F, 16.0F, 16.0F, 16.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

		root.addOrReplaceChild("legNW", CubeListBuilder.create().texOffs(46, 16).mirror().addBox(-2.0F, -3.0F, -2.0F, 4.0F, 5.0F, 4.0F, new CubeDeformation(0.0F)).mirror(false)
		.texOffs(48, 40).addBox(-2.0F, 2.0F, -3.0F, 5.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(42, 27).addBox(2.0F, 2.0F, -2.0F, 1.0F, 1.0F, 4.0F, new CubeDeformation(0.0F))
		.texOffs(56, 33).addBox(2.0F, -3.0F, -3.0F, 1.0F, 5.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(8.0F, -3.0F, -8.0F));

		root.addOrReplaceChild("legSW", CubeListBuilder.create().texOffs(38, 16).addBox(-2.0F, -3.0F, -2.0F, 4.0F, 5.0F, 4.0F, new CubeDeformation(0.0F))
		.texOffs(48, 46).addBox(-2.0F, 2.0F, 2.0F, 5.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(32, 27).addBox(2.0F, 2.0F, -2.0F, 1.0F, 1.0F, 4.0F, new CubeDeformation(0.0F))
		.texOffs(60, 30).addBox(2.0F, -3.0F, 2.0F, 1.0F, 5.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(8.0F, -3.0F, 8.0F));

		root.addOrReplaceChild("legSE", CubeListBuilder.create().texOffs(38, 16).mirror().addBox(-2.0F, -3.0F, -2.0F, 4.0F, 5.0F, 4.0F, new CubeDeformation(0.0F)).mirror(false)
		.texOffs(48, 44).addBox(-3.0F, 2.0F, 2.0F, 5.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(48, 28).addBox(-3.0F, 2.0F, -2.0F, 1.0F, 1.0F, 4.0F, new CubeDeformation(0.0F))
		.texOffs(60, 36).addBox(-3.0F, -3.0F, 2.0F, 1.0F, 5.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(-8.0F, -3.0F, 8.0F));

		root.addOrReplaceChild("legNE", CubeListBuilder.create().texOffs(46, 16).addBox(-2.0F, -3.0F, -2.0F, 4.0F, 5.0F, 4.0F, new CubeDeformation(0.0F))
		.texOffs(48, 42).addBox(-3.0F, 2.0F, -3.0F, 5.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(48, 35).addBox(-3.0F, 2.0F, -2.0F, 1.0F, 1.0F, 4.0F, new CubeDeformation(0.0F))
		.texOffs(60, 42).addBox(-3.0F, -3.0F, -3.0F, 1.0F, 5.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(-8.0F, -3.0F, -8.0F));
		return LayerDefinition.create(modelData, 64, 64);
	}

	@Override
	public void setAngles (SpawnerMimicRenderState state) {
		super.setupAnim(state);

		this.walkAnimation.applyWalk(state.walkAnimationPos, state.walkAnimationSpeed, 3f, 50f);
		this.idleAnimation.apply(state.idleState, state.ageInTicks);
		this.attackAnimation.apply(state.attackState, state.ageInTicks);
		this.spawnAnimation.apply(state.spawnState, state.ageInTicks);
	}
}