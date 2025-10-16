package com.provismet.cursedspawners.entity.renderers;

import com.provismet.cursedspawners.CursedSpawnersMain;
import com.provismet.cursedspawners.entity.SpawnerMimicEntity;
import com.provismet.cursedspawners.entity.models.SpawnerMimicModel;
import com.provismet.cursedspawners.entity.renderers.states.SpawnerMimicRenderState;
import com.provismet.cursedspawners.registries.client.CSModelLayers;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.entity.EntityRenderManager;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.render.state.CameraRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;

public class SpawnerMimicEntityRenderer extends MobEntityRenderer<SpawnerMimicEntity, SpawnerMimicRenderState, SpawnerMimicModel> {
    private static final Identifier TEXTURE = CursedSpawnersMain.identifier("textures/entity/spawner_mimic.png");

    private final EntityRenderManager dispatcher;

    public SpawnerMimicEntityRenderer (EntityRendererFactory.Context context) {
        super(context, new SpawnerMimicModel(context.getPart(CSModelLayers.SPAWNER_MIMIC)), 0.6f);
        this.dispatcher = context.getRenderDispatcher();
    }

    @Override
    public SpawnerMimicRenderState createRenderState () {
        return new SpawnerMimicRenderState();
    }

    @Override
    public void updateRenderState (SpawnerMimicEntity mimic, SpawnerMimicRenderState state, float tickDelta) {
        super.updateRenderState(mimic, state, tickDelta);
        state.attackState.copyFrom(mimic.attackState);
        state.idleState.copyFrom(mimic.idleState);
        state.spawnState.copyFrom(mimic.spawnState);
        state.mobRotation = MathHelper.lerp(tickDelta, mimic.getPrevMobRotation(), mimic.getMobRotation());
        if (mimic.getRenderedEntity() != null) state.renderedEntityState = this.dispatcher.getAndUpdateRenderState(mimic.getRenderedEntity(), tickDelta);
        else state.renderedEntityState = null;
    }

    @Override
    public void render (SpawnerMimicRenderState state, MatrixStack matrixStack, OrderedRenderCommandQueue orderedRenderCommandQueue, CameraRenderState cameraRenderState) {
        super.render(state, matrixStack, orderedRenderCommandQueue, cameraRenderState);

        if (state.renderedEntityState != null) { // Yoinked from MobSpawnerBlockEntityRenderer
            matrixStack.push();
            matrixStack.translate(0f, 0.25f, 0f);
            float f = 0.53125f;
            float g = Math.max(state.renderedEntityState.width, state.renderedEntityState.height);
            if ((double)g > 1.0) {
                f /= g;
            }

            matrixStack.translate(0f, 0.4f, 0f);
            matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees((float)state.mobRotation * 10f));
            matrixStack.translate(0f, -0.2f, 0f);
            matrixStack.multiply(RotationAxis.POSITIVE_X.rotationDegrees(-30f));
            matrixStack.scale(f, f, f);
            this.dispatcher.render(state.renderedEntityState, cameraRenderState, 0, 0, 0, matrixStack, orderedRenderCommandQueue);
            matrixStack.pop();
        }
    }

    @Override
    public Identifier getTexture (SpawnerMimicRenderState entity) {
        return TEXTURE;
    }
}
