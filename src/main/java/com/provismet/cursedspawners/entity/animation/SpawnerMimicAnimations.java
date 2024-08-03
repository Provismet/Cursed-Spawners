package com.provismet.cursedspawners.entity.animation;

import net.minecraft.client.render.entity.animation.Animation;
import net.minecraft.client.render.entity.animation.AnimationHelper;
import net.minecraft.client.render.entity.animation.Keyframe;
import net.minecraft.client.render.entity.animation.Transformation;

public class SpawnerMimicAnimations {
	public static final Animation IDLE = Animation.Builder.create(10.0F).looping()
		.addBoneAnimation("body", new Transformation(Transformation.Targets.TRANSLATE,
			new Keyframe(0.0F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
			new Keyframe(5.0F, AnimationHelper.createTranslationalVector(0.0F, 1.0F, 0.0F), Transformation.Interpolations.LINEAR),
			new Keyframe(10.0F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR)
		))
		.build();

	public static final Animation WALK = Animation.Builder.create(2.0F).looping()
		.addBoneAnimation("legNW", new Transformation(Transformation.Targets.ROTATE, 
			new Keyframe(0.0F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
			new Keyframe(0.25F, AnimationHelper.createRotationalVector(-5.0F, 0.0F, -3.5F), Transformation.Interpolations.CUBIC),
			new Keyframe(0.5F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
			new Keyframe(1.0F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
			new Keyframe(1.25F, AnimationHelper.createRotationalVector(-5.0F, 0.0F, -3.5F), Transformation.Interpolations.CUBIC),
			new Keyframe(1.5F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
			new Keyframe(2.0F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC)
		))
		.addBoneAnimation("legNW", new Transformation(Transformation.Targets.TRANSLATE, 
			new Keyframe(0.0F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 1.0F), Transformation.Interpolations.CUBIC),
			new Keyframe(0.25F, AnimationHelper.createTranslationalVector(0.0F, 1.0F, -2.0F), Transformation.Interpolations.CUBIC),
			new Keyframe(0.5F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, -3.0F), Transformation.Interpolations.CUBIC),
			new Keyframe(0.75F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 1.0F), Transformation.Interpolations.CUBIC),
			new Keyframe(1.0F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 1.0F), Transformation.Interpolations.CUBIC),
			new Keyframe(1.25F, AnimationHelper.createTranslationalVector(0.0F, 1.0F, -2.0F), Transformation.Interpolations.CUBIC),
			new Keyframe(1.5F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, -3.0F), Transformation.Interpolations.CUBIC),
			new Keyframe(1.75F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 1.0F), Transformation.Interpolations.CUBIC),
			new Keyframe(2.0F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 1.0F), Transformation.Interpolations.CUBIC)
		))
		.addBoneAnimation("legSW", new Transformation(Transformation.Targets.ROTATE, 
			new Keyframe(0.0F, AnimationHelper.createRotationalVector(-5.0F, 0.0F, -3.5F), Transformation.Interpolations.CUBIC),
			new Keyframe(0.25F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
			new Keyframe(0.75F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
			new Keyframe(1.0F, AnimationHelper.createRotationalVector(-5.0F, 0.0F, -3.5F), Transformation.Interpolations.CUBIC),
			new Keyframe(1.25F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
			new Keyframe(1.75F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
			new Keyframe(2.0F, AnimationHelper.createRotationalVector(-5.0F, 0.0F, -3.5F), Transformation.Interpolations.CUBIC)
		))
		.addBoneAnimation("legSW", new Transformation(Transformation.Targets.TRANSLATE, 
			new Keyframe(0.0F, AnimationHelper.createTranslationalVector(0.0F, 1.0F, -1.0F), Transformation.Interpolations.CUBIC),
			new Keyframe(0.25F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, -1.75F), Transformation.Interpolations.CUBIC),
			new Keyframe(0.5F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 2.0F), Transformation.Interpolations.CUBIC),
			new Keyframe(0.75F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 2.0F), Transformation.Interpolations.CUBIC),
			new Keyframe(1.0F, AnimationHelper.createTranslationalVector(0.0F, 1.0F, -1.0F), Transformation.Interpolations.CUBIC),
			new Keyframe(1.25F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, -1.75F), Transformation.Interpolations.CUBIC),
			new Keyframe(1.5F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 2.0F), Transformation.Interpolations.CUBIC),
			new Keyframe(1.75F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 2.0F), Transformation.Interpolations.CUBIC),
			new Keyframe(2.0F, AnimationHelper.createTranslationalVector(0.0F, 1.0F, -1.0F), Transformation.Interpolations.CUBIC)
		))
		.addBoneAnimation("legSE", new Transformation(Transformation.Targets.ROTATE, 
			new Keyframe(0.25F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
			new Keyframe(0.5F, AnimationHelper.createRotationalVector(-5.0F, 0.0F, 3.5F), Transformation.Interpolations.CUBIC),
			new Keyframe(0.75F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
			new Keyframe(1.25F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
			new Keyframe(1.5F, AnimationHelper.createRotationalVector(-5.0F, 0.0F, 3.5F), Transformation.Interpolations.CUBIC),
			new Keyframe(1.75F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC)
		))
		.addBoneAnimation("legSE", new Transformation(Transformation.Targets.TRANSLATE, 
			new Keyframe(0.0F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 2.0F), Transformation.Interpolations.CUBIC),
			new Keyframe(0.25F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 2.0F), Transformation.Interpolations.CUBIC),
			new Keyframe(0.5F, AnimationHelper.createTranslationalVector(0.0F, 1.0F, -1.0F), Transformation.Interpolations.CUBIC),
			new Keyframe(0.75F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, -1.75F), Transformation.Interpolations.CUBIC),
			new Keyframe(1.0F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 2.0F), Transformation.Interpolations.CUBIC),
			new Keyframe(1.25F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 2.0F), Transformation.Interpolations.CUBIC),
			new Keyframe(1.5F, AnimationHelper.createTranslationalVector(0.0F, 1.0F, -1.0F), Transformation.Interpolations.CUBIC),
			new Keyframe(1.75F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, -1.75F), Transformation.Interpolations.CUBIC),
			new Keyframe(2.0F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 2.0F), Transformation.Interpolations.CUBIC)
		))
		.addBoneAnimation("legNE", new Transformation(Transformation.Targets.ROTATE, 
			new Keyframe(0.0F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
			new Keyframe(0.5F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
			new Keyframe(0.75F, AnimationHelper.createRotationalVector(-5.0F, 0.0F, 3.5F), Transformation.Interpolations.CUBIC),
			new Keyframe(1.0F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
			new Keyframe(1.5F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
			new Keyframe(1.75F, AnimationHelper.createRotationalVector(-5.0F, 0.0F, 3.5F), Transformation.Interpolations.CUBIC),
			new Keyframe(2.0F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC)
		))
		.addBoneAnimation("legNE", new Transformation(Transformation.Targets.TRANSLATE, 
			new Keyframe(0.0F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, -3.0F), Transformation.Interpolations.CUBIC),
			new Keyframe(0.25F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 1.0F), Transformation.Interpolations.CUBIC),
			new Keyframe(0.5F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 1.0F), Transformation.Interpolations.CUBIC),
			new Keyframe(0.75F, AnimationHelper.createTranslationalVector(0.0F, 1.0F, -2.0F), Transformation.Interpolations.CUBIC),
			new Keyframe(1.0F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, -3.0F), Transformation.Interpolations.CUBIC),
			new Keyframe(1.25F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 1.0F), Transformation.Interpolations.CUBIC),
			new Keyframe(1.5F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 1.0F), Transformation.Interpolations.CUBIC),
			new Keyframe(1.75F, AnimationHelper.createTranslationalVector(0.0F, 1.0F, -2.0F), Transformation.Interpolations.CUBIC),
			new Keyframe(2.0F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, -3.0F), Transformation.Interpolations.CUBIC)
		))
		.addBoneAnimation("body", new Transformation(Transformation.Targets.ROTATE, 
			new Keyframe(0.0F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 1.0F), Transformation.Interpolations.CUBIC),
			new Keyframe(0.5F, AnimationHelper.createRotationalVector(1.0F, 0.0F, 1.0F), Transformation.Interpolations.CUBIC),
			new Keyframe(1.0F, AnimationHelper.createRotationalVector(1.0F, 0.0F, -1.0F), Transformation.Interpolations.CUBIC),
			new Keyframe(1.5F, AnimationHelper.createRotationalVector(0.0F, 0.0F, -1.0F), Transformation.Interpolations.CUBIC),
			new Keyframe(2.0F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 1.0F), Transformation.Interpolations.CUBIC)
		))
		.build();

	public static final Animation ATTACK = Animation.Builder.create(0.3333F)
		.addBoneAnimation("body", new Transformation(Transformation.Targets.ROTATE,
			new Keyframe(0.0F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
			new Keyframe(0.0833F, AnimationHelper.createRotationalVector(5.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
			new Keyframe(0.1667F, AnimationHelper.createRotationalVector(27.5F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
			new Keyframe(0.3333F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC)
		))
		.addBoneAnimation("body", new Transformation(Transformation.Targets.TRANSLATE,
			new Keyframe(0.0F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
			new Keyframe(0.0833F, AnimationHelper.createTranslationalVector(0.0F, -1.0F, 1.0F), Transformation.Interpolations.CUBIC),
			new Keyframe(0.1667F, AnimationHelper.createTranslationalVector(0.0F, 5.0F, -3.0F), Transformation.Interpolations.CUBIC),
			new Keyframe(0.3333F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC)
		))
		.addBoneAnimation("legSW", new Transformation(Transformation.Targets.ROTATE,
			new Keyframe(0.0F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
			new Keyframe(0.1667F, AnimationHelper.createRotationalVector(20.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
			new Keyframe(0.3333F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC)
		))
		.addBoneAnimation("legSW", new Transformation(Transformation.Targets.TRANSLATE,
			new Keyframe(0.0F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
			new Keyframe(0.0833F, AnimationHelper.createTranslationalVector(1.0F, 1.0F, -1.0F), Transformation.Interpolations.CUBIC),
			new Keyframe(0.1667F, AnimationHelper.createTranslationalVector(2.0F, 2.0F, -4.0F), Transformation.Interpolations.CUBIC),
			new Keyframe(0.25F, AnimationHelper.createTranslationalVector(1.0F, 1.0F, -1.0F), Transformation.Interpolations.CUBIC),
			new Keyframe(0.3333F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC)
		))
		.addBoneAnimation("legSE", new Transformation(Transformation.Targets.ROTATE,
			new Keyframe(0.0F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
			new Keyframe(0.1667F, AnimationHelper.createRotationalVector(20.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
			new Keyframe(0.3333F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC)
		))
		.addBoneAnimation("legSE", new Transformation(Transformation.Targets.TRANSLATE,
			new Keyframe(0.0F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
			new Keyframe(0.0833F, AnimationHelper.createTranslationalVector(-1.0F, 1.0F, -1.0F), Transformation.Interpolations.CUBIC),
			new Keyframe(0.1667F, AnimationHelper.createTranslationalVector(-2.0F, 2.0F, -4.0F), Transformation.Interpolations.CUBIC),
			new Keyframe(0.25F, AnimationHelper.createTranslationalVector(-1.0F, 1.0F, -1.0F), Transformation.Interpolations.CUBIC),
			new Keyframe(0.3333F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC)
		))
		.build();

	public static final Animation SPAWN = Animation.Builder.create(1.0833F)
		.addBoneAnimation("legNW", new Transformation(Transformation.Targets.TRANSLATE, 
			new Keyframe(0.25F, AnimationHelper.createTranslationalVector(-3.0F, 0.0F, 2.0F), Transformation.Interpolations.CUBIC),
			new Keyframe(0.4167F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC)
		))
		.addBoneAnimation("legNW", new Transformation(Transformation.Targets.SCALE, 
			new Keyframe(0.25F, AnimationHelper.createScalingVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
			new Keyframe(0.4167F, AnimationHelper.createScalingVector(1.0F, 1.0F, 1.0F), Transformation.Interpolations.CUBIC)
		))
		.addBoneAnimation("legSW", new Transformation(Transformation.Targets.TRANSLATE, 
			new Keyframe(0.25F, AnimationHelper.createTranslationalVector(-2.0F, 0.0F, -2.0F), Transformation.Interpolations.CUBIC),
			new Keyframe(0.7917F, AnimationHelper.createTranslationalVector(-2.0F, 0.0F, -2.0F), Transformation.Interpolations.CUBIC),
			new Keyframe(0.875F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC)
		))
		.addBoneAnimation("legSW", new Transformation(Transformation.Targets.SCALE, 
			new Keyframe(0.25F, AnimationHelper.createScalingVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
			new Keyframe(0.7917F, AnimationHelper.createScalingVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
			new Keyframe(0.875F, AnimationHelper.createScalingVector(1.0F, 1.0F, 1.0F), Transformation.Interpolations.CUBIC)
		))
		.addBoneAnimation("legSE", new Transformation(Transformation.Targets.TRANSLATE, 
			new Keyframe(0.25F, AnimationHelper.createTranslationalVector(2.0F, 0.0F, -2.0F), Transformation.Interpolations.CUBIC),
			new Keyframe(0.7917F, AnimationHelper.createTranslationalVector(2.0F, 0.0F, -2.0F), Transformation.Interpolations.CUBIC),
			new Keyframe(0.875F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC)
		))
		.addBoneAnimation("legSE", new Transformation(Transformation.Targets.SCALE, 
			new Keyframe(0.25F, AnimationHelper.createScalingVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
			new Keyframe(0.7917F, AnimationHelper.createScalingVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
			new Keyframe(0.875F, AnimationHelper.createScalingVector(1.0F, 1.0F, 1.0F), Transformation.Interpolations.CUBIC)
		))
		.addBoneAnimation("legNE", new Transformation(Transformation.Targets.TRANSLATE, 
			new Keyframe(0.25F, AnimationHelper.createTranslationalVector(2.0F, 0.0F, 2.0F), Transformation.Interpolations.CUBIC),
			new Keyframe(0.5F, AnimationHelper.createTranslationalVector(2.0F, 0.0F, 2.0F), Transformation.Interpolations.CUBIC),
			new Keyframe(0.5833F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC)
		))
		.addBoneAnimation("legNE", new Transformation(Transformation.Targets.SCALE, 
			new Keyframe(0.25F, AnimationHelper.createScalingVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
			new Keyframe(0.5F, AnimationHelper.createScalingVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
			new Keyframe(0.5833F, AnimationHelper.createScalingVector(1.0F, 1.0F, 1.0F), Transformation.Interpolations.CUBIC)
		))
		.addBoneAnimation("body", new Transformation(Transformation.Targets.ROTATE, 
			new Keyframe(0.25F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
			new Keyframe(0.4167F, AnimationHelper.createRotationalVector(-5.0F, 0.0F, -2.5F), Transformation.Interpolations.CUBIC),
			new Keyframe(0.5F, AnimationHelper.createRotationalVector(-5.0F, 0.0F, -2.5F), Transformation.Interpolations.CUBIC),
			new Keyframe(0.5833F, AnimationHelper.createRotationalVector(-7.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
			new Keyframe(0.7917F, AnimationHelper.createRotationalVector(-7.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
			new Keyframe(0.875F, AnimationHelper.createRotationalVector(2.5F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
			new Keyframe(0.9583F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC)
		))
		.addBoneAnimation("body", new Transformation(Transformation.Targets.TRANSLATE, 
			new Keyframe(0.25F, AnimationHelper.createTranslationalVector(0.0F, -3.0F, 0.0F), Transformation.Interpolations.CUBIC),
			new Keyframe(0.4167F, AnimationHelper.createTranslationalVector(0.0F, -2.0F, 0.0F), Transformation.Interpolations.CUBIC),
			new Keyframe(0.5F, AnimationHelper.createTranslationalVector(0.0F, -2.0F, 0.0F), Transformation.Interpolations.CUBIC),
			new Keyframe(0.7917F, AnimationHelper.createTranslationalVector(0.0F, -2.15F, 0.0F), Transformation.Interpolations.CUBIC),
			new Keyframe(0.875F, AnimationHelper.createTranslationalVector(0.0F, -1.21F, 0.0F), Transformation.Interpolations.CUBIC),
			new Keyframe(0.9583F, AnimationHelper.createTranslationalVector(0.0F, -2.0F, 0.0F), Transformation.Interpolations.CUBIC),
			new Keyframe(1.0833F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC)
		))
		.build();
}