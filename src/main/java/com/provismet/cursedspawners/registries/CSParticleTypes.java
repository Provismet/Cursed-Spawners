package com.provismet.cursedspawners.registries;

import com.provismet.cursedspawners.CursedSpawnersMain;
import com.provismet.cursedspawners.particle.effect.AOEChargingParticleEffect;
import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleType;
import net.minecraft.particle.SimpleParticleType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

public abstract class CSParticleTypes {
    public static final ParticleType<AOEChargingParticleEffect> AOE_CHARGING_INDICATOR = FabricParticleTypes.complex(AOEChargingParticleEffect.CODEC, AOEChargingParticleEffect.PACKET_CODEC);
    public static final SimpleParticleType HEAL = FabricParticleTypes.simple();
    public static final SimpleParticleType BOOST = FabricParticleTypes.simple();

    private static <T extends ParticleEffect> void register (ParticleType<T> particle, String name) {
        Registry.register(Registries.PARTICLE_TYPE, CursedSpawnersMain.identifier(name), particle);
    }

    public static void register () {
        register(AOE_CHARGING_INDICATOR, "aoe_charging_indicator");
        register(HEAL, "heal");
        register(BOOST, "boost");
    }
}
