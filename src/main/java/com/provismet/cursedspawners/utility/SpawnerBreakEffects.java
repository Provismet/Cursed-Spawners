package com.provismet.cursedspawners.utility;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.monster.Silverfish;
import net.minecraft.world.entity.monster.Vex;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.entity.SpawnerBlockEntity;
import net.minecraft.world.phys.Vec3;

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
            Vec3 centrePos = Vec3.atCenterOf(spawner.getBlockPos());
            for (int i = 0; i < 3; ++i) {
                Vex vex = new Vex(EntityType.VEX, world);
                vex.snapTo(centrePos.x(), centrePos.y() + 1, centrePos.z(), 0f, 0f);
                vex.setItemSlot(EquipmentSlot.MAINHAND, Items.STONE_SWORD.getDefaultInstance());
                world.tryAddFreshEntityWithPassengers(vex);
            }
        });

        EFFECTS.put(SUMMON_SILVERFISH, (spawner, world) -> {
            Vec3 centrePos = Vec3.atCenterOf(spawner.getBlockPos());
            for (int i = 0; i < 5; ++i) {
                Silverfish silverfish = new Silverfish(EntityType.SILVERFISH, world);
                silverfish.snapTo(centrePos.x(), centrePos.y() + 1, centrePos.z(), 0f, 0f);
                world.tryAddFreshEntityWithPassengers(silverfish);
            }
        });

        EFFECTS.put(CURSE, (spawner, world) -> {
            Vec3 centrePos = Vec3.atCenterOf(spawner.getBlockPos());
            List<ServerPlayer> playersInRange = world.getPlayers(player -> player.position().closerThan(centrePos, 8, 8) && !player.isCreative() && !player.isSpectator());
            for (ServerPlayer player : playersInRange) {
                player.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 60));
                player.addEffect(new MobEffectInstance(MobEffects.POISON, 60, 2));
            }
        });
    }

    public static SpawnerConsumer getEffect (String key) {
        return EFFECTS.getOrDefault(key, (spawner, world) -> {});
    }

    public static String getRandomEffectKey (RandomSource random) {
        List<String> keys = EFFECTS.keySet().stream().toList();
        return keys.get(random.nextInt(keys.size()));
    }

    @FunctionalInterface
    public interface SpawnerConsumer {
        void accept (SpawnerBlockEntity spawner, ServerLevel world);
    }
}
