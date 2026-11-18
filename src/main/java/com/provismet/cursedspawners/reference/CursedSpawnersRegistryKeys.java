package com.provismet.cursedspawners.reference;

import com.mojang.serialization.MapCodec;
import com.provismet.cursedspawners.CursedSpawnersMain;
import com.provismet.cursedspawners.action.destroy.SpawnerBreakEffect;
import com.provismet.cursedspawners.action.passive.SpawnerPassiveEffect;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;

public interface CursedSpawnersRegistryKeys {
    ResourceKey<Registry<SpawnerBreakEffect>> SPAWNER_BREAK_EFFECT = create("spawner_break_effect");
    ResourceKey<Registry<MapCodec<? extends SpawnerPassiveEffect>>> SPAWNER_PASSIVE_EFFECT = create("spawner_passive_effect");

    private static <T> ResourceKey<Registry<T>> create (String name) {
        return ResourceKey.createRegistryKey(CursedSpawnersMain.identifier(name));
    }
}
