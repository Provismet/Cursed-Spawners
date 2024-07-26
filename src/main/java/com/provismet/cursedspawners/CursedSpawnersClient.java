package com.provismet.cursedspawners;

import com.provismet.cursedspawners.networking.ClientPacketReceiver;
import com.provismet.cursedspawners.networking.GameRulePayloadS2C;
import com.provismet.cursedspawners.registries.client.CSParticleFactories;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

public class CursedSpawnersClient implements ClientModInitializer {
    @Override
    public void onInitializeClient () {
        CSParticleFactories.register();

        ClientPlayNetworking.registerGlobalReceiver(GameRulePayloadS2C.ID, (payload, context) -> ClientPacketReceiver.SPAWNER_BREAK_MODIFIER = payload.value());
    }
}
