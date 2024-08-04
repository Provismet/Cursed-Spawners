package com.provismet.cursedspawners.imixin;

public interface IMixinMobSpawnerBlockEntity {
    boolean cursed_spawners$useWorldMimicChance ();
    double cursed_spawners$getMimicChance ();
    boolean cursed_spawners$attemptBreak ();
    void cursed_spawners$setShouldGenerateEffects (boolean value);
}
