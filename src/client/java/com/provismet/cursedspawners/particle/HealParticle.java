package com.provismet.cursedspawners.particle;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.RandomSource;

public class HealParticle extends AbstractExplanationParticle {
    protected HealParticle (ClientLevel world, double x, double y, double z, SpriteSet spriteProvider) {
        super(world, x, y, z, spriteProvider, 0f);
    }

    @Environment(value=EnvType.CLIENT)
    public static class Factory implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet spriteProvider;

        public Factory (SpriteSet spriteProvider) {
            this.spriteProvider = spriteProvider;
        }

        @Override
        public Particle createParticle (SimpleParticleType defaultParticleType, ClientLevel clientWorld, double x, double y, double z, double velocityX, double velocityY, double velocityZ, RandomSource random) {
            return new HealParticle(clientWorld, x, y, z, this.spriteProvider);
        }
    }
}
