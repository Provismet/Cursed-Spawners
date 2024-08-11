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

    private static SoundEvent register (String name) {
        Identifier id = CursedSpawnersMain.identifier(name);
        return Registry.register(Registries.SOUND_EVENT, id, SoundEvent.of(id));
    }

    public static void init () {}
}
