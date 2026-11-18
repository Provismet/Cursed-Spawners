package com.provismet.cursedspawners.imixin;

import com.provismet.cursedspawners.action.SpawnerData;
import net.minecraft.server.level.ServerPlayer;

public interface IMixinSpawnerBlockEntity {
    default SpawnerData cursed_spawners$getSpawnerData () {
        return SpawnerData.DEFAULT;
    }

    default boolean cursed_spawners$attemptBreak (ServerPlayer player) {
        return true;
    }
}
