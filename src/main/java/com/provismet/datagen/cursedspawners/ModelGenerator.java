package com.provismet.datagen.cursedspawners;

import com.provismet.cursedspawners.registries.CSItems;
import net.fabricmc.fabric.api.client.datagen.v1.provider.FabricModelProvider;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.model.ModelTemplates;

public class ModelGenerator extends FabricModelProvider {
    public ModelGenerator (FabricDataOutput output) {
        super(output);
    }

    @Override
    public void generateBlockStateModels (BlockModelGenerators blockStateModelGenerator) {

    }

    @Override
    public void generateItemModels (ItemModelGenerators itemModelGenerator) {
        itemModelGenerator.generateFlatItem(CSItems.MIMIC_SPAWN_EGG, ModelTemplates.FLAT_ITEM);
    }
}
