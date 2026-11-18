package com.provismet.cursedspawners.gamerule;

import com.provismet.cursedspawners.CursedSpawnersMain;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleBuilder;
import net.minecraft.world.level.gamerules.GameRule;
import net.minecraft.world.level.gamerules.GameRuleCategory;

public abstract class CursedSpawnersGamerules {
    public static final GameRuleCategory SPAWNER_CATEGORY = new GameRuleCategory(CursedSpawnersMain.identifier("gamerule_category"));
    public static final GameRule<Double> MIMIC_CHANCE = GameRuleBuilder.forDouble(0.1).category(SPAWNER_CATEGORY).range(0.0, 1.0).buildAndRegister(CursedSpawnersMain.identifier("spawner_mimic_chance"));
    public static final GameRule<Double> BREAK_SPEED = GameRuleBuilder.forDouble(0.5).category(SPAWNER_CATEGORY).minValue(0.0).buildAndRegister(CursedSpawnersMain.identifier("spawner_mining_speed_modifier"));
    public static final GameRule<Double> SPAWNER_ACTION_CHANCE = GameRuleBuilder.forDouble(0.333).category(SPAWNER_CATEGORY).range(0.0, 1.0).buildAndRegister(CursedSpawnersMain.identifier("spawner_action_chance"));
    public static final GameRule<Integer> MAX_SPAWNER_ACTIONS = GameRuleBuilder.forInteger(3).category(SPAWNER_CATEGORY).range(0, 100).buildAndRegister(CursedSpawnersMain.identifier("spawner_max_actions"));

    public static void init () {}
}
