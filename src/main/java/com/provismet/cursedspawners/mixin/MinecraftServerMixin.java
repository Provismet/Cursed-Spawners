package com.provismet.cursedspawners.mixin;

import com.provismet.cursedspawners.networking.GameRulePayloadS2C;
import com.provismet.cursedspawners.gamerule.CursedSpawnersGamerules;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.TickTask;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerList;
import net.minecraft.util.thread.ReentrantBlockableEventLoop;
import net.minecraft.world.level.gamerules.GameRule;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftServer.class)
public abstract class MinecraftServerMixin extends ReentrantBlockableEventLoop<TickTask> {
    public MinecraftServerMixin (String name, boolean propagatesCrashes) {
        super(name, propagatesCrashes);
    }

    @Shadow
    public abstract PlayerList getPlayerList();

    @Inject(method = "onGameRuleChanged", at = @At("TAIL"))
    private <T> void updateGamerulePacket (GameRule<T> gameRule, T value, CallbackInfo info) {
        if (gameRule == CursedSpawnersGamerules.BREAK_SPEED && value instanceof Double doubleValue) {
            for (ServerPlayer player : this.getPlayerList().getPlayers()) {
                ServerPlayNetworking.send(player, new GameRulePayloadS2C(doubleValue.floatValue()));
            }
        }
    }
}
