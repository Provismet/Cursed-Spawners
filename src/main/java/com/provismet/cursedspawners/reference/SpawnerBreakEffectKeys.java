package com.provismet.cursedspawners.reference;

import com.provismet.cursedspawners.CursedSpawnersMain;
import com.provismet.cursedspawners.action.destroy.SpawnerBreakEffect;
import net.minecraft.resources.ResourceKey;

public interface SpawnerBreakEffectKeys {
    ResourceKey<SpawnerBreakEffect> SUMMON_VEX = create("summon_vex");
    ResourceKey<SpawnerBreakEffect> SUMMON_SILVERFISH = create("summon_silverfish");
    ResourceKey<SpawnerBreakEffect> CURSE = create("curse");

    private static ResourceKey<SpawnerBreakEffect> create (String name) {
        return ResourceKey.create(CursedSpawnersRegistryKeys.SPAWNER_BREAK_EFFECT, CursedSpawnersMain.identifier(name));
    }
}
