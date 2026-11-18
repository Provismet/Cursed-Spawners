package com.provismet.cursedspawners.registries;

import com.provismet.cursedspawners.CursedSpawnersMain;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvent;

public abstract class CursedSpawnersSoundEvents {
    public static final SoundEvent ENTITY_MIMIC_AMBIENT = register("entity.mimic.ambient");
    public static final SoundEvent ENTITY_MIMIC_HURT = register("entity.mimic.hurt");
    public static final SoundEvent ENTITY_MIMIC_DEATH = register("entity.mimic.death");
    public static final SoundEvent ENTITY_MIMIC_STEP = register("entity.mimic.step");
    public static final SoundEvent ENTITY_MIMIC_ATTACK = register("entity.mimic.attack");

    public static final SoundEvent BLOCK_SPAWNER_KNOCKBACK = register("block.spawner.knockback", 8f);
    public static final SoundEvent BLOCK_SPAWNER_HEAL = register("block.spawner.heal", 8f);
    public static final SoundEvent BLOCK_SPAWNER_BOOST = register("block.spawner.boost", 8f);

    private static SoundEvent register (String name) {
        Identifier id = CursedSpawnersMain.identifier(name);
        return Registry.register(BuiltInRegistries.SOUND_EVENT, id, SoundEvent.createVariableRangeEvent(id));
    }

    private static SoundEvent register (String name, float distance) {
        Identifier id = CursedSpawnersMain.identifier(name);
        return Registry.register(BuiltInRegistries.SOUND_EVENT, id, SoundEvent.createFixedRangeEvent(id, distance));
    }

    public static void init () {}
}
