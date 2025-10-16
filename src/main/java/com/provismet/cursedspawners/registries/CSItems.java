package com.provismet.cursedspawners.registries;

import com.provismet.cursedspawners.CursedSpawnersMain;
import net.minecraft.item.Item;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;

import java.util.function.Function;

public abstract class CSItems {
    public static final Item MIMIC_SPAWN_EGG = register("mimic_spawn_egg", settings -> new SpawnEggItem(settings.spawnEgg(CSEntityTypes.SPAWNER_MIMIC)));

    private static <T extends Item> T register (String name, Function<Item.Settings, T> settingsFunction) {
        RegistryKey<Item> key = RegistryKey.of(RegistryKeys.ITEM, CursedSpawnersMain.identifier(name));
        Item.Settings settings = new Item.Settings().registryKey(key);
        return Registry.register(Registries.ITEM, key, settingsFunction.apply(settings));
    }

    public static void init () {}
}
