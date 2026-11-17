package com.provismet.cursedspawners.particle.effect;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.provismet.cursedspawners.registries.CSParticleTypes;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ExtraCodecs;
import org.joml.Vector3fc;

public record AOEChargingParticleEffect (int maxAge, Vector3fc colour) implements ParticleOptions {
    public static final MapCodec<AOEChargingParticleEffect> CODEC = RecordCodecBuilder.mapCodec(
        instance -> instance.group(
            ExtraCodecs.POSITIVE_INT.fieldOf("duration").forGetter(AOEChargingParticleEffect::maxAge),
            ExtraCodecs.VECTOR3F.fieldOf("colour").forGetter(AOEChargingParticleEffect::colour)
        ).apply(instance, AOEChargingParticleEffect::new)
    );

    public static final StreamCodec<RegistryFriendlyByteBuf, AOEChargingParticleEffect> PACKET_CODEC = StreamCodec.composite(
        ByteBufCodecs.INT,
        AOEChargingParticleEffect::maxAge,
        ByteBufCodecs.VECTOR3F,
        AOEChargingParticleEffect::colour,
        AOEChargingParticleEffect::new
    );

    @Override
    public ParticleType<AOEChargingParticleEffect> getType () {
        return CSParticleTypes.AOE_CHARGING_INDICATOR;
    }
}
