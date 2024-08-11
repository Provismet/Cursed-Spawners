package com.provismet.datagen.cursedspawners;

import com.provismet.cursedspawners.registries.CSItems;
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
        translationBuilder.add(CSGamerules.MIMIC_CHANCE.getTranslationKey(), "Mimic Chance");
        translationBuilder.add(CSGamerules.MIMIC_CHANCE.getTranslationKey() + ".description", "The base odds of a Spawner becoming a mimic when broken. Overridden by NBT data when applicable.");
        translationBuilder.add(CSGamerules.SPAWNER_ACTION_CHANCE.getTranslationKey(), "Bonus Action Chance");
        translationBuilder.add(CSGamerules.SPAWNER_ACTION_CHANCE.getTranslationKey() + ".description", "The probability of a spawner having an addition effect. Effects can stack. Spawners will not attempt to generate actions if this is zero.");
        translationBuilder.add(CSGamerules.BREAK_SPEED.getTranslationKey(), "Mining Speed Modifier");
        translationBuilder.add(CSGamerules.BREAK_SPEED.getTranslationKey() + ".description", "Mining speed for spawners is multiplied by this value.");

        translationBuilder.add(CSItems.MIMIC_SPAWN_EGG, "Spawner Mimic Spawn Egg");

        translationBuilder.add("subtitle.mimic.hurt", "Spawner Mimic cries");
        translationBuilder.add("subtitle.mimic.ambient", "Spawner Mimic whistles");
        translationBuilder.add("subtitle.mimic.death", "Spawner Mimic dies");
    }
}
