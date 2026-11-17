package com.provismet.cursedspawners.mixin;

import com.provismet.cursedspawners.imixin.IMixinMobSpawnerBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerPlayerGameMode;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ServerPlayerGameMode.class)
public abstract class ServerPlayerInteractionManagerMixin {
    @Shadow protected ServerLevel level;

    @Shadow @Final protected ServerPlayer player;

    @Inject(
        method = "destroyBlock",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/level/block/Block;playerWillDestroy(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/entity/player/Player;)Lnet/minecraft/world/level/block/state/BlockState;",
            shift = At.Shift.BEFORE
        ),
        cancellable = true
    )
    private void falseBreak (BlockPos pos, CallbackInfoReturnable<Boolean> cir) {
        if (!this.player.isCreative() && !this.player.isSpectator() && this.level.getBlockEntity(pos) instanceof IMixinMobSpawnerBlockEntity spawnerBlockEntity) {
            if (!spawnerBlockEntity.cursed_spawners$attemptBreak()) cir.setReturnValue(false);
        }
    }
}
