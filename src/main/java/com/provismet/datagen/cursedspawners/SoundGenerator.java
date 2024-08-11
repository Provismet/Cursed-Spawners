package com.provismet.datagen.cursedspawners;

import com.provismet.cursedspawners.CursedSpawnersMain;
import com.provismet.cursedspawners.registries.CSSoundEvents;
import com.provismet.lilylib.datagen.provider.LilySoundProvider;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class SoundGenerator extends LilySoundProvider {
    protected SoundGenerator (FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registryLookup) {
        super(output, registryLookup);
    }

    @Override
    protected void generateSoundFile (RegistryWrapper.WrapperLookup registryLookup, SoundWriter writer) {
        writer.add(CSSoundEvents.ENTITY_MIMIC_HURT, "subtitles.entity.spawner_mimic.hurt", this.getNames(3, "mob/spawner_mimic/mimic_hurt"));
        writer.add(CSSoundEvents.ENTITY_MIMIC_AMBIENT, "subtitles.entity.spawner_mimic.ambient", this.getNames(3, "mob/spawner_mimic/mimic_ambient"));
        writer.add(CSSoundEvents.ENTITY_MIMIC_DEATH, "subtitles.entity.spawner_mimic.death", CursedSpawnersMain.identifier("mob/spawner_mimic/mimic_death"));
        writer.add(CSSoundEvents.ENTITY_MIMIC_STEP, "subtitles.entity.spawner_mimic.step", this.getNames(3, "mob/spawner_mimic/step"));
        writer.add(CSSoundEvents.ENTITY_MIMIC_ATTACK, "subtitles.entity.spawner_mimic.attack", this.getNames(2, "mob/spawner_mimic/attack"));

        writer.add(CSSoundEvents.BLOCK_SPAWNER_KNOCKBACK, "subtitles.block.spawner.knockback",
            Identifier.ofVanilla("entity/wind_charge/wind_burst1"),
            Identifier.ofVanilla("entity/wind_charge/wind_burst2"),
            Identifier.ofVanilla("entity/wind_charge/wind_burst3")
        );

        writer.add(CSSoundEvents.BLOCK_SPAWNER_HEAL, "subtitles.block.spawner.heal", this.getNames(2, "block/spawner/heal"));
        writer.add(CSSoundEvents.BLOCK_SPAWNER_BOOST, "subtitles.block.spawner.boost", this.getNames(2, "block/spawner/boost"));
    }

    private List<Identifier> getNames (int max, String base) {
        List<Identifier> names = new ArrayList<>();
        for (int i = 1; i <= max; ++i) {
            names.add(CursedSpawnersMain.identifier(base + "_" + i));
        }
        return names;
    }
}
