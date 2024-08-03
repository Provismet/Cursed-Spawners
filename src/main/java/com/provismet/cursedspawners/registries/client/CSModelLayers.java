package com.provismet.cursedspawners.registries.client;

import com.provismet.cursedspawners.CursedSpawnersMain;
import com.provismet.cursedspawners.entity.models.SpawnerMimicModel;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.minecraft.client.render.entity.model.EntityModelLayer;

public class CSModelLayers {
    public static final EntityModelLayer SPAWNER_MIMIC = new EntityModelLayer(CursedSpawnersMain.identifier("spawner_mimic"), "main");

    public static void register () {
        EntityModelLayerRegistry.registerModelLayer(SPAWNER_MIMIC, SpawnerMimicModel::getTexturedModelData);
    }
}
