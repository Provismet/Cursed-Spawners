package com.provismet.cursedspawners.registries;

import net.fabricmc.fabric.api.creativetab.v1.CreativeModeTabEvents;
import net.minecraft.world.item.CreativeModeTabs;

public abstract class CSItemGroups {
    public static void register () {
        CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.SPAWN_EGGS).register(content -> content.accept(CSItems.MIMIC_SPAWN_EGG));
    }
}
