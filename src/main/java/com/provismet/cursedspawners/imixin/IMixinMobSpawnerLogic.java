package com.provismet.cursedspawners.imixin;

public interface IMixinMobSpawnerLogic {
    boolean cursed_spawners$getCanKnockback ();
    void cursed_spawners$setCanKnockback (boolean value);
    void cursed_spawners$setKnockbackParams (int interval, double strength, double radius);
    boolean cursed_spawners$getCanHeal ();
    void cursed_spawners$setCanHeal (boolean value);
    void cursed_spawners$setHealParams (int interval, float amount, double radius);
    boolean cursed_spawners$getCanBoost ();
    void cursed_spawners$setCanBoost (boolean value);
    void cursed_spawners$setBoostParams (int interval, double radius);
}
