package com.provismet.cursedspawners.datagen;

import com.provismet.cursedspawners.reference.CursedSpawnedEntityTypeIds;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagsProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.tags.TagKey;

import java.util.concurrent.CompletableFuture;

public class EntityTypeTagGenerator extends FabricTagsProvider.EntityTypeTagsProvider {
    public EntityTypeTagGenerator (FabricPackOutput output, CompletableFuture<HolderLookup.Provider> completableFuture) {
        super(output, completableFuture);
    }

    @Override
    protected void addTags (HolderLookup.Provider wrapperLookup) {
        this.builder(TagKey.create(Registries.ENTITY_TYPE, Identifier.fromNamespaceAndPath("extra-damage-enchantments", "arcane")))
            .add(CursedSpawnedEntityTypeIds.SPAWNER_MIMIC);

        this.builder(EntityTypeTags.NO_ANGER_FROM_WIND_CHARGE)
            .add(CursedSpawnedEntityTypeIds.SPAWNER_MIMIC);

        this.builder(EntityTypeTags.WITHER_FRIENDS)
            .add(CursedSpawnedEntityTypeIds.SPAWNER_MIMIC);

        this.builder(EntityTypeTags.ILLAGER_FRIENDS)
            .add(CursedSpawnedEntityTypeIds.SPAWNER_MIMIC);
    }
}
