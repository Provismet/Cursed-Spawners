package com.provismet.cursedspawners.registries;

import com.provismet.cursedspawners.CursedSpawnersMain;
import com.provismet.cursedspawners.entity.renderer.models.SpawnerMimicModel;
import net.fabricmc.fabric.api.client.rendering.v1.ModelLayerRegistry;
import net.minecraft.client.model.geom.ModelLayerLocation;

public class CSModelLayers {
    public static final ModelLayerLocation SPAWNER_MIMIC = new ModelLayerLocation(CursedSpawnersMain.identifier("spawner_mimic"), "main");

    public static void register () {
        ModelLayerRegistry.registerModelLayer(SPAWNER_MIMIC, SpawnerMimicModel::getTexturedModelData);
    }
}
