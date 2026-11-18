package com.provismet.cursedspawners.reference;

import com.provismet.cursedspawners.CursedSpawnersMain;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EntityType;

public interface CursedSpawnedEntityTypeIds {
    ResourceKey<EntityType<?>> SPAWNER_MIMIC = ResourceKey.create(Registries.ENTITY_TYPE, CursedSpawnersMain.identifier("spawner_mimic"));
}
