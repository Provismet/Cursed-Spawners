package com.provismet.cursedspawners.registries;

import com.provismet.cursedspawners.CursedSpawnersMain;
import com.provismet.cursedspawners.entity.SpawnerMimicEntity;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import java.util.function.Function;

public abstract class CSEntityTypes {
    public static final EntityType<SpawnerMimicEntity> SPAWNER_MIMIC = register(
        "spawner_mimic",
        key -> EntityType.Builder.<SpawnerMimicEntity>of(SpawnerMimicEntity::new, MobCategory.MONSTER)
            .sized(1.25f, 1.25f)
            .eyeHeight(0.75f)
            .passengerAttachments(1.15f)
            .clientTrackingRange(10)
            .fireImmune()
            .build(key)
    );

    private static <T extends Entity> EntityType<T> register (String name, Function<ResourceKey<EntityType<?>>, EntityType<T>> builder) {
        Identifier identifier = CursedSpawnersMain.identifier(name);
        return Registry.register(
            BuiltInRegistries.ENTITY_TYPE,
            identifier,
            builder.apply(ResourceKey.create(Registries.ENTITY_TYPE, identifier))
        );
    }

    public static void register () {
        FabricDefaultAttributeRegistry.register(SPAWNER_MIMIC, SpawnerMimicEntity.getSpawnerMimicAttributes());
    }
}
