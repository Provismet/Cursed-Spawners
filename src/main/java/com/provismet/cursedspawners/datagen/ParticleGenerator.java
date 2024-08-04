package com.provismet.cursedspawners.datagen;

import com.provismet.cursedspawners.CursedSpawnersMain;
import com.provismet.cursedspawners.registries.CSParticleTypes;
import com.provismet.lilylib.datagen.provider.LilyParticleTextureProvider;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.minecraft.registry.RegistryWrapper;

import java.util.concurrent.CompletableFuture;

public class ParticleGenerator extends LilyParticleTextureProvider {
    protected ParticleGenerator (FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registryLookup) {
        super(output, registryLookup);
    }

    @Override
    protected void generate (RegistryWrapper.WrapperLookup registryLookup, ParticleWriter writer) {
        writer.add(CSParticleTypes.WIND_CHARGING_INDICATOR, CursedSpawnersMain.identifier("wind_charging_indicator_1"));
    }
}
