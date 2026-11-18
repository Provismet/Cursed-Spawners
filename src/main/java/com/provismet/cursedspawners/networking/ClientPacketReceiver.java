package com.provismet.cursedspawners.networking;

/**
 * This should technically be a client-only class, but the relevant mixin runs on both client and server.
 */
public abstract class ClientPacketReceiver {
    public static float SPAWNER_BREAK_MODIFIER = 1;
}
