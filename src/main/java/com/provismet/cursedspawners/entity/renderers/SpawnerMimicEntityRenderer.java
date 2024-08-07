package com.provismet.cursedspawners.entity.renderers;

import com.provismet.cursedspawners.CursedSpawnersMain;
import com.provismet.cursedspawners.entity.SpawnerMimicEntity;
import com.provismet.cursedspawners.entity.models.SpawnerMimicModel;
import com.provismet.cursedspawners.registries.client.CSModelLayers;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;

public class SpawnerMimicEntityRenderer extends MobEntityRenderer<SpawnerMimicEntity, SpawnerMimicModel> {
    private static final Identifier TEXTURE = CursedSpawnersMain.identifier("textures/entity/spawner_mimic.png");

    private final EntityRenderDispatcher dispatcher;

    public SpawnerMimicEntityRenderer (EntityRendererFactory.Context context) {
        super(context, new SpawnerMimicModel(context.getPart(CSModelLayers.SPAWNER_MIMIC)), 0.6f);
        this.dispatcher = context.getRenderDispatcher();
    }

    @Override
    public void render (SpawnerMimicEntity mimic, float yaw, float tickDelta, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int light) {
        super.render(mimic, yaw, tickDelta, matrixStack, vertexConsumerProvider, light);

        Entity innerEntity = mimic.getRenderedEntity();
        if (innerEntity != null) {
            matrixStack.push();
            matrixStack.translate(0f, 0.25f, 0f);
            float f = 0.53125f;
            float g = Math.max(innerEntity.getWidth(), innerEntity.getHeight());
            if ((double)g > 1.0) {
                f /= g;
            }

            matrixStack.translate(0f, 0.4f, 0f);
            matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees((float) MathHelper.lerp(tickDelta, mimic.getPrevMobRotation(), mimic.getMobRotation()) * 10f));
            matrixStack.translate(0f, -0.2f, 0f);
            matrixStack.multiply(RotationAxis.POSITIVE_X.rotationDegrees(-30f));
            matrixStack.scale(f, f, f);
            this.dispatcher.render(innerEntity, 0.0, 0.0, 0.0, 0f, tickDelta, matrixStack, vertexConsumerProvider, light);
            matrixStack.pop();
        }
    }

    @Override
    public Identifier getTexture (SpawnerMimicEntity entity) {
        return TEXTURE;
    }
}
