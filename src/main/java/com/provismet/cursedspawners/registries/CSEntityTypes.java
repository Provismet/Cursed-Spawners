package com.provismet.cursedspawners.registries;

import com.provismet.cursedspawners.CursedSpawnersMain;
import com.provismet.cursedspawners.entity.SpawnerMimicEntity;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

public abstract class CSEntityTypes {
    public static final EntityType<SpawnerMimicEntity> SPAWNER_MIMIC = Registry.register(
        Registries.ENTITY_TYPE,
        CursedSpawnersMain.identifier("spawner_mimic"),
        EntityType.Builder.<SpawnerMimicEntity>create(SpawnerMimicEntity::new, SpawnGroup.MONSTER)
            .dimensions(1.25f, 1.25f)
            .eyeHeight(0.75f)
            .passengerAttachments(1.15f)
            .maxTrackingRange(10)
            .makeFireImmune()
            .build()
    );

    public static void register () {
        FabricDefaultAttributeRegistry.register(SPAWNER_MIMIC, SpawnerMimicEntity.getSpawnerMimicAttributes());
    }
}
