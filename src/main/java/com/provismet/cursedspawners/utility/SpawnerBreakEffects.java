package com.provismet.cursedspawners.utility;

import net.minecraft.block.entity.MobSpawnerBlockEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.SilverfishEntity;
import net.minecraft.entity.mob.VexEntity;
import net.minecraft.item.Items;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class SpawnerBreakEffects {
    public static final String NORMAL_BREAK = "normal";
    public static final String SUMMON_VEX = "vex";
    public static final String SUMMON_SILVERFISH = "silverfish";
    public static final String CURSE = "curse";

    private static final Map<String, SpawnerConsumer> EFFECTS = new HashMap<>();

    static {
        EFFECTS.put(SUMMON_VEX, (spawner, world) -> {
            Vec3d centrePos = spawner.getPos().toCenterPos();
            for (int i = 0; i < 3; ++i) {
                VexEntity vex = new VexEntity(EntityType.VEX, world);
                vex.refreshPositionAndAngles(centrePos.getX(), centrePos.getY() + 1, centrePos.getZ(), 0f, 0f);
                vex.equipStack(EquipmentSlot.MAINHAND, Items.STONE_SWORD.getDefaultStack());
                world.spawnNewEntityAndPassengers(vex);
            }
        });

        EFFECTS.put(SUMMON_SILVERFISH, (spawner, world) -> {
            Vec3d centrePos = spawner.getPos().toCenterPos();
            for (int i = 0; i < 5; ++i) {
                SilverfishEntity silverfish = new SilverfishEntity(EntityType.SILVERFISH, world);
                silverfish.refreshPositionAndAngles(centrePos.getX(), centrePos.getY() + 1, centrePos.getZ(), 0f, 0f);
                world.spawnNewEntityAndPassengers(silverfish);
            }
        });

        EFFECTS.put(CURSE, (spawner, world) -> {
            Vec3d centrePos = spawner.getPos().toCenterPos();
            List<ServerPlayerEntity> playersInRange = world.getPlayers(player -> player.getPos().isWithinRangeOf(centrePos, 8, 8) && !player.isCreative() && !player.isSpectator());
            for (ServerPlayerEntity player : playersInRange) {
                player.addStatusEffect(new StatusEffectInstance(StatusEffects.BLINDNESS, 60));
                player.addStatusEffect(new StatusEffectInstance(StatusEffects.POISON, 60, 2));
            }
        });
    }

    public static SpawnerConsumer getEffect (String key) {
        return EFFECTS.getOrDefault(key, (spawner, world) -> {});
    }

    public static String getRandomEffectKey (Random random) {
        List<String> keys = EFFECTS.keySet().stream().toList();
        return keys.get(random.nextInt(keys.size()));
    }

    @FunctionalInterface
    public interface SpawnerConsumer {
        void accept (MobSpawnerBlockEntity spawner, ServerWorld world);
    }
}
