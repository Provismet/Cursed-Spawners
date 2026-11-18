package com.provismet.cursedspawners.datagen;

import com.provismet.cursedspawners.reference.CursedSpawnersLootTables;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.SimpleFabricLootTableSubProvider;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.EmptyLootItem;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.EnchantRandomlyFunction;
import net.minecraft.world.level.storage.loot.functions.EnchantWithLevelsFunction;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.providers.number.ints.ContextIntProviders;

import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;

public class ChestLootTableGenerator extends SimpleFabricLootTableSubProvider {
    private final CompletableFuture<HolderLookup.Provider> future;

    public ChestLootTableGenerator (FabricPackOutput output, CompletableFuture<HolderLookup.Provider> registryLookupFuture) {
        super(output, registryLookupFuture, LootContextParamSets.ALL_PARAMS);
        this.future = registryLookupFuture;
    }

    @Override
    public void generate (BiConsumer<ResourceKey<LootTable>, LootTable.Builder> output) {
        HolderLookup.Provider provider = this.future.join();
        HolderGetter<Enchantment> enchantmentLookup = provider.lookupOrThrow(Registries.ENCHANTMENT);

        output.accept( // Copy of Jungle Temple
            CursedSpawnersLootTables.TIER_1,
            LootTable.lootTable()
                .withPool(LootPool.lootPool()
                    .setRolls(ContextIntProviders.between(2, 6))
                    .add(LootItem.lootTableItem(Items.DIAMOND).setWeight(3).apply(SetItemCountFunction.setCount(ContextIntProviders.between(1, 3))))
                    .add(LootItem.lootTableItem(Items.IRON_INGOT).setWeight(10).apply(SetItemCountFunction.setCount(ContextIntProviders.between(1, 5))))
                    .add(LootItem.lootTableItem(Items.GOLD_INGOT).setWeight(15).apply(SetItemCountFunction.setCount(ContextIntProviders.between(2, 7))))
                    .add(LootItem.lootTableItem(Blocks.BAMBOO).setWeight(15).apply(SetItemCountFunction.setCount(ContextIntProviders.between(1, 3))))
                    .add(LootItem.lootTableItem(Items.EMERALD).setWeight(2).apply(SetItemCountFunction.setCount(ContextIntProviders.between(1, 3))))
                    .add(LootItem.lootTableItem(Items.BONE).setWeight(20).apply(SetItemCountFunction.setCount(ContextIntProviders.between(4, 6))))
                    .add(LootItem.lootTableItem(Items.ROTTEN_FLESH).setWeight(16).apply(SetItemCountFunction.setCount(ContextIntProviders.between(3, 7))))
                    .add(LootItem.lootTableItem(Items.LEATHER).setWeight(3).apply(SetItemCountFunction.setCount(ContextIntProviders.between(1, 5))))
                    .add(LootItem.lootTableItem(Items.COPPER_HORSE_ARMOR))
                    .add(LootItem.lootTableItem(Items.IRON_HORSE_ARMOR))
                    .add(LootItem.lootTableItem(Items.GOLDEN_HORSE_ARMOR))
                    .add(LootItem.lootTableItem(Items.DIAMOND_HORSE_ARMOR))
                    .add(LootItem.lootTableItem(Items.BOOK).apply(EnchantWithLevelsFunction.enchantWithLevels(enchantmentLookup, ContextIntProviders.exactly(30))))                )
                .withPool(LootPool.lootPool()
                    .setRolls(ContextIntProviders.exactly(1))
                    .add(EmptyLootItem.emptyItem().setWeight(2))
                    .add(LootItem.lootTableItem(Items.WILD_ARMOR_TRIM_SMITHING_TEMPLATE).setWeight(1).apply(SetItemCountFunction.setCount(ContextIntProviders.exactly(2))))                )
        );

        output.accept( // Copy of Simple Dungeon
            CursedSpawnersLootTables.TIER_2,
            LootTable.lootTable()
                .withPool(LootPool.lootPool()
                    .setRolls(ContextIntProviders.between(1, 3))
                    .add(LootItem.lootTableItem(Items.LEATHER).setWeight(20).apply(SetItemCountFunction.setCount(ContextIntProviders.between(1, 5))))
                    .add(LootItem.lootTableItem(Items.GOLDEN_APPLE).setWeight(15))
                    .add(LootItem.lootTableItem(Items.ENCHANTED_GOLDEN_APPLE).setWeight(2))
                    .add(LootItem.lootTableItem(Items.MUSIC_DISC_OTHERSIDE).setWeight(2))
                    .add(LootItem.lootTableItem(Items.MUSIC_DISC_13).setWeight(15))
                    .add(LootItem.lootTableItem(Items.MUSIC_DISC_CAT).setWeight(15))
                    .add(LootItem.lootTableItem(Items.NAME_TAG).setWeight(20))
                    .add(LootItem.lootTableItem(Items.GOLDEN_HORSE_ARMOR).setWeight(10))
                    .add(LootItem.lootTableItem(Items.COPPER_HORSE_ARMOR).setWeight(15))
                    .add(LootItem.lootTableItem(Items.IRON_HORSE_ARMOR).setWeight(15))
                    .add(LootItem.lootTableItem(Items.DIAMOND_HORSE_ARMOR).setWeight(5))
                    .add(LootItem.lootTableItem(Items.BOOK).setWeight(10).apply(EnchantRandomlyFunction.randomApplicableEnchantment(enchantmentLookup))))
                .withPool(LootPool.lootPool()
                    .setRolls(ContextIntProviders.between(1, 4))
                    .add(LootItem.lootTableItem(Items.IRON_INGOT).setWeight(10).apply(SetItemCountFunction.setCount(ContextIntProviders.between(1, 4))))
                    .add(LootItem.lootTableItem(Items.GOLD_INGOT).setWeight(5).apply(SetItemCountFunction.setCount(ContextIntProviders.between(1, 4))))
                    .add(LootItem.lootTableItem(Items.BREAD).setWeight(20))
                    .add(LootItem.lootTableItem(Items.WHEAT).setWeight(20).apply(SetItemCountFunction.setCount(ContextIntProviders.between(1, 4))))
                    .add(LootItem.lootTableItem(Items.BUCKET).setWeight(10))
                    .add(LootItem.lootTableItem(Items.REDSTONE).setWeight(15).apply(SetItemCountFunction.setCount(ContextIntProviders.between(1, 4))))
                    .add(LootItem.lootTableItem(Items.COAL).setWeight(15).apply(SetItemCountFunction.setCount(ContextIntProviders.between(1, 4))))
                    .add(LootItem.lootTableItem(Items.MELON_SEEDS).setWeight(10).apply(SetItemCountFunction.setCount(ContextIntProviders.between(2, 4))))
                    .add(LootItem.lootTableItem(Items.PUMPKIN_SEEDS).setWeight(10).apply(SetItemCountFunction.setCount(ContextIntProviders.between(2, 4))))
                    .add(LootItem.lootTableItem(Items.BEETROOT_SEEDS).setWeight(10).apply(SetItemCountFunction.setCount(ContextIntProviders.between(2, 4)))))
                .withPool(LootPool.lootPool()
                    .setRolls(ContextIntProviders.exactly(3))
                    .add(LootItem.lootTableItem(Items.BONE).setWeight(10).apply(SetItemCountFunction.setCount(ContextIntProviders.between(1, 8))))
                    .add(LootItem.lootTableItem(Items.GUNPOWDER).setWeight(10).apply(SetItemCountFunction.setCount(ContextIntProviders.between(1, 8))))
                    .add(LootItem.lootTableItem(Items.ROTTEN_FLESH).setWeight(10).apply(SetItemCountFunction.setCount(ContextIntProviders.between(1, 8))))
                    .add(LootItem.lootTableItem(Items.STRING).setWeight(10).apply(SetItemCountFunction.setCount(ContextIntProviders.between(1, 8)))))
        );

        output.accept( // Copy of Woodland Mansion
            CursedSpawnersLootTables.TIER_3,
            LootTable.lootTable()
                .withPool(LootPool.lootPool()
                    .setRolls(ContextIntProviders.between(1, 3))
                    .add(LootItem.lootTableItem(Items.LEAD).setWeight(20))
                    .add(LootItem.lootTableItem(Items.GOLDEN_APPLE).setWeight(15))
                    .add(LootItem.lootTableItem(Items.ENCHANTED_GOLDEN_APPLE).setWeight(2))
                    .add(LootItem.lootTableItem(Items.MUSIC_DISC_13).setWeight(15))
                    .add(LootItem.lootTableItem(Items.MUSIC_DISC_CAT).setWeight(15))
                    .add(LootItem.lootTableItem(Items.CHAINMAIL_CHESTPLATE).setWeight(10))
                    .add(LootItem.lootTableItem(Items.DIAMOND_HOE).setWeight(15))
                    .add(LootItem.lootTableItem(Items.DIAMOND_CHESTPLATE).setWeight(5))
                    .add(LootItem.lootTableItem(Items.BOOK).setWeight(10).apply(EnchantRandomlyFunction.randomApplicableEnchantment(enchantmentLookup))))
                .withPool(LootPool.lootPool()
                    .setRolls(ContextIntProviders.between(1, 4))
                    .add(LootItem.lootTableItem(Items.IRON_INGOT).setWeight(10).apply(SetItemCountFunction.setCount(ContextIntProviders.between(1, 4))))
                    .add(LootItem.lootTableItem(Items.GOLD_INGOT).setWeight(5).apply(SetItemCountFunction.setCount(ContextIntProviders.between(1, 4))))
                    .add(LootItem.lootTableItem(Items.BREAD).setWeight(20))
                    .add(LootItem.lootTableItem(Items.WHEAT).setWeight(20).apply(SetItemCountFunction.setCount(ContextIntProviders.between(1, 4))))
                    .add(LootItem.lootTableItem(Items.BUCKET).setWeight(10))
                    .add(LootItem.lootTableItem(Items.REDSTONE).setWeight(15).apply(SetItemCountFunction.setCount(ContextIntProviders.between(1, 4))))
                    .add(LootItem.lootTableItem(Items.COAL).setWeight(15).apply(SetItemCountFunction.setCount(ContextIntProviders.between(1, 4))))
                    .add(LootItem.lootTableItem(Items.MELON_SEEDS).setWeight(10).apply(SetItemCountFunction.setCount(ContextIntProviders.between(2, 4))))
                    .add(LootItem.lootTableItem(Items.PUMPKIN_SEEDS).setWeight(10).apply(SetItemCountFunction.setCount(ContextIntProviders.between(2, 4))))
                    .add(LootItem.lootTableItem(Items.BEETROOT_SEEDS).setWeight(10).apply(SetItemCountFunction.setCount(ContextIntProviders.between(2, 4))))
                    .add(LootItem.lootTableItem(Items.RESIN_CLUMP).setWeight(50).apply(SetItemCountFunction.setCount(ContextIntProviders.between(2, 4)))))
                .withPool(LootPool.lootPool()
                    .setRolls(ContextIntProviders.exactly(3))
                    .add(LootItem.lootTableItem(Items.BONE).setWeight(10).apply(SetItemCountFunction.setCount(ContextIntProviders.between(1, 8))))
                    .add(LootItem.lootTableItem(Items.GUNPOWDER).setWeight(10).apply(SetItemCountFunction.setCount(ContextIntProviders.between(1, 8))))
                    .add(LootItem.lootTableItem(Items.ROTTEN_FLESH).setWeight(10).apply(SetItemCountFunction.setCount(ContextIntProviders.between(1, 8))))
                    .add(LootItem.lootTableItem(Items.STRING).setWeight(10).apply(SetItemCountFunction.setCount(ContextIntProviders.between(1, 8)))))
                .withPool(LootPool.lootPool()
                    .setRolls(ContextIntProviders.exactly(1))
                    .add(EmptyLootItem.emptyItem().setWeight(1))
                    .add(LootItem.lootTableItem(Items.VEX_ARMOR_TRIM_SMITHING_TEMPLATE).setWeight(1)))
        );
    }
}
