package com.provismet.datagen.cursedspawners;

import com.provismet.cursedspawners.registries.CSItems;
import net.fabricmc.fabric.api.client.datagen.v1.provider.FabricModelProvider;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.minecraft.client.data.BlockStateModelGenerator;
import net.minecraft.client.data.ItemModelGenerator;

public class ModelGenerator extends FabricModelProvider {
    public ModelGenerator (FabricDataOutput output) {
        super(output);
    }

    @Override
    public void generateBlockStateModels (BlockStateModelGenerator blockStateModelGenerator) {

    }

    @Override
    public void generateItemModels (ItemModelGenerator itemModelGenerator) {
        itemModelGenerator.registerSpawnEgg(CSItems.MIMIC_SPAWN_EGG, 0x2A4455, 0x6E0453);
    }
}
