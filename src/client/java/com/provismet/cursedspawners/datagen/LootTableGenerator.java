package com.provismet.cursedspawners.datagen;

import com.provismet.cursedspawners.registries.CSEntityTypes;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricEntityLootSubProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.EnchantedCountIncreaseFunction;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.providers.number.BinomialDistributionGenerator;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;

import java.util.concurrent.CompletableFuture;

public class LootTableGenerator extends FabricEntityLootSubProvider {
    CompletableFuture<HolderLookup.Provider> completableLookup;

    public LootTableGenerator (FabricPackOutput output, CompletableFuture<HolderLookup.Provider> registryLookup) {
        super(output, registryLookup);
        this.completableLookup = registryLookup;
    }

    @Override
    public void generate () {
        this.add(
            CSEntityTypes.SPAWNER_MIMIC,
            LootTable.lootTable()
                .withPool(
                    LootPool.lootPool()
                        .setRolls(ConstantValue.exactly(1))
                        .add(
                            LootItem.lootTableItem(Items.EXPERIENCE_BOTTLE)
                                .apply(SetItemCountFunction.setCount(ConstantValue.exactly(0)))
                                .apply(EnchantedCountIncreaseFunction.lootingMultiplier(this.registries, UniformGenerator.between(0, 3)))
                        )
                )
                .withPool(
                    LootPool.lootPool()
                        .setRolls(BinomialDistributionGenerator.binomial(1, 0.5f))
                        .add(
                            LootItem.lootTableItem(Items.IRON_BARS)
                                .apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 2)))
                        )
                )
        );
    }
}
