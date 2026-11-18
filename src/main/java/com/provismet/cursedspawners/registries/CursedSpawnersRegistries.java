package com.provismet.cursedspawners.registries;

import com.mojang.serialization.MapCodec;
import com.provismet.cursedspawners.action.destroy.SpawnerBreakEffect;
import com.provismet.cursedspawners.action.passive.SpawnerPassiveEffect;
import com.provismet.cursedspawners.reference.CursedSpawnersRegistryKeys;
import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;

public interface CursedSpawnersRegistries {
    Registry<SpawnerBreakEffect> SPAWNER_BREAK_EFFECT = create(CursedSpawnersRegistryKeys.SPAWNER_BREAK_EFFECT);
    Registry<MapCodec<? extends SpawnerPassiveEffect>> SPAWNER_PASSIVE_EFFECT = create(CursedSpawnersRegistryKeys.SPAWNER_PASSIVE_EFFECT);

    private static <T> Registry<T> create (ResourceKey<Registry<T>> key) {
        return FabricRegistryBuilder.create(key).buildAndRegister();
    }
}
