package com.provismet.cursedspawners.action.destroy;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.monster.Vex;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.entity.SpawnerBlockEntity;
import net.minecraft.world.phys.Vec3;

public class SummonVexEffect implements SpawnerBreakEffect {
    @Override
    public void onBreak (SpawnerBlockEntity spawner, ServerLevel world, ServerPlayer player) {
        Vec3 centrePos = Vec3.atCenterOf(spawner.getBlockPos());
        for (int i = 0; i < 3; ++i) {
            Vex vex = new Vex(EntityTypes.VEX, world);
            vex.snapTo(centrePos.x(), centrePos.y() + 1, centrePos.z(), 0f, 0f);
            vex.setItemSlot(EquipmentSlot.MAINHAND, Items.STONE_SWORD.getDefaultInstance());
            world.tryAddFreshEntityWithPassengers(vex);
        }
    }
}
