package com.provismet.datagen.cursedspawners;

import com.provismet.cursedspawners.registries.CSEntityTypes;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.SimpleFabricLootTableProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.EnchantedCountIncreaseFunction;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.providers.number.BinomialDistributionGenerator;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;

public class LootTableGenerator extends SimpleFabricLootTableProvider {
    CompletableFuture<HolderLookup.Provider> completableLookup;

    public LootTableGenerator (FabricDataOutput output, CompletableFuture<HolderLookup.Provider> registryLookup) {
        super(output, registryLookup, LootContextParamSets.ENTITY);
        this.completableLookup = registryLookup;
    }

    @Override
    public void generate (BiConsumer<ResourceKey<LootTable>, LootTable.Builder> lootTableBiConsumer) {
        HolderLookup.Provider lookup = this.completableLookup.join();

        lootTableBiConsumer.accept(
            CSEntityTypes.SPAWNER_MIMIC.getDefaultLootTable().get(),
            LootTable.lootTable()
                .withPool(
                    LootPool.lootPool()
                        .setRolls(ConstantValue.exactly(1))
                        .add(
                            LootItem.lootTableItem(Items.EXPERIENCE_BOTTLE)
                                .apply(SetItemCountFunction.setCount(ConstantValue.exactly(0)))
                                .apply(EnchantedCountIncreaseFunction.lootingMultiplier(lookup, UniformGenerator.between(0, 3)))
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
