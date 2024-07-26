package com.provismet.cursedspawners.mixin;

import com.provismet.cursedspawners.imixin.IMixinMobSpawnerBlockEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.network.ServerPlayerInteractionManager;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ServerPlayerInteractionManager.class)
public abstract class ServerPlayerInteractionManagerMixin {
    @Shadow protected ServerWorld world;

    @Shadow @Final protected ServerPlayerEntity player;

    @Inject(method="tryBreakBlock", at=@At(
        value="INVOKE",
        target="Lnet/minecraft/block/Block;onBreak(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;Lnet/minecraft/entity/player/PlayerEntity;)Lnet/minecraft/block/BlockState;",
        shift = At.Shift.BEFORE),
        cancellable=true
    )
    private void falseBreak (BlockPos pos, CallbackInfoReturnable<Boolean> cir) {
        if (!this.player.isCreative() && !this.player.isSpectator() && this.world.getBlockEntity(pos) instanceof IMixinMobSpawnerBlockEntity spawnerBlockEntity) {
            if (!spawnerBlockEntity.cursed_spawners$attemptBreak()) cir.setReturnValue(false);
        }
    }
}
