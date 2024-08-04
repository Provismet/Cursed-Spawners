package com.provismet.cursedspawners.imixin;

public interface IMixinMobSpawnerLogic {
    boolean cursed_spawners$getCanKnockback ();
    void cursed_spawners$setCanKnockback (boolean value);
    void cursed_spawners$setKnockbackParams (int interval, double strength, double radius);
}
