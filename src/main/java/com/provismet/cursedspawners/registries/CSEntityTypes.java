package com.provismet.cursedspawners.registries;

import com.provismet.cursedspawners.CursedSpawnersMain;
import com.provismet.cursedspawners.entity.SpawnerMimicEntity;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;

import java.util.function.Function;

public abstract class CSEntityTypes {
    public static final EntityType<SpawnerMimicEntity> SPAWNER_MIMIC = register(
        "spawner_mimic",
        key -> EntityType.Builder.<SpawnerMimicEntity>create(SpawnerMimicEntity::new, SpawnGroup.MONSTER)
            .dimensions(1.25f, 1.25f)
            .eyeHeight(0.75f)
            .passengerAttachments(1.15f)
            .maxTrackingRange(10)
            .makeFireImmune()
            .build(key)
    );

    private static <T extends Entity> EntityType<T> register (String name, Function<RegistryKey<EntityType<?>>, EntityType<T>> builder) {
        Identifier identifier = CursedSpawnersMain.identifier(name);
        return Registry.register(
            Registries.ENTITY_TYPE,
            identifier,
            builder.apply(RegistryKey.of(RegistryKeys.ENTITY_TYPE, identifier))
        );
    }

    public static void register () {
        FabricDefaultAttributeRegistry.register(SPAWNER_MIMIC, SpawnerMimicEntity.getSpawnerMimicAttributes());
    }
}
