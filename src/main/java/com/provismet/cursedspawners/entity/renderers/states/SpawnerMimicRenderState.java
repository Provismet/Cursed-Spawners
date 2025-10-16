package com.provismet.cursedspawners.entity.renderers.states;

import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.entity.AnimationState;

public class SpawnerMimicRenderState extends LivingEntityRenderState {
    public final AnimationState idleState = new AnimationState();
    public final AnimationState attackState = new AnimationState();
    public final AnimationState spawnState = new AnimationState();
    public EntityRenderState renderedEntityState;
    public double mobRotation;
}
