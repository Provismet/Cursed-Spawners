package com.provismet.cursedspawners.registries;

import com.provismet.cursedspawners.entity.renderer.SpawnerMimicEntityRenderer;
import net.minecraft.client.renderer.entity.EntityRenderers;

public abstract class CSEntityRenderers {
    public static void register () {
        EntityRenderers.register(CSEntityTypes.SPAWNER_MIMIC, SpawnerMimicEntityRenderer::new);
    }
}
