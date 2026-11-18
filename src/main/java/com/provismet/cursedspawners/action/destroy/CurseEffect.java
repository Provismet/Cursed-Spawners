package com.provismet.cursedspawners.action.destroy;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.level.block.entity.SpawnerBlockEntity;

public class CurseEffect implements SpawnerBreakEffect {
    @Override
    public void onBreak (SpawnerBlockEntity spawner, ServerLevel world, ServerPlayer player) {
        player.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 60));
        player.addEffect(new MobEffectInstance(MobEffects.POISON, 60, 2));
    }
}
