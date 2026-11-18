package com.provismet.cursedspawners.mixin;

import com.provismet.cursedspawners.CursedSpawnersMain;
import com.provismet.cursedspawners.action.ActionGroup;
import com.provismet.cursedspawners.action.SpawnerData;
import com.provismet.cursedspawners.action.destroy.SpawnerBreakEffect;
import com.provismet.cursedspawners.action.passive.SpawnerPassiveEffect;
import com.provismet.cursedspawners.imixin.IMixinSpawnerBlockEntity;
import com.provismet.cursedspawners.registries.CursedSpawnersRegistries;
import com.provismet.cursedspawners.registries.SpawnerPassiveEffects;
import com.provismet.cursedspawners.utility.CSGamerules;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.RandomizableContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.SeededContainerLoot;
import net.minecraft.world.level.BaseSpawner;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.SpawnerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
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
import java.util.function.Function;

@Mixin(SpawnerBlockEntity.class)
public abstract class SpawnerBlockEntityMixin extends BlockEntity implements IMixinSpawnerBlockEntity, RandomizableContainer {
    @Shadow @Final private BaseSpawner spawner;

    @Unique private SpawnerData spawnerData = SpawnerData.DEFAULT;
    @Unique private final NonNullList<ItemStack> inventory = NonNullList.withSize(27, ItemStack.EMPTY);
    @Unique private long lootTableSeed = 0;
    @Unique private ResourceKey<LootTable> lootTable = null;

    public SpawnerBlockEntityMixin (BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Inject(method="loadAdditional", at=@At("TAIL"))
    private void readExtendedNbt (ValueInput input, CallbackInfo ci) {
        this.spawnerData = input.read(CursedSpawnersMain.MODID, SpawnerData.CODEC).orElse(SpawnerData.DEFAULT);
        this.tryLoadLootTable(input);
    }

    @Inject(method="saveAdditional", at=@At("TAIL"))
    private void writeExtendedNbt (ValueOutput output, CallbackInfo ci) {
        output.store(CursedSpawnersMain.MODID, SpawnerData.CODEC, this.spawnerData.withActionGroup(this.spawner.cursed_spawners$getActionGroup()));
        this.trySaveLootTable(output);
    }

    @Override
    @NotNull
    public SpawnerData cursed_spawners$getSpawnerData () {
        return this.spawnerData;
    }

    @Inject(method="serverTick", at=@At("HEAD"))
    private static void initialSetup (Level level, BlockPos pos, BlockState state, SpawnerBlockEntity entity, CallbackInfo info) {
        SpawnerBlockEntityMixin self = (SpawnerBlockEntityMixin)(Object) entity;
        if (self != null && self.spawnerData.shouldGenerateEffects() && self.hasLevel() && level instanceof ServerLevel serverWorld && serverWorld.getGameRules().get(CSGamerules.SPAWNER_ACTION_CHANCE) > 0) {
            self.generateEffects(serverWorld);
            self.setChanged();
        }
    }

    @Unique
    private void generateEffects (ServerLevel level) {
        this.spawnerData = this.spawnerData.withoutEffectGeneration();
        RandomSource random = level.getRandom();

        int dangerLevel = 0;
        double chanceToAddAction = level.getGameRules().get(CSGamerules.SPAWNER_ACTION_CHANCE);
        while (dangerLevel < level.getGameRules().get(CSGamerules.MAX_SPAWNER_ACTIONS)) {
            if (random.nextDouble() < chanceToAddAction) ++dangerLevel;
            else break;
        }

        if (dangerLevel == 0) {
            this.spawner.cursed_spawners$setActionGroup(ActionGroup.DEFAULT);
            this.lootTable = null;
            return;
        }

        List<SpawnerBreakEffect> breakEffects = new ArrayList<>();
        List<SpawnerPassiveEffect> passiveEffects = new ArrayList<>();
        for (int i = 0; i < dangerLevel; ++i) {
            if (random.nextBoolean()) { // Select a break effect
                List<SpawnerBreakEffect> possibilities = CursedSpawnersRegistries.SPAWNER_BREAK_EFFECT.stream().toList();
                breakEffects.add(possibilities.get(random.nextInt(possibilities.size())));
            }
            else { // Select a passive effect
                List<Function<RandomSource,SpawnerPassiveEffect>> possibilities = SpawnerPassiveEffects.streamFactories().toList();
                passiveEffects.add(possibilities.get(random.nextInt(possibilities.size())).apply(random));
            }
        }

        ActionGroup group = new ActionGroup(passiveEffects, breakEffects);
        this.spawner.cursed_spawners$setActionGroup(group);

        // TODO: These should be custom loot tables.
        if (dangerLevel > 0 && this.lootTable == null) {
            if (dangerLevel < 3) this.lootTable = BuiltInLootTables.JUNGLE_TEMPLE;
            else if (dangerLevel < 6) this.lootTable = BuiltInLootTables.SIMPLE_DUNGEON;
            else this.lootTable = BuiltInLootTables.WOODLAND_MANSION;
        }
    }

    @Override
    public boolean cursed_spawners$attemptBreak (ServerPlayer player) {
        if (!(this.level instanceof ServerLevel serverWorld)) return true;
        ActionGroup actionGroup = this.spawner.cursed_spawners$getActionGroup();

        SpawnerBreakEffect breakEffect = actionGroup.getNextBreakEffect();
        if (breakEffect != null) {
            breakEffect.onBreak((SpawnerBlockEntity)(Object)this, serverWorld, player);
        }

        if (actionGroup.canReforge()) {
            Vec3 centrePos = Vec3.atCenterOf(this.getBlockPos());
            serverWorld.sendParticles(ParticleTypes.POOF, centrePos.x(), centrePos.y(), centrePos.z(), 20, 0.5, 0.5, 0.5, 0);
            this.spawner.cursed_spawners$setActionGroup(actionGroup.popBreakEffect());
            return false;
        }

        this.spawner.cursed_spawners$setActionGroup(actionGroup.popBreakEffect());
        return true;
    }

    @Nullable
    @Override
    public ResourceKey<LootTable> getLootTable () {
        return this.lootTable;
    }

    @Override
    public void setLootTable (@Nullable ResourceKey<LootTable> lootTable) {
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
    public int getContainerSize () {
        this.unpackLootTable(null);
        return this.inventory.size();
    }

    @Override
    public boolean isEmpty () {
        this.unpackLootTable(null);
        return this.inventory.isEmpty();
    }

    @Override
    @NotNull
    public ItemStack getItem (int slot) {
        this.unpackLootTable(null);
        if (this.inventory.size() <= slot) return ItemStack.EMPTY;
        return this.inventory.get(slot);
    }

    @Override
    @NotNull
    public ItemStack removeItem (int slot, int amount) {
        this.unpackLootTable(null);
        ItemStack itemStack = ContainerHelper.removeItem(this.inventory, slot, amount);
        if (!itemStack.isEmpty()) {
            this.setChanged();
        }
        return itemStack;
    }

    @Override
    @NotNull
    public ItemStack removeItemNoUpdate (int slot) {
        this.unpackLootTable(null);
        return ContainerHelper.takeItem(this.inventory, slot);
    }

    @Override
    public void setItem (int slot, @NotNull ItemStack stack) {
        this.unpackLootTable(null);
        this.inventory.set(slot, stack);
        stack.limitSize(this.getMaxStackSize(stack));
        this.setChanged();
    }

    @Override
    public boolean stillValid (@NotNull Player player) {
        return false;
    }

    @Override
    public void clearContent () {
        this.inventory.clear();
    }

    @Override
    protected void applyImplicitComponents (@NotNull DataComponentGetter components) {
        super.applyImplicitComponents(components);
        SeededContainerLoot containerLootComponent = components.get(DataComponents.CONTAINER_LOOT);
        if (containerLootComponent != null) {
            this.lootTable = containerLootComponent.lootTable();
            this.lootTableSeed = containerLootComponent.seed();
        }
    }

    @Override
    protected void collectImplicitComponents (@NotNull DataComponentMap.Builder componentMapBuilder) {
        super.collectImplicitComponents(componentMapBuilder);
        if (this.lootTable != null) {
            componentMapBuilder.set(DataComponents.CONTAINER_LOOT, new SeededContainerLoot(this.lootTable, this.lootTableSeed));
        }
    }
}
