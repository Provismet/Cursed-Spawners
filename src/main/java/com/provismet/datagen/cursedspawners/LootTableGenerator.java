package com.provismet.datagen.cursedspawners;

import com.provismet.cursedspawners.registries.CSEntityTypes;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.SimpleFabricLootTableProvider;
import net.minecraft.item.Items;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.context.LootContextTypes;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.loot.function.EnchantedCountIncreaseLootFunction;
import net.minecraft.loot.function.SetCountLootFunction;
import net.minecraft.loot.provider.number.BinomialLootNumberProvider;
import net.minecraft.loot.provider.number.ConstantLootNumberProvider;
import net.minecraft.loot.provider.number.UniformLootNumberProvider;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryWrapper;

import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;

public class LootTableGenerator extends SimpleFabricLootTableProvider {
    CompletableFuture<RegistryWrapper.WrapperLookup> completableLookup;

    public LootTableGenerator (FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registryLookup) {
        super(output, registryLookup, LootContextTypes.ENTITY);
        this.completableLookup = registryLookup;
    }

    @Override
    public void accept (BiConsumer<RegistryKey<LootTable>, LootTable.Builder> lootTableBiConsumer) {
        RegistryWrapper.WrapperLookup lookup = this.completableLookup.join();

        lootTableBiConsumer.accept(
            CSEntityTypes.SPAWNER_MIMIC.getLootTableId(),
            LootTable.builder()
                .pool(
                    LootPool.builder()
                        .rolls(ConstantLootNumberProvider.create(1))
                        .with(
                            ItemEntry.builder(Items.EXPERIENCE_BOTTLE)
                                .apply(SetCountLootFunction.builder(ConstantLootNumberProvider.create(0)))
                                .apply(EnchantedCountIncreaseLootFunction.builder(lookup, UniformLootNumberProvider.create(0, 3)))
                        )
                )
                .pool(
                    LootPool.builder()
                        .rolls(BinomialLootNumberProvider.create(1, 0.5f))
                        .with(
                            ItemEntry.builder(Items.IRON_BARS)
                                .apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1, 2)))
                        )
                )
        );
    }
}
