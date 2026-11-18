package com.provismet.cursedspawners.action.destroy;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Silverfish;
import net.minecraft.world.level.block.entity.SpawnerBlockEntity;
import net.minecraft.world.phys.Vec3;

public class SummonSilverfishEffect implements SpawnerBreakEffect {
    @Override
    public void onBreak (SpawnerBlockEntity spawner, ServerLevel world, ServerPlayer player) {
        Vec3 centrePos = Vec3.atCenterOf(spawner.getBlockPos());
        for (int i = 0; i < 5; ++i) {
            Silverfish silverfish = new Silverfish(EntityType.SILVERFISH, world);
            silverfish.snapTo(centrePos.x(), centrePos.y() + 1, centrePos.z(), 0f, 0f);
            world.tryAddFreshEntityWithPassengers(silverfish);
        }
    }
}
