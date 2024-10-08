package com.provismet.datagen.cursedspawners;

import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;

public class CursedSpawnersDataGenerator implements DataGeneratorEntrypoint {
	@Override
	public void onInitializeDataGenerator (FabricDataGenerator fabricDataGenerator) {
		FabricDataGenerator.Pack pack = fabricDataGenerator.createPack();
		pack.addProvider(LanguageGenerator::new);
		pack.addProvider(ModelGenerator::new);
		pack.addProvider(ParticleGenerator::new);
		pack.addProvider(LootTableGenerator::new);
		pack.addProvider(SoundGenerator::new);
		pack.addProvider(EntityTypeTagGenerator::new);
	}
}
