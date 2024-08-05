package com.provismet.cursedspawners.registries.client;

import com.provismet.cursedspawners.particle.type.AOEChargingParticle;
import com.provismet.cursedspawners.particle.type.BoostParticle;
import com.provismet.cursedspawners.particle.type.HealParticle;
import com.provismet.cursedspawners.registries.CSParticleTypes;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleType;

@Environment(EnvType.CLIENT)
public abstract class CSParticleFactories {
    private static <T extends ParticleEffect> void register (ParticleType<T> particle, ParticleFactoryRegistry.PendingParticleFactory<T> constructor) {
        ParticleFactoryRegistry.getInstance().register(particle, constructor);
    }

    public static void register () {
        register(CSParticleTypes.AOE_CHARGING_INDICATOR, AOEChargingParticle.Factory::new);
        register(CSParticleTypes.HEAL, HealParticle.Factory::new);
        register(CSParticleTypes.BOOST, BoostParticle.Factory::new);
    }
}
