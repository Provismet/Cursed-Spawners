package com.provismet.cursedspawners.registries;

import com.provismet.cursedspawners.action.destroy.CurseEffect;
import com.provismet.cursedspawners.action.destroy.SpawnerBreakEffect;
import com.provismet.cursedspawners.action.destroy.SummonSilverfishEffect;
import com.provismet.cursedspawners.action.destroy.SummonVexEffect;
import com.provismet.cursedspawners.reference.SpawnerBreakEffectKeys;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;

import java.util.function.Supplier;

public interface SpawnerBreakEffects {
    SpawnerBreakEffect SUMMON_VEX = register(SpawnerBreakEffectKeys.SUMMON_VEX, SummonVexEffect::new);
    SpawnerBreakEffect SUMMON_SILVERFISH = register(SpawnerBreakEffectKeys.SUMMON_SILVERFISH, SummonSilverfishEffect::new);
    SpawnerBreakEffect CURSE = register(SpawnerBreakEffectKeys.CURSE, CurseEffect::new);

    private static SpawnerBreakEffect register (ResourceKey<SpawnerBreakEffect> key, Supplier<SpawnerBreakEffect> effect) {
        return Registry.register(CursedSpawnersRegistries.SPAWNER_BREAK_EFFECT, key, effect.get());
    }

    static void init () {}
}
