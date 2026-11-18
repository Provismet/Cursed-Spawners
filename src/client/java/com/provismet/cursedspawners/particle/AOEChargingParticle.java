package com.provismet.cursedspawners.particle;

import com.provismet.cursedspawners.particle.effect.AOEChargingParticleEffect;
import com.provismet.lilylib.particle.FlatParticle;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import org.jetbrains.annotations.Nullable;

public class AOEChargingParticle extends FlatParticle {
    private float prevScale;
    private final float maxScale;

    protected AOEChargingParticle (ClientLevel clientWorld, double x, double y, double z, SpriteSet spriteProvider, AOEChargingParticleEffect effect) {
        super(clientWorld, x, y, z, spriteProvider);
        this.quadSize = 3f;
        this.maxScale = this.quadSize;
        this.prevScale = this.quadSize;
        this.lifetime = effect.maxAge();
        this.alpha = 0;
        this.rCol = effect.colour().x();
        this.gCol = effect.colour().y();
        this.bCol = effect.colour().z();
    }

    @Override
    public void tick () {
        super.tick();
        this.prevScale = this.quadSize;
        this.quadSize = this.maxScale * (1 - (float)this.age / (float)this.lifetime);

        if (this.alpha < 1) this.alpha += 0.1f;
        if (this.alpha > 1) this.alpha = 1f;
    }

    @Override
    public float getQuadSize (float tickDelta) {
        return Mth.lerp(tickDelta, this.prevScale, this.quadSize);
    }

    public static class Factory implements ParticleProvider<AOEChargingParticleEffect> {
        private final SpriteSet spriteProvider;

        public Factory (SpriteSet spriteProvider) {
            this.spriteProvider = spriteProvider;
        }

        @Nullable
        @Override
        public Particle createParticle (AOEChargingParticleEffect parameters, ClientLevel world, double x, double y, double z, double velocityX, double velocityY, double velocityZ, RandomSource random) {
            return new AOEChargingParticle(world, x, y, z, this.spriteProvider, parameters);
        }
    }
}
