package com.provismet.cursedspawners.registries;

import com.provismet.cursedspawners.CursedSpawnersMain;
import com.provismet.cursedspawners.particle.effect.WindChargingParticleEffect;
import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

public abstract class CSParticleTypes {
    public static final ParticleType<WindChargingParticleEffect> WIND_CHARGING_INDICATOR = FabricParticleTypes.complex(WindChargingParticleEffect.CODEC, WindChargingParticleEffect.PACKET_CODEC);

    private static <T extends ParticleEffect> void register (ParticleType<T> particle, String name) {
        Registry.register(Registries.PARTICLE_TYPE, CursedSpawnersMain.identifier(name), particle);
    }

    public static void register () {
        register(WIND_CHARGING_INDICATOR, "wind_charging_indicator");
    }
}
