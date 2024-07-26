package com.provismet.cursedspawners.particle.effect;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.provismet.cursedspawners.registries.CSParticleTypes;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleType;
import net.minecraft.util.dynamic.Codecs;

public record WindChargingParticleEffect (int maxAge) implements ParticleEffect {
    public static final MapCodec<WindChargingParticleEffect> CODEC = RecordCodecBuilder.mapCodec(
        instance -> instance.group(
            Codecs.POSITIVE_INT.fieldOf("duration").forGetter(WindChargingParticleEffect::maxAge)
        ).apply(instance, WindChargingParticleEffect::new)
    );

    public static final PacketCodec<RegistryByteBuf, WindChargingParticleEffect> PACKET_CODEC = PacketCodec.tuple(
        PacketCodecs.INTEGER,
        WindChargingParticleEffect::maxAge,
        WindChargingParticleEffect::new
    );

    @Override
    public ParticleType<WindChargingParticleEffect> getType () {
        return CSParticleTypes.WIND_CHARGING_INDICATOR;
    }
}
