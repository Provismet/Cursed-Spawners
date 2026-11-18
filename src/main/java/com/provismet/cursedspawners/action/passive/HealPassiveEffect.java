package com.provismet.cursedspawners.action.passive;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.provismet.cursedspawners.particle.effect.AOEChargingParticleEffect;
import com.provismet.cursedspawners.registries.CursedSpawnersParticleTypes;
import com.provismet.cursedspawners.registries.CursedSpawnersSoundEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.ARGB;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public record HealPassiveEffect (int interval, double radius, float amount) implements SpawnerPassiveEffect {
    public static final MapCodec<HealPassiveEffect> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
        Codec.INT.fieldOf("interval").forGetter(HealPassiveEffect::interval),
        Codec.DOUBLE.fieldOf("radius").forGetter(HealPassiveEffect::radius),
        Codec.FLOAT.fieldOf("amount").forGetter(HealPassiveEffect::amount)
    ).apply(instance, HealPassiveEffect::new));

    public static HealPassiveEffect fromRandom (RandomSource random) {
        return new HealPassiveEffect(
            random.nextIntBetweenInclusive(80, 160),
            random.triangle(5, 1),
            2f
        );
    }

    @Override
    public void tick (ServerLevel level, BlockPos spawnerPos) {
        Vec3 centrePos = Vec3.atCenterOf(spawnerPos);

        if (level.getGameTime() % this.interval == 0) {
            level.sendParticles(CursedSpawnersParticleTypes.HEAL, centrePos.x(), centrePos.y() + 0.5, centrePos.z(), 1, 0, 0, 0, 0);
            List<Monster> mobs = level.getEntitiesOfClass(Monster.class, AABB.ofSize(centrePos, this.radius, 3, this.radius), mob -> true);
            for (Monster hostile : mobs) {
                hostile.heal(this.amount);
                level.sendParticles(ParticleTypes.HAPPY_VILLAGER, hostile.getX(), hostile.getEyeY(), hostile.getZ(), 8, 0.35, 0.35, 0.35, 0);
            }
            level.playSound(null, spawnerPos, CursedSpawnersSoundEvents.BLOCK_SPAWNER_HEAL, SoundSource.BLOCKS, 1, 1);
        }
        else if (level.getGameTime() % this.interval == this.interval / 2) {
            level.sendParticles(new AOEChargingParticleEffect(this.interval / 2, ARGB.vector3fFromRGB24(0x47BC78)), centrePos.x(), spawnerPos.getY() + 0.025, centrePos.z(), 1, 0, 0, 0, 0);
        }
    }

    @Override
    @NotNull
    public MapCodec<HealPassiveEffect> getCodec () {
        return CODEC;
    }
}
