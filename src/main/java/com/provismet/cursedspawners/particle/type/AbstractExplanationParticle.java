package com.provismet.cursedspawners.particle.type;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.SimpleAnimatedParticle;
import net.minecraft.client.particle.SpriteSet;

public abstract class AbstractExplanationParticle extends SimpleAnimatedParticle {
    protected AbstractExplanationParticle (ClientLevel world, double x, double y, double z, SpriteSet spriteProvider, float upwardsAcceleration) {
        super(world, x, y, z, spriteProvider, upwardsAcceleration);
        this.xd = 0;
        this.yd = 0.1;
        this.zd = 0;
        this.lifetime = 40;
        this.friction = 0.75f;
        this.quadSize = 0.25f;
        this.setSpriteFromAge(spriteProvider);
    }
}
