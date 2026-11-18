package com.provismet.cursedspawners.datagen;

import com.provismet.cursedspawners.registries.CSEntityTypes;
import com.provismet.cursedspawners.registries.CSItems;
import com.provismet.cursedspawners.utility.CSGamerules;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.minecraft.core.HolderLookup;
import java.util.concurrent.CompletableFuture;

public class LanguageGenerator extends FabricLanguageProvider {
    protected LanguageGenerator (FabricPackOutput dataOutput, CompletableFuture<HolderLookup.Provider> registryLookup) {
        super(dataOutput, registryLookup);
    }

    @Override
    public void generateTranslations (HolderLookup.Provider registryLookup, TranslationBuilder translationBuilder) {
        translationBuilder.add("gamerule.category.cursed-spawners", "Cursed Spawners");
        translationBuilder.add(CSGamerules.MIMIC_CHANCE.getDescriptionId(), "Mimic Chance");
        translationBuilder.add(CSGamerules.MIMIC_CHANCE.getDescriptionId() + ".description", "The base odds of a Spawner becoming a mimic when broken. Overridden by NBT data when applicable.");
        translationBuilder.add(CSGamerules.SPAWNER_ACTION_CHANCE.getDescriptionId(), "Bonus Action Chance");
        translationBuilder.add(CSGamerules.SPAWNER_ACTION_CHANCE.getDescriptionId() + ".description", "The probability of a spawner having an addition effect. Effects can stack. Spawners will not attempt to generate actions if this is zero.");
        translationBuilder.add(CSGamerules.BREAK_SPEED.getDescriptionId(), "Mining Speed Modifier");
        translationBuilder.add(CSGamerules.BREAK_SPEED.getDescriptionId() + ".description", "Mining speed for spawners is multiplied by this value.");

        translationBuilder.add(CSEntityTypes.SPAWNER_MIMIC, "Spawner Mimic");

        translationBuilder.add(CSItems.MIMIC_SPAWN_EGG, "Spawner Mimic Spawn Egg");

        translationBuilder.add("subtitles.entity.spawner_mimic.hurt", "Spawner Mimic cries");
        translationBuilder.add("subtitles.entity.spawner_mimic.ambient", "Spawner Mimic whistles");
        translationBuilder.add("subtitles.entity.spawner_mimic.death", "Spawner Mimic dies");
        translationBuilder.add("subtitles.entity.spawner_mimic.attack", "Spawner Mimic attacks");
        translationBuilder.add("subtitles.entity.spawner_mimic.step", "Spawner Mimic steps");

        translationBuilder.add("subtitles.block.spawner.knockback", "Spawner bursts");
        translationBuilder.add("subtitles.block.spawner.heal", "Spawner heals");
        translationBuilder.add("subtitles.block.spawner.boost", "Spawner accelerates");
    }
}
