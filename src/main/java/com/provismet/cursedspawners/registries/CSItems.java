package com.provismet.cursedspawners.registries;

import com.provismet.cursedspawners.CursedSpawnersMain;
import net.minecraft.item.Item;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

public abstract class CSItems {
    public static final Item MIMIC_SPAWN_EGG = Registry.register(Registries.ITEM, CursedSpawnersMain.identifier("mimic_spawn_egg"),
        new SpawnEggItem(CSEntityTypes.SPAWNER_MIMIC, 0x2A4455, 0x6E0453, new Item.Settings()));

    public static void init () {}
}
