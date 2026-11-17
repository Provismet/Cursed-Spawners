package com.provismet.cursedspawners.registries.client;

import com.provismet.cursedspawners.entity.renderers.SpawnerMimicEntityRenderer;
import com.provismet.cursedspawners.registries.CSEntityTypes;
import net.minecraft.client.renderer.entity.EntityRenderers;

public abstract class CSEntityRenderers {
    public static void register () {
        EntityRenderers.register(CSEntityTypes.SPAWNER_MIMIC, SpawnerMimicEntityRenderer::new);
    }
}
