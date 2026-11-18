package com.provismet.cursedspawners.registries;

import com.provismet.cursedspawners.particle.AOEChargingParticle;
import com.provismet.cursedspawners.particle.BoostParticle;
import com.provismet.cursedspawners.particle.HealParticle;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.particle.v1.ParticleProviderRegistry;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;

@Environment(EnvType.CLIENT)
public abstract class CSParticleFactories {
    private static <T extends ParticleOptions> void register (ParticleType<T> particle, ParticleProviderRegistry.PendingParticleProvider<T> constructor) {
        ParticleProviderRegistry.getInstance().register(particle, constructor);
    }

    public static void register () {
        register(CSParticleTypes.AOE_CHARGING_INDICATOR, AOEChargingParticle.Factory::new);
        register(CSParticleTypes.HEAL, HealParticle.Factory::new);
        register(CSParticleTypes.BOOST, BoostParticle.Factory::new);
    }
}
