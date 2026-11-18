package com.provismet.cursedspawners;

import com.provismet.cursedspawners.networking.ClientPacketReceiver;
import com.provismet.cursedspawners.networking.GameRulePayloadS2C;
import com.provismet.cursedspawners.registries.CSEntityRenderers;
import com.provismet.cursedspawners.registries.CSModelLayers;
import com.provismet.cursedspawners.registries.CSParticleFactories;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

public class CursedSpawnersClient implements ClientModInitializer {
    @Override
    public void onInitializeClient () {
        CSParticleFactories.register();
        CSModelLayers.register();
        CSEntityRenderers.register();

        ClientPlayNetworking.registerGlobalReceiver(GameRulePayloadS2C.ID, (payload, _) -> ClientPacketReceiver.SPAWNER_BREAK_MODIFIER = payload.value());

        ClientPlayConnectionEvents.DISCONNECT.register((_, _) -> ClientPacketReceiver.SPAWNER_BREAK_MODIFIER = 1f);
    }
}
