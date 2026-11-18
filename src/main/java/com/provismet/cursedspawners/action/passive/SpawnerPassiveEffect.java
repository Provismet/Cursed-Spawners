package com.provismet.cursedspawners.action.passive;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.provismet.cursedspawners.registries.CursedSpawnersRegistries;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

public interface SpawnerPassiveEffect {
    Codec<SpawnerPassiveEffect> CODEC = CursedSpawnersRegistries.SPAWNER_PASSIVE_EFFECT.byNameCodec().dispatch(SpawnerPassiveEffect::getCodec, Function.identity());

    void tick (ServerLevel level, BlockPos spawnerPos);
    @NotNull MapCodec<? extends SpawnerPassiveEffect> getCodec ();
}
