package com.provismet.cursedspawners.registries;

import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.ItemGroups;

public abstract class CSItemGroups {
    public static void register () {
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.SPAWN_EGGS).register(content -> content.add(CSItems.MIMIC_SPAWN_EGG));
    }
}
