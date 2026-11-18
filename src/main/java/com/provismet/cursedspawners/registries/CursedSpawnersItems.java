package com.provismet.cursedspawners.registries;

import com.provismet.cursedspawners.CursedSpawnersMain;
import java.util.function.Function;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.SpawnEggItem;

public abstract class CursedSpawnersItems {
    public static final Item MIMIC_SPAWN_EGG = register("mimic_spawn_egg", settings -> new SpawnEggItem(settings.spawnEgg(CursedSpawnersEntityTypes.SPAWNER_MIMIC)));

    private static <T extends Item> T register (String name, Function<Item.Properties, T> settingsFunction) {
        ResourceKey<Item> key = ResourceKey.create(Registries.ITEM, CursedSpawnersMain.identifier(name));
        Item.Properties settings = new Item.Properties().setId(key);
        return Registry.register(BuiltInRegistries.ITEM, key, settingsFunction.apply(settings));
    }

    public static void init () {}
}
