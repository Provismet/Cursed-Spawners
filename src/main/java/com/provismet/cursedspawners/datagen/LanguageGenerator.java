package com.provismet.cursedspawners.datagen;

import com.provismet.cursedspawners.utility.CSGamerules;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.minecraft.registry.RegistryWrapper;

import java.util.concurrent.CompletableFuture;

public class LanguageGenerator extends FabricLanguageProvider {
    protected LanguageGenerator (FabricDataOutput dataOutput, CompletableFuture<RegistryWrapper.WrapperLookup> registryLookup) {
        super(dataOutput, registryLookup);
    }

    @Override
    public void generateTranslations (RegistryWrapper.WrapperLookup registryLookup, TranslationBuilder translationBuilder) {
        translationBuilder.add("gamerule.category.cursed-spawners", "Cursed Spawners");
        translationBuilder.add(CSGamerules.MIMIC_CHANCE.getTranslationKey(), "Spawner Mimic Chance");
        translationBuilder.add(CSGamerules.MIMIC_CHANCE.getTranslationKey() + ".description", "The base odds of a Spawner becoming a mimic when broken. Overridden by NBT data when applicable.");
    }
}
