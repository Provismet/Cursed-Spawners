package com.provismet.cursedspawners.mixin;

import com.provismet.cursedspawners.networking.GameRulePayloadS2C;
import com.provismet.cursedspawners.utility.CSGamerules;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.ClientConnection;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ConnectedClientData;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerManager.class)
public abstract class PlayerManagerMixin {
    @Shadow @Final private MinecraftServer server;

    @Inject(
        method = "onPlayerConnect",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/server/network/ServerPlayNetworkHandler;sendPacket(Lnet/minecraft/network/packet/Packet;)V",
            ordinal = 0
        )
    )
    private void sendJoinPacket (ClientConnection connection, ServerPlayerEntity player, ConnectedClientData clientData, CallbackInfo ci) {
        ServerPlayNetworking.send(player, new GameRulePayloadS2C((float)this.server.getOverworld().getGameRules().get(CSGamerules.BREAK_SPEED).get()));
    }
}
