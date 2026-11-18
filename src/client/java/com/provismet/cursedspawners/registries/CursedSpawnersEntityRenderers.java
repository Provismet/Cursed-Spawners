package com.provismet.cursedspawners.registries;

import com.provismet.cursedspawners.entity.renderer.SpawnerMimicEntityRenderer;
import net.minecraft.client.renderer.entity.EntityRenderers;

public abstract class CursedSpawnersEntityRenderers {
    public static void register () {
        EntityRenderers.register(CursedSpawnersEntityTypes.SPAWNER_MIMIC, SpawnerMimicEntityRenderer::new);
    }
}
