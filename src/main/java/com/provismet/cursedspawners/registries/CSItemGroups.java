package com.provismet.cursedspawners.registries;

import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.world.item.CreativeModeTabs;

public abstract class CSItemGroups {
    public static void register () {
        ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.SPAWN_EGGS).register(content -> content.accept(CSItems.MIMIC_SPAWN_EGG));
    }
}
