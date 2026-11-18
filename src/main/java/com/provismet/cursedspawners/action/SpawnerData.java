package com.provismet.cursedspawners.action;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.provismet.cursedspawners.gamerule.CursedSpawnersGamerules;
import net.minecraft.server.level.ServerLevel;

public record SpawnerData (double mimicChance, boolean shouldGenerateEffects, ActionGroup actions) {
    public static final Codec<SpawnerData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        Codec.DOUBLE.fieldOf("mimic_chance").forGetter(SpawnerData::mimicChance),
        Codec.BOOL.fieldOf("should_generate_effects").forGetter(SpawnerData::shouldGenerateEffects),
        ActionGroup.CODEC.fieldOf("actions").forGetter(SpawnerData::actions)
    ).apply(instance, SpawnerData::new));

    public static final double PASSTHROUGH_MIMIC_CHANCE = -1;
    public static final SpawnerData DEFAULT = new SpawnerData(PASSTHROUGH_MIMIC_CHANCE, true, ActionGroup.DEFAULT);

    public SpawnerData withoutEffectGeneration () {
        return new SpawnerData(this.mimicChance, false, this.actions);
    }

    public SpawnerData withActionGroup (ActionGroup actionGroup) {
        return new SpawnerData(this.mimicChance, this.shouldGenerateEffects, actionGroup);
    }

    public boolean shouldUseGameruleMimicChance () {
        return this.mimicChance == PASSTHROUGH_MIMIC_CHANCE;
    }

    public double getEffectiveMimicChance (ServerLevel level) {
        if (this.shouldUseGameruleMimicChance()) return level.getGameRules().get(CursedSpawnersGamerules.MIMIC_CHANCE);
        return this.mimicChance;
    }
}
