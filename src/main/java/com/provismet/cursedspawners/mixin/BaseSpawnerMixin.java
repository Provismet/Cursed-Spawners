package com.provismet.cursedspawners.mixin;

import com.provismet.cursedspawners.CursedSpawnersMain;
import com.provismet.cursedspawners.action.ActionGroup;
import com.provismet.cursedspawners.action.SpawnerData;
import com.provismet.cursedspawners.imixin.IMixinBaseSpawner;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.BaseSpawner;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

@Mixin(BaseSpawner.class)
public abstract class BaseSpawnerMixin implements IMixinBaseSpawner {
    @Unique
    private ActionGroup actionGroup = ActionGroup.DEFAULT;

    @Inject(method="load", at=@At("TAIL"))
    private void readExtendedNbt (Level level, BlockPos pos, ValueInput input, CallbackInfo ci) {
        input.read(CursedSpawnersMain.MODID, SpawnerData.CODEC).ifPresent(value -> this.actionGroup = value.actions());
    }

    @Inject(method="serverTick", at=@At(value="INVOKE", target="Lnet/minecraft/world/level/BaseSpawner;isNearPlayer(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;)Z", shift=At.Shift.AFTER))
    private void performActions (ServerLevel level, BlockPos pos, CallbackInfo ci) {
        this.actionGroup.tick(level, pos);
    }

    @Override
    public ActionGroup cursed_spawners$getActionGroup () {
        return this.actionGroup;
    }

    @Override
    public void cursed_spawners$setActionGroup (ActionGroup actionGroup) {
        this.actionGroup = actionGroup;
    }
}
