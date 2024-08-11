package com.provismet.cursedspawners.registries;

import com.provismet.cursedspawners.CursedSpawnersMain;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;

public abstract class CSSoundEvents {
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
        return Registry.register(Registries.SOUND_EVENT, id, SoundEvent.of(id));
    }

    private static SoundEvent register (String name, float distance) {
        Identifier id = CursedSpawnersMain.identifier(name);
        return Registry.register(Registries.SOUND_EVENT, id, SoundEvent.of(id, distance));
    }

    public static void init () {}
}
