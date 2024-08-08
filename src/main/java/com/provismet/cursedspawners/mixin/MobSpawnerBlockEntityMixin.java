package com.provismet.cursedspawners.mixin;

import com.provismet.cursedspawners.imixin.IMixinMobSpawnerBlockEntity;
import com.provismet.cursedspawners.imixin.IMixinMobSpawnerLogic;
import com.provismet.cursedspawners.utility.CSGamerules;
import com.provismet.cursedspawners.utility.SpawnerBreakEffects;
import com.provismet.cursedspawners.utility.SpawnerEffects;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.MobSpawnerBlockEntity;
import net.minecraft.block.spawner.MobSpawnerLogic;
import net.minecraft.component.ComponentMap;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ContainerLootComponent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.LootableInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.LootTables;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Mixin(MobSpawnerBlockEntity.class)
public abstract class MobSpawnerBlockEntityMixin extends BlockEntity implements IMixinMobSpawnerBlockEntity, LootableInventory {
    @Shadow @Final private MobSpawnerLogic logic;

    @Unique private static final String REFORGE_ACTIONS = "ReforgeActions";
    @Unique private static final String BREAK_ACTION = "BreakAction";
    @Unique private static final String RANDOMISE = "ShouldGenerateEffects";

    @Unique private static final String MIMIC_CHANCE = "MimicChance";
    @Unique private static final double PASSTHROUGH_MIMIC_CHANCE = -1;

    public MobSpawnerBlockEntityMixin (BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Unique private final DefaultedList<ItemStack> inventory = DefaultedList.ofSize(27, ItemStack.EMPTY);

    @Unique private long lootTableSeed = 0;
    @Unique private RegistryKey<LootTable> lootTable = null;
    @Unique private double mimicChance = PASSTHROUGH_MIMIC_CHANCE;
    @Unique private List<String> reforgeActions = new ArrayList<>();
    @Unique private String breakAction = SpawnerBreakEffects.NORMAL_BREAK;

    @Unique private boolean shouldRandomiseEffects = true;

    @Inject(method="readNbt", at=@At("TAIL"))
    private void readExtendedNbt (NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup, CallbackInfo info) {
        if (nbt.contains(MIMIC_CHANCE, NbtElement.DOUBLE_TYPE)) this.mimicChance = nbt.getDouble(MIMIC_CHANCE);
        else this.mimicChance = PASSTHROUGH_MIMIC_CHANCE;

        if (nbt.contains(RANDOMISE)) this.shouldRandomiseEffects = nbt.getBoolean(RANDOMISE);

        if (nbt.contains(REFORGE_ACTIONS, NbtElement.LIST_TYPE)) {
            reforgeActions = new ArrayList<>();
            reforgeActions.addAll(nbt.getList(REFORGE_ACTIONS, NbtElement.STRING_TYPE).stream().map(NbtElement::asString).toList());
        } else this.reforgeActions = new ArrayList<>();

        if (nbt.contains(BREAK_ACTION, NbtElement.STRING_TYPE)) this.breakAction = nbt.getString(BREAK_ACTION);
        else this.breakAction = SpawnerBreakEffects.NORMAL_BREAK;

        this.readLootTable(nbt);
    }

    @Inject(method="writeNbt", at=@At("TAIL"))
    private void writeExtendedNbt (NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup, CallbackInfo info) {
        nbt.putDouble(MIMIC_CHANCE, this.mimicChance);

        NbtList actions = new NbtList();
        for (String action : this.reforgeActions) {
            actions.add(NbtString.of(action));
        }
        nbt.put(REFORGE_ACTIONS, actions);
        nbt.putString(BREAK_ACTION, this.breakAction);
        nbt.putBoolean(RANDOMISE, this.shouldRandomiseEffects);

        this.writeLootTable(nbt);
    }

    @Override
    public double cursed_spawners$getMimicChance () {
        return this.mimicChance;
    }

    @Override
    public boolean cursed_spawners$useWorldMimicChance () {
        return this.mimicChance == PASSTHROUGH_MIMIC_CHANCE;
    }

    @Inject(method="serverTick", at=@At("HEAD"))
    private static void tick (World world, BlockPos pos, BlockState state, MobSpawnerBlockEntity blockEntity, CallbackInfo info) {
        MobSpawnerBlockEntityMixin self = (MobSpawnerBlockEntityMixin)(Object)blockEntity;
        if (self.shouldRandomiseEffects && self.hasWorld() && world.getGameRules().get(CSGamerules.SPAWNER_ACTION_CHANCE).get() > 0) {
            self.generateEffects(world);
            self.markDirty();
        }
    }

    @Unique
    private void generateEffects (World world) {
        this.shouldRandomiseEffects = false;
        Random random = world.getRandom();

        int dangerLevel = 0;
        double chanceToAddAction = world.getGameRules().get(CSGamerules.SPAWNER_ACTION_CHANCE).get();
        while (dangerLevel < 7) {
            if (random.nextDouble() < chanceToAddAction) ++dangerLevel;
            else break;
        }

        for (int i = 0; i < dangerLevel; ++i) {
            IMixinMobSpawnerLogic mixinLogic = (IMixinMobSpawnerLogic)this.logic;

            List<SpawnerEffects> possibleEffects = new ArrayList<>();
            possibleEffects.add(SpawnerEffects.REFORGE);
            if (Objects.equals(this.breakAction, SpawnerBreakEffects.NORMAL_BREAK)) possibleEffects.add(SpawnerEffects.BREAK);
            if (!mixinLogic.cursed_spawners$getCanKnockback()) possibleEffects.add(SpawnerEffects.KNOCKBACK);
            if (!mixinLogic.cursed_spawners$getCanHeal()) possibleEffects.add(SpawnerEffects.HEAL);
            if (!mixinLogic.cursed_spawners$getCanBoost()) possibleEffects.add(SpawnerEffects.BOOST);

            SpawnerEffects chosen = possibleEffects.get(random.nextInt(possibleEffects.size()));
            if (chosen == SpawnerEffects.BREAK) this.breakAction = SpawnerBreakEffects.getRandomEffectKey(random);
            else if (chosen == SpawnerEffects.REFORGE) this.reforgeActions.add(SpawnerBreakEffects.getRandomEffectKey(random));
            else if (chosen == SpawnerEffects.KNOCKBACK) {
                mixinLogic.cursed_spawners$setCanKnockback(true);
                mixinLogic.cursed_spawners$setKnockbackParams(
                    random.nextBetween(100, 160),
                    random.nextTriangular(1.5, 0.5),
                    random.nextTriangular(5, 0.75)
                );
            }
            else if (chosen == SpawnerEffects.HEAL) {
                mixinLogic.cursed_spawners$setCanHeal(true);
                mixinLogic.cursed_spawners$setHealParams(
                    random.nextBetween(80, 160),
                    2f,
                    random.nextTriangular(5, 1)
                );
            }
            else if (chosen == SpawnerEffects.BOOST) {
                mixinLogic.cursed_spawners$setCanBoost(true);
                mixinLogic.cursed_spawners$setBoostParams(
                    random.nextBetween(120, 200),
                    random.nextTriangular(8, 4)
                );
            }
        }

        if (dangerLevel > 0 && this.lootTable == null) {
            if (dangerLevel < 3) this.lootTable = LootTables.JUNGLE_TEMPLE_CHEST;
            else if (dangerLevel < 6) this.lootTable = LootTables.SIMPLE_DUNGEON_CHEST;
            else this.lootTable = LootTables.WOODLAND_MANSION_CHEST;
        }
    }

    @Override
    public boolean cursed_spawners$attemptBreak () {
        if (!(this.world instanceof ServerWorld serverWorld)) return true;
        if (this.reforgeActions.isEmpty()) {
            SpawnerBreakEffects.getEffect(this.breakAction).accept((MobSpawnerBlockEntity)(Object)this, serverWorld);
            return true;
        }

        String nextAction = this.reforgeActions.removeFirst();
        Vec3d centrePos = this.getPos().toCenterPos();
        serverWorld.spawnParticles(ParticleTypes.POOF, centrePos.getX(), centrePos.getY(), centrePos.getZ(), 20, 0.5, 0.5, 0.5, 0);
        SpawnerBreakEffects.getEffect(nextAction).accept((MobSpawnerBlockEntity)(Object)this, serverWorld);
        return false;
    }

    @Override
    public void cursed_spawners$setShouldGenerateEffects (boolean value) {
        this.shouldRandomiseEffects = value;
    }

    @Nullable
    @Override
    public RegistryKey<LootTable> getLootTable () {
        return this.lootTable;
    }

    @Override
    public void setLootTable (@Nullable RegistryKey<LootTable> lootTable) {
        this.lootTable = lootTable;
    }

    @Override
    public long getLootTableSeed () {
        return this.lootTableSeed;
    }

    @Override
    public void setLootTableSeed (long lootTableSeed) {
        this.lootTableSeed = lootTableSeed;
    }

    @Override
    public int size () {
        this.generateLoot(null);
        return this.inventory.size();
    }

    @Override
    public boolean isEmpty () {
        this.generateLoot(null);
        return this.inventory.isEmpty();
    }

    @Override
    public ItemStack getStack (int slot) {
        this.generateLoot(null);
        if (this.inventory.size() <= slot) return ItemStack.EMPTY;
        return this.inventory.get(slot);
    }

    @Override
    public ItemStack removeStack (int slot, int amount) {
        this.generateLoot(null);
        ItemStack itemStack = Inventories.splitStack(this.inventory, slot, amount);
        if (!itemStack.isEmpty()) {
            this.markDirty();
        }
        return itemStack;
    }

    @Override
    public ItemStack removeStack (int slot) {
        this.generateLoot(null);
        return Inventories.removeStack(this.inventory, slot);
    }

    @Override
    public void setStack (int slot, ItemStack stack) {
        this.generateLoot(null);
        this.inventory.set(slot, stack);
        stack.capCount(this.getMaxCount(stack));
        this.markDirty();
    }

    @Override
    public boolean canPlayerUse (PlayerEntity player) {
        return false;
    }

    @Override
    public void clear () {
        this.inventory.clear();
    }

    @Override
    protected void readComponents (BlockEntity.ComponentsAccess components) {
        super.readComponents(components);
        ContainerLootComponent containerLootComponent = components.get(DataComponentTypes.CONTAINER_LOOT);
        if (containerLootComponent != null) {
            this.lootTable = containerLootComponent.lootTable();
            this.lootTableSeed = containerLootComponent.seed();
        }
    }

    @Override
    protected void addComponents (ComponentMap.Builder componentMapBuilder) {
        super.addComponents(componentMapBuilder);
        if (this.lootTable != null) {
            componentMapBuilder.add(DataComponentTypes.CONTAINER_LOOT, new ContainerLootComponent(this.lootTable, this.lootTableSeed));
        }
    }
}
