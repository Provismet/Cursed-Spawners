package com.provismet.datagen.cursedspawners;

import com.provismet.cursedspawners.CursedSpawnersMain;
import com.provismet.cursedspawners.registries.CSParticleTypes;
import com.provismet.lilylib.datagen.provider.LilyParticleTextureProvider;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.minecraft.core.HolderLookup;
import net.minecraft.resources.Identifier;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class ParticleGenerator extends LilyParticleTextureProvider {
    protected ParticleGenerator (FabricPackOutput output, CompletableFuture<HolderLookup.Provider> registryLookup) {
        super(output, registryLookup);
    }

    @Override
    protected void generate (HolderLookup.Provider registryLookup, ParticleWriter writer) {
        writer.add(CSParticleTypes.AOE_CHARGING_INDICATOR, CursedSpawnersMain.identifier("aoe_charging_indicator_1"));
        writer.add(CSParticleTypes.HEAL, CursedSpawnersMain.identifier("heal"));

        List<Identifier> speed = new ArrayList<>();
        for (int i = 1; i <= 20; ++i) {
            speed.add(CursedSpawnersMain.identifier("speed_" + i));
        }
        writer.add(CSParticleTypes.BOOST, speed);
    }
}
