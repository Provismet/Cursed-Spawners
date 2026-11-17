package com.provismet.cursedspawners.networking;

import com.provismet.cursedspawners.CursedSpawnersMain;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public record GameRulePayloadS2C (float value) implements CustomPacketPayload {
    public static final Type<GameRulePayloadS2C> ID = new Type<>(CursedSpawnersMain.identifier("spawner_mining_speed"));
    public static final StreamCodec<FriendlyByteBuf, GameRulePayloadS2C> CODEC = StreamCodec.composite(
        ByteBufCodecs.FLOAT,
        GameRulePayloadS2C::value,
        GameRulePayloadS2C::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type () {
        return ID;
    }
}
