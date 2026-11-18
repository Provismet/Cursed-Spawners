package com.provismet.cursedspawners.action.destroy;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.provismet.cursedspawners.registries.CursedSpawnersRegistries;
import net.minecraft.core.Holder;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.entity.SpawnerBlockEntity;

import java.util.Optional;

public interface SpawnerBreakEffect {
    Codec<SpawnerBreakEffect> CODEC = Identifier.CODEC.flatXmap(
        identifier -> {
            Optional<Holder.Reference<SpawnerBreakEffect>> optionalEffect = CursedSpawnersRegistries.SPAWNER_BREAK_EFFECT.get(identifier);
            return optionalEffect.map(spawnerBreakEffectReference -> DataResult.success(spawnerBreakEffectReference.value()))
                .orElseGet(() -> DataResult.error(() -> "No SpawnerBreakEffect found for id: " + identifier));
        },
        effect -> {
            Identifier id = CursedSpawnersRegistries.SPAWNER_BREAK_EFFECT.getKey(effect);
            if (id != null) return DataResult.success(id);
            return DataResult.error(() -> "No id found for SpawnerBreakEffect: " + effect.getClass().getName());
        }
    );

    void onBreak (SpawnerBlockEntity spawner, ServerLevel world, ServerPlayer player);
}
