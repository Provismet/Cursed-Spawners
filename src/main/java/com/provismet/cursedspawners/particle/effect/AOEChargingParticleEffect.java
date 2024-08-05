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
import org.joml.Vector3f;

public record AOEChargingParticleEffect (int maxAge, Vector3f colour) implements ParticleEffect {
    public static final MapCodec<AOEChargingParticleEffect> CODEC = RecordCodecBuilder.mapCodec(
        instance -> instance.group(
            Codecs.POSITIVE_INT.fieldOf("duration").forGetter(AOEChargingParticleEffect::maxAge),
            Codecs.VECTOR_3F.fieldOf("colour").forGetter(AOEChargingParticleEffect::colour)
        ).apply(instance, AOEChargingParticleEffect::new)
    );

    public static final PacketCodec<RegistryByteBuf, AOEChargingParticleEffect> PACKET_CODEC = PacketCodec.tuple(
        PacketCodecs.INTEGER,
        AOEChargingParticleEffect::maxAge,
        PacketCodecs.VECTOR3F,
        AOEChargingParticleEffect::colour,
        AOEChargingParticleEffect::new
    );

    @Override
    public ParticleType<AOEChargingParticleEffect> getType () {
        return CSParticleTypes.AOE_CHARGING_INDICATOR;
    }
}
