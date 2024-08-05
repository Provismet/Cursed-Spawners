package com.provismet.cursedspawners.particle.type;

import com.provismet.cursedspawners.particle.effect.AOEChargingParticleEffect;
import com.provismet.lilylib.particle.FlatParticle;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleFactory;
import net.minecraft.client.particle.SpriteProvider;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.Nullable;

public class AOEChargingParticle extends FlatParticle {
    private float prevScale;
    private final float maxScale;

    protected AOEChargingParticle (ClientWorld clientWorld, double x, double y, double z, SpriteProvider spriteProvider, AOEChargingParticleEffect effect) {
        super(clientWorld, x, y, z, spriteProvider);
        this.scale = 3f;
        this.maxScale = this.scale;
        this.prevScale = this.scale;
        this.maxAge = effect.maxAge();
        this.alpha = 0;
        this.red = effect.colour().x();
        this.green = effect.colour().y();
        this.blue = effect.colour().z();
    }

    @Override
    public void tick () {
        super.tick();
        this.prevScale = this.scale;
        this.scale = this.maxScale * (1 - (float)this.age / (float)this.maxAge);

        if (this.alpha < 1) this.alpha += 0.1f;
        if (this.alpha > 1) this.alpha = 1f;
    }

    @Override
    public float getSize (float tickDelta) {
        return MathHelper.lerp(tickDelta, this.prevScale, this.scale);
    }

    public static class Factory implements ParticleFactory<AOEChargingParticleEffect> {
        private final SpriteProvider spriteProvider;

        public Factory (SpriteProvider spriteProvider) {
            this.spriteProvider = spriteProvider;
        }

        @Nullable
        @Override
        public Particle createParticle (AOEChargingParticleEffect parameters, ClientWorld world, double x, double y, double z, double velocityX, double velocityY, double velocityZ) {
            return new AOEChargingParticle(world, x, y, z, this.spriteProvider, parameters);
        }
    }
}
