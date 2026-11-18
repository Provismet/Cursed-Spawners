package com.provismet.cursedspawners.action.passive;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.provismet.cursedspawners.particle.effect.AOEChargingParticleEffect;
import com.provismet.cursedspawners.registries.CSSoundEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.ARGB;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public record KnockbackPassiveEffect (int interval, double radius, double strength) implements SpawnerPassiveEffect {
    public static final MapCodec<KnockbackPassiveEffect> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
        Codec.INT.fieldOf("interval").forGetter(KnockbackPassiveEffect::interval),
        Codec.DOUBLE.fieldOf("radius").forGetter(KnockbackPassiveEffect::radius),
        Codec.DOUBLE.fieldOf("strength").forGetter(KnockbackPassiveEffect::strength)
    ).apply(instance, KnockbackPassiveEffect::new));

    public static KnockbackPassiveEffect fromRandom (RandomSource random) {
        return new KnockbackPassiveEffect(
            random.nextIntBetweenInclusive(100, 160),
            random.triangle(5, 0.75),
            random.triangle(1.5, 0.5)
        );
    }

    @Override
    public void tick (ServerLevel level, BlockPos spawnerPos) {
        Vec3 centrePos = Vec3.atCenterOf(spawnerPos);

        if (level.getGameTime() % this.interval == 0) {
            level.sendParticles(ParticleTypes.GUST_EMITTER_SMALL, centrePos.x(), centrePos.y(), centrePos.z(), 1, 0, 0, 0, 0);
            List<ServerPlayer> players = level.getPlayers(player -> player.position().closerThan(centrePos, this.radius, this.radius) && !player.isCreative() && !player.isSpectator());
            for (ServerPlayer player : players) {
                double strength = this.strength * (1 - player.getAttributeValue(Attributes.KNOCKBACK_RESISTANCE));
                if (strength > 0) {
                    Vec3 velocity = new Vec3(player.getX() - centrePos.x(), player.getY() >= spawnerPos.getY() ? 0.5 : -0.5, player.getZ() - centrePos.z()).normalize().scale(strength);
                    player.push(velocity);
                    player.hurtMarked = true;
                }
            }
            level.playSound(null, spawnerPos, CSSoundEvents.BLOCK_SPAWNER_KNOCKBACK, SoundSource.BLOCKS, 1, 1);
        }
        else if (level.getGameTime() % this.interval == this.interval / 2) {
            level.sendParticles(new AOEChargingParticleEffect(this.interval / 2, ARGB.vector3fFromRGB24(0xE2E2E2)), centrePos.x(), spawnerPos.getY() + 0.025, centrePos.z(), 1, 0, 0, 0, 0);
        }
    }

    @Override
    @NotNull
    public MapCodec<KnockbackPassiveEffect> getCodec () {
        return CODEC;
    }
}
