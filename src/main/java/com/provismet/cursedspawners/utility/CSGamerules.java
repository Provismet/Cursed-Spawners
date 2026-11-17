package com.provismet.cursedspawners.utility;

import com.provismet.cursedspawners.CursedSpawnersMain;
import net.fabricmc.fabric.api.gamerule.v1.CustomGameRuleCategory;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleBuilder;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.gamerules.GameRule;

public abstract class CSGamerules {
    public static final CustomGameRuleCategory SPAWNER_CATEGORY = new CustomGameRuleCategory(CursedSpawnersMain.identifier("gamerule_category"), Component.translatable("gamerule.category.cursed-spawners").withStyle(ChatFormatting.BOLD, ChatFormatting.YELLOW));
    public static final GameRule<Double> MIMIC_CHANCE = GameRuleBuilder.forDouble(0.1).category(SPAWNER_CATEGORY).range(0.0, 1.0).buildAndRegister(CursedSpawnersMain.identifier("spawner_mimic_chance"));
    public static final GameRule<Double> BREAK_SPEED = GameRuleBuilder.forDouble(0.5).category(SPAWNER_CATEGORY).minValue(0.0).buildAndRegister(CursedSpawnersMain.identifier("spawner_mining_speed_modifier"));
    public static final GameRule<Double> SPAWNER_ACTION_CHANCE = GameRuleBuilder.forDouble(0.333).category(SPAWNER_CATEGORY).range(0.0, 1.0).buildAndRegister(CursedSpawnersMain.identifier("spawner_action_chance"));

    public static void init () {}
}
