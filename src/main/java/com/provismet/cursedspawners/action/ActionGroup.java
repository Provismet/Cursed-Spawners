package com.provismet.cursedspawners.action;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.provismet.cursedspawners.action.destroy.SpawnerBreakEffect;
import com.provismet.cursedspawners.action.passive.SpawnerPassiveEffect;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public record ActionGroup (List<SpawnerPassiveEffect> passiveEffects, List<SpawnerBreakEffect> breakEffects) {
    public static final Codec<ActionGroup> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        SpawnerPassiveEffect.CODEC.listOf().optionalFieldOf("passive", List.of()).forGetter(ActionGroup::passiveEffects),
        SpawnerBreakEffect.CODEC.listOf().orElse(List.of()).optionalFieldOf("break", List.of()).forGetter(ActionGroup::breakEffects)
    ).apply(instance, ActionGroup::new));

    public static final ActionGroup DEFAULT = new ActionGroup(List.of(), List.of());

    public void tick (ServerLevel level, BlockPos spawnerPos) {
        for (SpawnerPassiveEffect effect : this.passiveEffects) {
            effect.tick(level, spawnerPos);
        }
    }

    public boolean canReforge () {
        return this.breakEffects.size() > 1;
    }

    @Nullable
    public SpawnerBreakEffect getNextBreakEffect () {
        if (this.breakEffects.isEmpty()) return null;
        return this.breakEffects.getFirst();
    }

    public ActionGroup popBreakEffect () {
        if (this.breakEffects.size() < 2) return new ActionGroup(this.passiveEffects, List.of());
        return new ActionGroup(this.passiveEffects, this.breakEffects.subList(1, this.breakEffects.size()));
    }
}
