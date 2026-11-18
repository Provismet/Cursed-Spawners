package com.provismet.cursedspawners;

import com.provismet.cursedspawners.networking.ClientPacketReceiver;
import com.provismet.cursedspawners.networking.GameRulePayloadS2C;
import com.provismet.cursedspawners.registries.CursedSpawnersEntityRenderers;
import com.provismet.cursedspawners.registries.CursedSpawnersModelLayers;
import com.provismet.cursedspawners.registries.CursedSpawnersParticleFactories;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

public class CursedSpawnersClient implements ClientModInitializer {
    @Override
    public void onInitializeClient () {
        CursedSpawnersParticleFactories.register();
        CursedSpawnersModelLayers.register();
        CursedSpawnersEntityRenderers.register();

        ClientPlayNetworking.registerGlobalReceiver(GameRulePayloadS2C.ID, (payload, _) -> ClientPacketReceiver.SPAWNER_BREAK_MODIFIER = payload.value());

        ClientPlayConnectionEvents.DISCONNECT.register((_, _) -> ClientPacketReceiver.SPAWNER_BREAK_MODIFIER = 1f);
    }
}
