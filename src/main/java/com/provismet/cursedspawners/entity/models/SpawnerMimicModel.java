package com.provismet.cursedspawners.entity.models;

import com.provismet.cursedspawners.entity.animation.SpawnerMimicAnimations;
import com.provismet.cursedspawners.entity.renderers.states.SpawnerMimicRenderState;
import net.minecraft.client.model.Dilation;
import net.minecraft.client.model.ModelData;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.model.ModelPartBuilder;
import net.minecraft.client.model.ModelPartData;
import net.minecraft.client.model.ModelTransform;
import net.minecraft.client.model.TexturedModelData;
import net.minecraft.client.render.entity.model.EntityModel;

public class SpawnerMimicModel extends EntityModel<SpawnerMimicRenderState> {
	public SpawnerMimicModel (ModelPart root) {
        super(root.getChild("root"));
	}
	public static TexturedModelData getTexturedModelData () {
		ModelData modelData = new ModelData();
		ModelPartData modelPartData = modelData.getRoot();
		ModelPartData root = modelPartData.addChild("root", ModelPartBuilder.create(), ModelTransform.origin(0.0F, 24.0F, 0.0F));

		root.addChild("body", ModelPartBuilder.create().uv(0, 32).cuboid(-8.0F, -19.0F, -8.0F, 16.0F, 16.0F, 16.0F, new Dilation(0.0F)), ModelTransform.origin(0.0F, 0.0F, 0.0F));

		root.addChild("legNW", ModelPartBuilder.create().uv(46, 16).mirrored().cuboid(-2.0F, -3.0F, -2.0F, 4.0F, 5.0F, 4.0F, new Dilation(0.0F)).mirrored(false)
		.uv(48, 40).cuboid(-2.0F, 2.0F, -3.0F, 5.0F, 1.0F, 1.0F, new Dilation(0.0F))
		.uv(42, 27).cuboid(2.0F, 2.0F, -2.0F, 1.0F, 1.0F, 4.0F, new Dilation(0.0F))
		.uv(56, 33).cuboid(2.0F, -3.0F, -3.0F, 1.0F, 5.0F, 1.0F, new Dilation(0.0F)), ModelTransform.origin(8.0F, -3.0F, -8.0F));

		root.addChild("legSW", ModelPartBuilder.create().uv(38, 16).cuboid(-2.0F, -3.0F, -2.0F, 4.0F, 5.0F, 4.0F, new Dilation(0.0F))
		.uv(48, 46).cuboid(-2.0F, 2.0F, 2.0F, 5.0F, 1.0F, 1.0F, new Dilation(0.0F))
		.uv(32, 27).cuboid(2.0F, 2.0F, -2.0F, 1.0F, 1.0F, 4.0F, new Dilation(0.0F))
		.uv(60, 30).cuboid(2.0F, -3.0F, 2.0F, 1.0F, 5.0F, 1.0F, new Dilation(0.0F)), ModelTransform.origin(8.0F, -3.0F, 8.0F));

		root.addChild("legSE", ModelPartBuilder.create().uv(38, 16).mirrored().cuboid(-2.0F, -3.0F, -2.0F, 4.0F, 5.0F, 4.0F, new Dilation(0.0F)).mirrored(false)
		.uv(48, 44).cuboid(-3.0F, 2.0F, 2.0F, 5.0F, 1.0F, 1.0F, new Dilation(0.0F))
		.uv(48, 28).cuboid(-3.0F, 2.0F, -2.0F, 1.0F, 1.0F, 4.0F, new Dilation(0.0F))
		.uv(60, 36).cuboid(-3.0F, -3.0F, 2.0F, 1.0F, 5.0F, 1.0F, new Dilation(0.0F)), ModelTransform.origin(-8.0F, -3.0F, 8.0F));

		root.addChild("legNE", ModelPartBuilder.create().uv(46, 16).cuboid(-2.0F, -3.0F, -2.0F, 4.0F, 5.0F, 4.0F, new Dilation(0.0F))
		.uv(48, 42).cuboid(-3.0F, 2.0F, -3.0F, 5.0F, 1.0F, 1.0F, new Dilation(0.0F))
		.uv(48, 35).cuboid(-3.0F, 2.0F, -2.0F, 1.0F, 1.0F, 4.0F, new Dilation(0.0F))
		.uv(60, 42).cuboid(-3.0F, -3.0F, -3.0F, 1.0F, 5.0F, 1.0F, new Dilation(0.0F)), ModelTransform.origin(-8.0F, -3.0F, -8.0F));
		return TexturedModelData.of(modelData, 64, 64);
	}

	@Override
	public void setAngles (SpawnerMimicRenderState state) {
		super.setAngles(state);

		this.animateWalking(SpawnerMimicAnimations.WALK, state.limbSwingAnimationProgress, state.limbSwingAmplitude, 3f, 50f);
		this.animate(state.idleState, SpawnerMimicAnimations.IDLE, state.age);
		this.animate(state.attackState, SpawnerMimicAnimations.ATTACK, state.age);
		this.animate(state.spawnState, SpawnerMimicAnimations.SPAWN, state.age);
	}
}