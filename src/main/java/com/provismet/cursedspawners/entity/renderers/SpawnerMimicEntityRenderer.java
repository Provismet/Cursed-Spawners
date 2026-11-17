package com.provismet.cursedspawners.entity.renderers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.provismet.cursedspawners.CursedSpawnersMain;
import com.provismet.cursedspawners.entity.SpawnerMimicEntity;
import com.provismet.cursedspawners.entity.models.SpawnerMimicModel;
import com.provismet.cursedspawners.entity.renderers.states.SpawnerMimicRenderState;
import com.provismet.cursedspawners.registries.client.CSModelLayers;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;

public class SpawnerMimicEntityRenderer extends MobRenderer<SpawnerMimicEntity, SpawnerMimicRenderState, SpawnerMimicModel> {
    private static final Identifier TEXTURE = CursedSpawnersMain.identifier("textures/entity/spawner_mimic.png");

    public SpawnerMimicEntityRenderer (EntityRendererProvider.Context context) {
        super(context, new SpawnerMimicModel(context.bakeLayer(CSModelLayers.SPAWNER_MIMIC)), 0.6f);
    }

    @Override
    @NotNull
    public SpawnerMimicRenderState createRenderState () {
        return new SpawnerMimicRenderState();
    }

    @Override
    public void extractRenderState (SpawnerMimicEntity mimic, SpawnerMimicRenderState state, float tickDelta) {
        super.extractRenderState(mimic, state, tickDelta);
        state.attackState.copyFrom(mimic.attackState);
        state.idleState.copyFrom(mimic.idleState);
        state.spawnState.copyFrom(mimic.spawnState);
        state.mobRotation = Mth.lerp(tickDelta, mimic.getPrevMobRotation(), mimic.getMobRotation());
        if (mimic.getRenderedEntity() != null) state.renderedEntityState = this.entityRenderDispatcher.extractEntity(mimic.getRenderedEntity(), tickDelta);
        else state.renderedEntityState = null;
    }

    @Override
    public void submit (SpawnerMimicRenderState state, PoseStack matrixStack, SubmitNodeCollector orderedRenderCommandQueue, CameraRenderState cameraRenderState) {
        super.submit(state, matrixStack, orderedRenderCommandQueue, cameraRenderState);

        if (state.renderedEntityState != null) { // Yoinked from MobSpawnerBlockEntityRenderer
            matrixStack.pushPose();
            matrixStack.translate(0f, 0.25f, 0f);
            float f = 0.53125f;
            float g = Math.max(state.renderedEntityState.boundingBoxWidth, state.renderedEntityState.boundingBoxHeight);
            if ((double)g > 1.0) {
                f /= g;
            }

            matrixStack.translate(0f, 0.4f, 0f);
            matrixStack.mulPose(Axis.YP.rotationDegrees((float)state.mobRotation * 10f));
            matrixStack.translate(0f, -0.2f, 0f);
            matrixStack.mulPose(Axis.XP.rotationDegrees(-30f));
            matrixStack.scale(f, f, f);
            this.entityRenderDispatcher.submit(state.renderedEntityState, cameraRenderState, 0, 0, 0, matrixStack, orderedRenderCommandQueue);
            matrixStack.popPose();
        }
    }

    @Override
    @NotNull
    public Identifier getTextureLocation (SpawnerMimicRenderState entity) {
        return TEXTURE;
    }
}
