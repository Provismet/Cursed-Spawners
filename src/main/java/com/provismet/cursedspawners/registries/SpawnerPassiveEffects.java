package com.provismet.cursedspawners.registries;

import com.mojang.serialization.MapCodec;
import com.provismet.cursedspawners.CursedSpawnersMain;
import com.provismet.cursedspawners.action.passive.HealPassiveEffect;
import com.provismet.cursedspawners.action.passive.KnockbackPassiveEffect;
import com.provismet.cursedspawners.action.passive.SpawnerPassiveEffect;
import com.provismet.cursedspawners.action.passive.SpeedBoostEffect;
import net.minecraft.core.Registry;
import net.minecraft.util.RandomSource;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;

public abstract class SpawnerPassiveEffects {
    private static final List<Function<RandomSource,SpawnerPassiveEffect>> RANDOM_FACTORIES = new ArrayList<>();

    public static void bootstrap () {
        register("knockback", KnockbackPassiveEffect.CODEC, KnockbackPassiveEffect::fromRandom);
        register("heal", HealPassiveEffect.CODEC, HealPassiveEffect::fromRandom);
        register("speed_boost", SpeedBoostEffect.CODEC, SpeedBoostEffect::fromRandom);
    }

    public static void register (String name, MapCodec<? extends SpawnerPassiveEffect> codec, Function<RandomSource,SpawnerPassiveEffect> factory) {
        Registry.register(CursedSpawnersRegistries.SPAWNER_PASSIVE_EFFECT, CursedSpawnersMain.identifier(name), codec);
        RANDOM_FACTORIES.add(factory);
    }

    public static Stream<Function<RandomSource,SpawnerPassiveEffect>> streamFactories () {
        return RANDOM_FACTORIES.stream();
    }
}
