package com.provismet.cursedspawners.mixin;

import com.provismet.cursedspawners.networking.GameRulePayloadS2C;
import com.provismet.cursedspawners.utility.CSGamerules;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.ServerTask;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.thread.ReentrantThreadExecutor;
import net.minecraft.world.rule.GameRule;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftServer.class)
public abstract class MinecraftServerMixin extends ReentrantThreadExecutor<ServerTask> {
    @Shadow
    public abstract PlayerManager getPlayerManager();

    public MinecraftServerMixin(String string) {
        super(string);
    }

    @Inject(method = "onGameRuleUpdated", at = @At("TAIL"))
    private <T> void updateGamerulePacket (GameRule<T> gameRule, T value, CallbackInfo info) {
        if (gameRule == CSGamerules.BREAK_SPEED && value instanceof Double doubleValue) {
            for (ServerPlayerEntity player : this.getPlayerManager().getPlayerList()) {
                ServerPlayNetworking.send(player, new GameRulePayloadS2C(doubleValue.floatValue()));
            }
        }
    }
}
