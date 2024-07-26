package com.provismet.cursedspawners.networking;

import com.provismet.cursedspawners.CursedSpawnersMain;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;

public record GameRulePayloadS2C (float value) implements CustomPayload {
    public static final Id<GameRulePayloadS2C> ID = new Id<>(CursedSpawnersMain.identifier("spawner_mining_speed"));
    public static final PacketCodec<PacketByteBuf, GameRulePayloadS2C> CODEC = PacketCodec.tuple(
        PacketCodecs.FLOAT,
        GameRulePayloadS2C::value,
        GameRulePayloadS2C::new
    );

    @Override
    public Id<? extends CustomPayload> getId () {
        return ID;
    }
}
