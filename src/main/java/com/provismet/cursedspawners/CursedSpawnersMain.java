package com.provismet.cursedspawners;

import com.provismet.cursedspawners.networking.GameRulePayloadS2C;
import com.provismet.cursedspawners.registries.CSEntityTypes;
import com.provismet.cursedspawners.registries.CSItems;
import com.provismet.cursedspawners.registries.CSParticleTypes;
import com.provismet.cursedspawners.utility.CSGamerules;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CursedSpawnersMain implements ModInitializer {
	public static final String MODID = "cursed-spawners";
    public static final Logger LOGGER = LoggerFactory.getLogger("Curse Spawners");

	public static Identifier identifier (String path) {
		return Identifier.of(MODID, path);
	}

	@Override
	public void onInitialize () {
		CSGamerules.init();
		CSItems.init();
		CSEntityTypes.register();
		CSParticleTypes.register();

		PayloadTypeRegistry.playS2C().register(GameRulePayloadS2C.ID, GameRulePayloadS2C.CODEC);
	}
}