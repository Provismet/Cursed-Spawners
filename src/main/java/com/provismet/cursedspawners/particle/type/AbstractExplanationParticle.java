package com.provismet.cursedspawners.particle.type;

import net.minecraft.client.particle.AnimatedParticle;
import net.minecraft.client.particle.SpriteProvider;
import net.minecraft.client.world.ClientWorld;

public abstract class AbstractExplanationParticle extends AnimatedParticle {
    protected AbstractExplanationParticle (ClientWorld world, double x, double y, double z, SpriteProvider spriteProvider, float upwardsAcceleration) {
        super(world, x, y, z, spriteProvider, upwardsAcceleration);
        this.velocityX = 0;
        this.velocityY = 0.1;
        this.velocityZ = 0;
        this.maxAge = 40;
        this.velocityMultiplier = 0.75f;
        this.scale = 0.25f;
        this.setSpriteForAge(spriteProvider);
    }
}
