package com.provismet.cursedspawners.mixin;

import com.mojang.serialization.Codec;
import com.provismet.cursedspawners.imixin.IMixinMobSpawnerBlockEntity;
import com.provismet.cursedspawners.imixin.IMixinMobSpawnerLogic;
import com.provismet.cursedspawners.utility.CSGamerules;
import com.provismet.cursedspawners.utility.SpawnerBreakEffects;
import com.provismet.cursedspawners.utility.SpawnerEffects;
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
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
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

@Mixin(SpawnerBlockEntity.class)
public abstract class MobSpawnerBlockEntityMixin extends BlockEntity implements IMixinMobSpawnerBlockEntity, RandomizableContainer {
    @Shadow @Final private BaseSpawner spawner;

    @Unique private static final String REFORGE_ACTIONS = "ReforgeActions";
    @Unique private static final String BREAK_ACTION = "BreakAction";
    @Unique private static final String RANDOMISE = "ShouldGenerateEffects";

    @Unique private static final String MIMIC_CHANCE = "MimicChance";
    @Unique private static final double PASSTHROUGH_MIMIC_CHANCE = -1;

    public MobSpawnerBlockEntityMixin (BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Unique private final NonNullList<ItemStack> inventory = NonNullList.withSize(27, ItemStack.EMPTY);

    @Unique private long lootTableSeed = 0;
    @Unique private ResourceKey<LootTable> lootTable = null;
    @Unique private double mimicChance = PASSTHROUGH_MIMIC_CHANCE;
    @Unique private List<String> reforgeActions = new ArrayList<>();
    @Unique private String breakAction = SpawnerBreakEffects.NORMAL_BREAK;

    @Unique private boolean shouldRandomiseEffects = true;

    @Inject(method="loadAdditional", at=@At("TAIL"))
    private void readExtendedNbt (ValueInput view, CallbackInfo ci) {
        this.mimicChance = view.getDoubleOr(MIMIC_CHANCE, PASSTHROUGH_MIMIC_CHANCE);
        this.shouldRandomiseEffects = view.getBooleanOr(RANDOMISE, true);
        this.breakAction = view.getStringOr(BREAK_ACTION, SpawnerBreakEffects.NORMAL_BREAK);

        view.read(REFORGE_ACTIONS, Codec.STRING.listOf()).ifPresentOrElse(
            actions -> this.reforgeActions = new ArrayList<>(actions),
            () -> this.reforgeActions = new ArrayList<>()
        );

        this.tryLoadLootTable(view);
    }

    @Inject(method="saveAdditional", at=@At("TAIL"))
    private void writeExtendedNbt (ValueOutput view, CallbackInfo ci) {
        view.putDouble(MIMIC_CHANCE, this.mimicChance);
        view.store(REFORGE_ACTIONS, Codec.STRING.listOf(), this.reforgeActions);
        view.putString(BREAK_ACTION, this.breakAction);
        view.putBoolean(RANDOMISE, this.shouldRandomiseEffects);
        this.trySaveLootTable(view);
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
    private static void tick (Level world, BlockPos pos, BlockState state, SpawnerBlockEntity blockEntity, CallbackInfo info) {
        MobSpawnerBlockEntityMixin self = (MobSpawnerBlockEntityMixin)(Object)blockEntity;
        if (self != null && self.shouldRandomiseEffects && self.hasLevel() && world instanceof ServerLevel serverWorld && serverWorld.getGameRules().get(CSGamerules.SPAWNER_ACTION_CHANCE) > 0) {
            self.generateEffects(serverWorld);
            self.setChanged();
        }
    }

    @Unique
    private void generateEffects (ServerLevel world) {
        this.shouldRandomiseEffects = false;
        RandomSource random = world.getRandom();

        int dangerLevel = 0;
        double chanceToAddAction = world.getGameRules().get(CSGamerules.SPAWNER_ACTION_CHANCE);
        while (dangerLevel < 7) {
            if (random.nextDouble() < chanceToAddAction) ++dangerLevel;
            else break;
        }

        for (int i = 0; i < dangerLevel; ++i) {
            IMixinMobSpawnerLogic mixinLogic = (IMixinMobSpawnerLogic)this.spawner;

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
                    random.nextIntBetweenInclusive(100, 160),
                    random.triangle(1.5, 0.5),
                    random.triangle(5, 0.75)
                );
            }
            else if (chosen == SpawnerEffects.HEAL) {
                mixinLogic.cursed_spawners$setCanHeal(true);
                mixinLogic.cursed_spawners$setHealParams(
                    random.nextIntBetweenInclusive(80, 160),
                    2f,
                    random.triangle(5, 1)
                );
            }
            else if (chosen == SpawnerEffects.BOOST) {
                mixinLogic.cursed_spawners$setCanBoost(true);
                mixinLogic.cursed_spawners$setBoostParams(
                    random.nextIntBetweenInclusive(120, 200),
                    random.triangle(8, 4)
                );
            }
        }

        if (dangerLevel > 0 && this.lootTable == null) {
            if (dangerLevel < 3) this.lootTable = BuiltInLootTables.JUNGLE_TEMPLE;
            else if (dangerLevel < 6) this.lootTable = BuiltInLootTables.SIMPLE_DUNGEON;
            else this.lootTable = BuiltInLootTables.WOODLAND_MANSION;
        }
    }

    @Override
    public boolean cursed_spawners$attemptBreak () {
        if (!(this.level instanceof ServerLevel serverWorld)) return true;
        if (this.reforgeActions.isEmpty()) {
            SpawnerBreakEffects.getEffect(this.breakAction).accept((SpawnerBlockEntity)(Object)this, serverWorld);
            return true;
        }

        String nextAction = this.reforgeActions.removeFirst();
        Vec3 centrePos = this.getBlockPos().getCenter();
        serverWorld.sendParticles(ParticleTypes.POOF, centrePos.x(), centrePos.y(), centrePos.z(), 20, 0.5, 0.5, 0.5, 0);
        SpawnerBreakEffects.getEffect(nextAction).accept((SpawnerBlockEntity)(Object)this, serverWorld);
        return false;
    }

    @Override
    public void cursed_spawners$setShouldGenerateEffects (boolean value) {
        this.shouldRandomiseEffects = value;
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
    public ItemStack getItem (int slot) {
        this.unpackLootTable(null);
        if (this.inventory.size() <= slot) return ItemStack.EMPTY;
        return this.inventory.get(slot);
    }

    @Override
    public ItemStack removeItem (int slot, int amount) {
        this.unpackLootTable(null);
        ItemStack itemStack = ContainerHelper.removeItem(this.inventory, slot, amount);
        if (!itemStack.isEmpty()) {
            this.setChanged();
        }
        return itemStack;
    }

    @Override
    public ItemStack removeItemNoUpdate (int slot) {
        this.unpackLootTable(null);
        return ContainerHelper.takeItem(this.inventory, slot);
    }

    @Override
    public void setItem (int slot, ItemStack stack) {
        this.unpackLootTable(null);
        this.inventory.set(slot, stack);
        stack.limitSize(this.getMaxStackSize(stack));
        this.setChanged();
    }

    @Override
    public boolean stillValid (Player player) {
        return false;
    }

    @Override
    public void clearContent () {
        this.inventory.clear();
    }

    @Override
    protected void applyImplicitComponents (DataComponentGetter components) {
        super.applyImplicitComponents(components);
        SeededContainerLoot containerLootComponent = components.get(DataComponents.CONTAINER_LOOT);
        if (containerLootComponent != null) {
            this.lootTable = containerLootComponent.lootTable();
            this.lootTableSeed = containerLootComponent.seed();
        }
    }

    @Override
    protected void collectImplicitComponents (DataComponentMap.Builder componentMapBuilder) {
        super.collectImplicitComponents(componentMapBuilder);
        if (this.lootTable != null) {
            componentMapBuilder.set(DataComponents.CONTAINER_LOOT, new SeededContainerLoot(this.lootTable, this.lootTableSeed));
        }
    }
}
