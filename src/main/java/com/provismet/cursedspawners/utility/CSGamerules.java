package com.provismet.cursedspawners.utility;

import com.provismet.cursedspawners.CursedSpawnersMain;
import com.provismet.cursedspawners.networking.GameRulePayloadS2C;
import net.fabricmc.fabric.api.gamerule.v1.CustomGameRuleCategory;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleFactory;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleRegistry;
import net.fabricmc.fabric.api.gamerule.v1.rule.DoubleRule;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.world.GameRules;

public abstract class CSGamerules {
    public static final CustomGameRuleCategory SPAWNER_CATEGORY = new CustomGameRuleCategory(CursedSpawnersMain.identifier("gamerule_category"), Text.translatable("gamerule.category.cursed-spawners").formatted(Formatting.BOLD, Formatting.YELLOW));
    public static final GameRules.Key<DoubleRule> MIMIC_CHANCE = GameRuleRegistry.register("spawnerMimicChance", SPAWNER_CATEGORY, GameRuleFactory.createDoubleRule(0.05, 0, 1));
    public static final GameRules.Key<DoubleRule> BREAK_SPEED = GameRuleRegistry.register("spawnerMiningSpeedModifier", SPAWNER_CATEGORY, GameRuleFactory.createDoubleRule(1, 0, (server, rule) -> {
        for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
            ServerPlayNetworking.send(player, new GameRulePayloadS2C((float)rule.get()));
        }
    }));

    public static void init () {}
}
