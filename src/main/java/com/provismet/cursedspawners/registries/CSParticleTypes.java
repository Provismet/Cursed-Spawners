package com.provismet.cursedspawners.registries;

import com.provismet.cursedspawners.CursedSpawnersMain;
import com.provismet.cursedspawners.particle.effect.AOEChargingParticleEffect;
import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes;
import net.minecraft.core.Registry;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.BuiltInRegistries;

public abstract class CSParticleTypes {
    public static final ParticleType<AOEChargingParticleEffect> AOE_CHARGING_INDICATOR = FabricParticleTypes.complex(AOEChargingParticleEffect.CODEC, AOEChargingParticleEffect.PACKET_CODEC);
    public static final SimpleParticleType HEAL = FabricParticleTypes.simple();
    public static final SimpleParticleType BOOST = FabricParticleTypes.simple();

    private static <T extends ParticleOptions> void register (ParticleType<T> particle, String name) {
        Registry.register(BuiltInRegistries.PARTICLE_TYPE, CursedSpawnersMain.identifier(name), particle);
    }

    public static void register () {
        register(AOE_CHARGING_INDICATOR, "aoe_charging_indicator");
        register(HEAL, "heal");
        register(BOOST, "boost");
    }
}
