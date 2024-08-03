package com.provismet.cursedspawners.registries.client;

import com.provismet.cursedspawners.entity.renderers.SpawnerMimicEntityRenderer;
import com.provismet.cursedspawners.registries.CSEntityTypes;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;

public abstract class CSEntityRenderers {
    public static void register () {
        EntityRendererRegistry.register(CSEntityTypes.SPAWNER_MIMIC, SpawnerMimicEntityRenderer::new);
    }
}
