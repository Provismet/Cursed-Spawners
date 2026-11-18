package com.provismet.cursedspawners.action.passive;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.provismet.cursedspawners.particle.effect.AOEChargingParticleEffect;
import com.provismet.cursedspawners.registries.CSParticleTypes;
import com.provismet.cursedspawners.registries.CSSoundEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.ARGB;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public record SpeedBoostEffect (int interval, double radius, int duration) implements SpawnerPassiveEffect {
    public static final MapCodec<SpeedBoostEffect> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
        Codec.INT.fieldOf("interval").forGetter(SpeedBoostEffect::interval),
        Codec.DOUBLE.fieldOf("radius").forGetter(SpeedBoostEffect::radius),
        Codec.INT.fieldOf("duration").forGetter(SpeedBoostEffect::duration)
    ).apply(instance, SpeedBoostEffect::new));

    public static SpeedBoostEffect fromRandom (RandomSource random) {
        return new SpeedBoostEffect(
            random.nextIntBetweenInclusive(120, 200),
            random.triangle(8, 4),
            random.nextIntBetweenInclusive(20, 60)
        );
    }

    @Override
    public void tick (ServerLevel level, BlockPos spawnerPos) {
        Vec3 centrePos = Vec3.atCenterOf(spawnerPos);

        if (level.getGameTime() % this.interval == 0) {
            level.sendParticles(CSParticleTypes.BOOST, centrePos.x(), centrePos.y() + 0.5, centrePos.z(), 1, 0, 0, 0, 0);
            List<Monster> mobs = level.getEntitiesOfClass(Monster.class, AABB.ofSize(centrePos, this.radius, 3, this.radius), mob -> true);
            for (Monster hostile : mobs) {
                hostile.addEffect(new MobEffectInstance(MobEffects.SPEED, 30, 1));
                level.sendParticles(ParticleTypes.HAPPY_VILLAGER, hostile.getX(), hostile.getEyeY(), hostile.getZ(), 8, 0.35, 0.35, 0.35, 0);
            }
            level.playSound(null, spawnerPos, CSSoundEvents.BLOCK_SPAWNER_BOOST, SoundSource.BLOCKS, 1, 1);
        }
        else if (level.getGameTime() % this.interval == this.interval / 2) {
            level.sendParticles(new AOEChargingParticleEffect(this.interval / 2, ARGB.vector3fFromRGB24(0xFF8459)), centrePos.x(), spawnerPos.getY() + 0.025, centrePos.z(), 1, 0, 0, 0, 0);
        }
    }

    @Override
    @NotNull
    public MapCodec<SpeedBoostEffect> getCodec () {
        return CODEC;
    }
}
