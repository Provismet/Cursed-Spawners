package com.provismet.cursedspawners.datagen;

import com.provismet.cursedspawners.CursedSpawnersMain;
import com.provismet.cursedspawners.registries.CSParticleTypes;
import com.provismet.lilylib.datagen.provider.LilyParticleTextureProvider;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class ParticleGenerator extends LilyParticleTextureProvider {
    protected ParticleGenerator (FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registryLookup) {
        super(output, registryLookup);
    }

    @Override
    protected void generate (RegistryWrapper.WrapperLookup registryLookup, ParticleWriter writer) {
        writer.add(CSParticleTypes.AOE_CHARGING_INDICATOR, CursedSpawnersMain.identifier("aoe_charging_indicator_1"));
        writer.add(CSParticleTypes.HEAL, CursedSpawnersMain.identifier("heal"));

        List<Identifier> speed = new ArrayList<>();
        for (int i = 1; i <= 20; ++i) {
            speed.add(CursedSpawnersMain.identifier("speed_" + i));
        }
        writer.add(CSParticleTypes.BOOST, speed);
    }
}
