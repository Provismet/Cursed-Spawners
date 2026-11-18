package com.provismet.cursedspawners.reference;

import com.provismet.cursedspawners.CursedSpawnersMain;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.storage.loot.LootTable;

public interface CursedSpawnersLootTables {
    ResourceKey<LootTable> TIER_1 = create("tier_1");
    ResourceKey<LootTable> TIER_2 = create("tier_2");
    ResourceKey<LootTable> TIER_3 = create("tier_3");

    private static ResourceKey<LootTable> create (String name) {
        return ResourceKey.create(Registries.LOOT_TABLE, CursedSpawnersMain.identifier(name));
    }
}
