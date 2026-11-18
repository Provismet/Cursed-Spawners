package com.provismet.cursedspawners;

import com.provismet.cursedspawners.networking.GameRulePayloadS2C;
import com.provismet.cursedspawners.registries.CursedSpawnersEntityTypes;
import com.provismet.cursedspawners.registries.CursedSpawnersItemGroups;
import com.provismet.cursedspawners.registries.CursedSpawnersItems;
import com.provismet.cursedspawners.registries.CursedSpawnersParticleTypes;
import com.provismet.cursedspawners.registries.CursedSpawnersSoundEvents;
import com.provismet.cursedspawners.registries.SpawnerBreakEffects;
import com.provismet.cursedspawners.registries.SpawnerPassiveEffects;
import com.provismet.cursedspawners.gamerule.CursedSpawnersGamerules;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.resources.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CursedSpawnersMain implements ModInitializer {
	public static final String MODID = "cursed-spawners";
    public static final Logger LOGGER = LoggerFactory.getLogger("Cursed Spawners");

	public static Identifier identifier (String path) {
		return Identifier.fromNamespaceAndPath(MODID, path);
	}

	@Override
	public void onInitialize () {
		CursedSpawnersGamerules.init();
		CursedSpawnersItems.init();
		CursedSpawnersSoundEvents.init();
		SpawnerBreakEffects.init();
		SpawnerPassiveEffects.bootstrap();
		CursedSpawnersEntityTypes.register();
		CursedSpawnersParticleTypes.register();
		CursedSpawnersItemGroups.register();

		PayloadTypeRegistry.clientboundPlay().register(GameRulePayloadS2C.ID, GameRulePayloadS2C.CODEC);

		ServerPlayConnectionEvents.JOIN.register((networkHandler, packetSender, server) -> {
			packetSender.sendPacket(new GameRulePayloadS2C(server.overworld().getGameRules().get(CursedSpawnersGamerules.BREAK_SPEED).floatValue()));
		});
	}
}