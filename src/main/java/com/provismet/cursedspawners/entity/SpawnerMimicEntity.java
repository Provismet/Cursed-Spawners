package com.provismet.cursedspawners.entity;

import com.provismet.cursedspawners.CursedSpawnersMain;
import com.provismet.cursedspawners.registries.CSEntityTypes;
import com.provismet.cursedspawners.registries.CSSoundEvents;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.spawner.MobSpawnerEntry;
import net.minecraft.entity.AnimationState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityStatuses;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.SpawnRestriction;
import net.minecraft.entity.ai.goal.ActiveTargetGoal;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.LookAroundGoal;
import net.minecraft.entity.ai.goal.LookAtEntityGoal;
import net.minecraft.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.entity.ai.goal.RevengeGoal;
import net.minecraft.entity.ai.goal.WanderAroundFarGoal;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.registry.Registries;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypeFilter;
import net.minecraft.util.collection.DataPool;
import net.minecraft.util.collection.Weighted;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.Difficulty;
import net.minecraft.world.World;
import net.minecraft.world.WorldEvents;
import net.minecraft.world.event.GameEvent;
import org.jetbrains.annotations.Nullable;

import java.util.EnumSet;
import java.util.Optional;
import java.util.function.Function;

public class SpawnerMimicEntity extends HostileEntity {
    private int spawnDelay = 20;
    private DataPool<MobSpawnerEntry> spawnPotentials = DataPool.<MobSpawnerEntry>empty();
    private MobSpawnerEntry spawnEntry;
    private double mobEntryRotation;
    private double prevMobEntryRotation;
    private int minSpawnDelay = 200;
    private int maxSpawnDelay = 800;
    private int spawnCount = 4;
    private Entity renderedEntity;
    private int maxNearbyEntities = 6;
    private int requiredPlayerRange = 16;
    private int spawnRange = 4;

    public final AnimationState idleState = new AnimationState();
    public final AnimationState attackState = new AnimationState();
    public final AnimationState spawnState = new AnimationState();

    private static final TrackedData<Boolean> RUNNING_SPAWN_ANIMATION = DataTracker.registerData(SpawnerMimicEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    private static final TrackedData<NbtCompound> RENDERED_ENTITY = DataTracker.registerData(SpawnerMimicEntity.class, TrackedDataHandlerRegistry.NBT_COMPOUND);

    public SpawnerMimicEntity (World world) {
        this(CSEntityTypes.SPAWNER_MIMIC, world);
    }

    public SpawnerMimicEntity (EntityType<? extends SpawnerMimicEntity> entityType, World world) {
        super(entityType, world);
        this.experiencePoints = 30;
    }

    public static DefaultAttributeContainer.Builder getSpawnerMimicAttributes () {
        return HostileEntity.createHostileAttributes()
            .add(EntityAttributes.GENERIC_MAX_HEALTH, 10)
            .add(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE, 0.8)
            .add(EntityAttributes.GENERIC_ARMOR, 25)
            .add(EntityAttributes.GENERIC_ARMOR_TOUGHNESS, 5)
            .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.275)
            .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 2)
            .add(EntityAttributes.GENERIC_ATTACK_KNOCKBACK, 1);
    }

    @Override
    protected void initGoals () {
        super.initGoals();
        this.targetSelector.add(0, new RevengeGoal(this));
        this.targetSelector.add(1, new ActiveTargetGoal<>(this, PlayerEntity.class, true));

        this.goalSelector.add(0, new MimicAppearGoal(this));
        this.goalSelector.add(1, new MimicAttackGoal(this, 1, false));
        this.goalSelector.add(2, new WanderAroundFarGoal(this, 1));
        this.goalSelector.add(3, new LookAtEntityGoal(this, PlayerEntity.class, 16));
        this.goalSelector.add(4, new LookAroundGoal(this));
    }

    @Override
    protected void initDataTracker (DataTracker.Builder builder) {
        super.initDataTracker(builder);
        builder.add(RUNNING_SPAWN_ANIMATION, false);
        builder.add(RENDERED_ENTITY, new NbtCompound());
    }

    @Override
    public void onSpawnPacket (EntitySpawnS2CPacket packet) {
        super.onSpawnPacket(packet);
        this.spawnState.start(this.age);
        this.setRunningSpawnAnimation(true);
    }

    @Override
    public void readCustomDataFromNbt (NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);

        this.spawnDelay = nbt.getShort("Delay");
        boolean bl = nbt.contains("SpawnData", NbtElement.COMPOUND_TYPE);
        if (bl) {
            MobSpawnerEntry mobSpawnerEntry = MobSpawnerEntry.CODEC
                .parse(NbtOps.INSTANCE, nbt.getCompound("SpawnData"))
                .resultOrPartial(string -> CursedSpawnersMain.LOGGER.warn("Invalid SpawnData: {}", string))
                .orElseGet(MobSpawnerEntry::new);
            this.setSpawnEntry(mobSpawnerEntry);
        }

        boolean bl2 = nbt.contains("SpawnPotentials", NbtElement.LIST_TYPE);
        if (bl2) {
            NbtList nbtList = nbt.getList("SpawnPotentials", NbtElement.COMPOUND_TYPE);
            this.spawnPotentials = MobSpawnerEntry.DATA_POOL_CODEC
                .parse(NbtOps.INSTANCE, nbtList)
                .resultOrPartial(error -> CursedSpawnersMain.LOGGER.warn("Invalid SpawnPotentials list: {}", error))
                .orElseGet(() -> DataPool.<MobSpawnerEntry>empty());
        } else {
            this.spawnPotentials = DataPool.of(this.spawnEntry != null ? this.spawnEntry : new MobSpawnerEntry());
        }

        if (nbt.contains("MinSpawnDelay", NbtElement.NUMBER_TYPE)) {
            this.minSpawnDelay = nbt.getShort("MinSpawnDelay");
            this.maxSpawnDelay = nbt.getShort("MaxSpawnDelay");
            this.spawnCount = nbt.getShort("SpawnCount");
        }

        if (nbt.contains("MaxNearbyEntities", NbtElement.NUMBER_TYPE)) {
            this.maxNearbyEntities = nbt.getShort("MaxNearbyEntities");
            this.requiredPlayerRange = nbt.getShort("RequiredPlayerRange");
        }

        if (nbt.contains("SpawnRange", NbtElement.NUMBER_TYPE)) {
            this.spawnRange = nbt.getShort("SpawnRange");
        }

        this.renderedEntity = null;
    }

    @Override
    public void writeCustomDataToNbt (NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);
        nbt.putShort("Delay", (short)this.spawnDelay);
        nbt.putShort("MinSpawnDelay", (short)this.minSpawnDelay);
        nbt.putShort("MaxSpawnDelay", (short)this.maxSpawnDelay);
        nbt.putShort("SpawnCount", (short)this.spawnCount);
        nbt.putShort("MaxNearbyEntities", (short)this.maxNearbyEntities);
        nbt.putShort("RequiredPlayerRange", (short)this.requiredPlayerRange);
        nbt.putShort("SpawnRange", (short)this.spawnRange);
        if (this.spawnEntry != null) {
            nbt.put(
                "SpawnData",
                MobSpawnerEntry.CODEC.encodeStart(NbtOps.INSTANCE, this.spawnEntry).getOrThrow(string -> new IllegalStateException("Invalid SpawnData: " + string))
            );
        }

        nbt.put("SpawnPotentials", MobSpawnerEntry.DATA_POOL_CODEC.encodeStart(NbtOps.INSTANCE, this.spawnPotentials).getOrThrow());
    }

    @Override
    public void onTrackedDataSet (TrackedData<?> data) {
        super.onTrackedDataSet(data);
    }

    @Override
    public void tick () {
        this.prevMobEntryRotation = this.mobEntryRotation;
        this.mobEntryRotation = (this.mobEntryRotation + (double)(1000f / ((float)this.spawnDelay + 200f))) % 360.0;
        this.setupAnimations();

        if (!this.isAiDisabled() && this.getWorld() instanceof ServerWorld serverWorld) {
            if (this.spawnDelay < 0) this.updateSpawns();
            if (this.spawnDelay > 0) this.spawnDelay--;
            else this.spawn(serverWorld);
        }

        if (this.getWorld().isClient() && this.getRenderedEntity() != null) {
            double x = this.getX() + this.random.nextDouble() - 0.5;
            double y = this.getY() + this.random.nextDouble();
            double z = this.getZ() + this.random.nextDouble() - 0.5;
            this.getWorld().addParticle(ParticleTypes.SMOKE, x, y, z, 0.0, 0.0, 0.0);
            this.getWorld().addParticle(ParticleTypes.FLAME, x, y, z, 0.0, 0.0, 0.0);
        }

        super.tick();
    }

    private void setupAnimations () {
        if (!this.isNavigating()) this.idleState.startIfNotRunning(this.age);
        else this.idleState.stop();

        if (this.isRunningSpawnAnimation()) this.spawnState.startIfNotRunning(this.age);

        if (!this.isAttacking()) this.attackState.stop();
    }

    @Override
    public boolean tryAttack (Entity target) {
        this.getWorld().sendEntityStatus(this, EntityStatuses.PLAY_ATTACK_SOUND);
        return super.tryAttack(target);
    }

    @Override
    public void handleStatus (byte status) {
        super.handleStatus(status);
        if (status == EntityStatuses.PLAY_ATTACK_SOUND) {
            this.attackState.start(this.age);
        }
    }

    @Override
    public boolean isCollidable () {
        return this.isAlive();
    }

    @Override
    public void slowMovement (BlockState state, Vec3d multiplier) {
        if (!state.isOf(Blocks.COBWEB)) {
            super.slowMovement(state, multiplier);
        }
    }

    @Override
    protected void playAttackSound () {
        this.playSound(CSSoundEvents.ENTITY_MIMIC_ATTACK);
    }

    @Override
    protected SoundEvent getAmbientSound () {
        return CSSoundEvents.ENTITY_MIMIC_AMBIENT;
    }

    @Override
    protected SoundEvent getDeathSound () {
        return CSSoundEvents.ENTITY_MIMIC_DEATH;
    }

    @Override
    protected SoundEvent getHurtSound (DamageSource source) {
        return CSSoundEvents.ENTITY_MIMIC_HURT;
    }

    @Override
    protected void playStepSound (BlockPos pos, BlockState state) {
        this.playSound(CSSoundEvents.ENTITY_MIMIC_STEP);
    }

    @Override
    protected ActionResult interactMob (PlayerEntity player, Hand hand) {
        ItemStack held = player.getStackInHand(hand);
        if (held.getItem() instanceof SpawnEggItem spawnEgg) {
            EntityType<?> entityType = spawnEgg.getEntityType(held);
            this.setEntityType(entityType);
            this.getWorld().emitGameEvent(player, GameEvent.ENTITY_INTERACT, this.getBlockPos());
            held.decrement(1);
            this.spawnDelay = 20;
            return ActionResult.success(this.getWorld().isClient);
        }
        else {
            return super.interactMob(player, hand);
        }
    }

    @Override
    protected ActionResult interactWithItem (PlayerEntity player, Hand hand) {
        ItemStack held = player.getStackInHand(hand);
        if (held.getItem() instanceof SpawnEggItem) return ActionResult.PASS;
        return super.interactWithItem(player, hand);
    }

    protected void spawn (ServerWorld serverWorld) {
        boolean spawnedSuccessfully = false;
        MobSpawnerEntry mobSpawnerEntry = this.getSpawnEntry();

        for (int i = 0; i < this.spawnCount; i++) {
            NbtCompound nbtCompound = mobSpawnerEntry.getNbt();
            Optional<EntityType<?>> optionalEntityType = EntityType.fromNbt(nbtCompound);
            if (optionalEntityType.isEmpty()) {
                this.updateSpawns();
                return;
            }

            NbtList nbtList = nbtCompound.getList("Pos", NbtElement.DOUBLE_TYPE);
            int nbtSize = nbtList.size();
            double mobX = nbtSize >= 1 ? nbtList.getDouble(0) : this.getX() + (this.random.nextDouble() - this.random.nextDouble()) * (double)this.spawnRange + 0.5;
            double mobY = nbtSize >= 2 ? nbtList.getDouble(1) : this.getY() + this.random.nextInt(3) - 1;
            double mobZ = nbtSize >= 3 ? nbtList.getDouble(2) : this.getZ() + (this.random.nextDouble() - this.random.nextDouble()) * (double)this.spawnRange + 0.5;
            if (this.getWorld().isSpaceEmpty(optionalEntityType.get().getSpawnBox(mobX, mobY, mobZ))) {
                BlockPos mobBlockPos = BlockPos.ofFloored(mobX, mobY, mobZ);
                if (mobSpawnerEntry.getCustomSpawnRules().isPresent()) {
                    if (!optionalEntityType.get().getSpawnGroup().isPeaceful() && this.getWorld().getDifficulty() == Difficulty.PEACEFUL) {
                        continue;
                    }

                    MobSpawnerEntry.CustomSpawnRules customSpawnRules = mobSpawnerEntry.getCustomSpawnRules().get();
                    if (!customSpawnRules.canSpawn(mobBlockPos, serverWorld)) {
                        continue;
                    }
                }
                else if (!SpawnRestriction.canSpawn(optionalEntityType.get(), serverWorld, SpawnReason.SPAWNER, mobBlockPos, this.getRandom())) {
                    continue;
                }

                Entity entity = EntityType.loadEntityWithPassengers(nbtCompound, serverWorld, entityx -> {
                    entityx.refreshPositionAndAngles(mobX, mobY, mobZ, entityx.getYaw(), entityx.getPitch());
                    return entityx;
                });

                if (entity == null) {
                    this.updateSpawns();
                    return;
                }

                int nearbyEntityCount = this.getWorld().getEntitiesByType(
                    TypeFilter.equals(entity.getClass()),
                        new Box(this.getX(), this.getY(), this.getZ(), this.getX() + 1, this.getY() + 1, this.getZ() + 1).expand(this.spawnRange),
                        EntityPredicates.EXCEPT_SPECTATOR
                    ).size();
                if (nearbyEntityCount >= this.maxNearbyEntities) {
                    this.updateSpawns();
                    return;
                }

                entity.refreshPositionAndAngles(entity.getX(), entity.getY(), entity.getZ(), this.random.nextFloat() * 360.0F, 0.0F);
                if (entity instanceof MobEntity mobEntity) {
                    if (mobSpawnerEntry.getCustomSpawnRules().isEmpty() && !mobEntity.canSpawn(serverWorld, SpawnReason.SPAWNER) || !mobEntity.canSpawn(serverWorld)) {
                        continue;
                    }

                    boolean bl2 = mobSpawnerEntry.getNbt().getSize() == 1 && mobSpawnerEntry.getNbt().contains("id", NbtElement.STRING_TYPE);
                    if (bl2) {
                        ((MobEntity)entity).initialize(serverWorld, serverWorld.getLocalDifficulty(entity.getBlockPos()), SpawnReason.SPAWNER, null);
                    }

                    mobSpawnerEntry.getEquipment().ifPresent(mobEntity::setEquipmentFromTable);
                }

                if (!serverWorld.spawnNewEntityAndPassengers(entity)) {
                    this.updateSpawns();
                    return;
                }

                serverWorld.syncWorldEvent(WorldEvents.SPAWNER_SPAWNS_MOB, this.getBlockPos(), 0);
                serverWorld.emitGameEvent(entity, GameEvent.ENTITY_PLACE, mobBlockPos);
                if (entity instanceof MobEntity mobEntity) {
                    mobEntity.playSpawnEffects();
                }

                spawnedSuccessfully = true;
            }
        }

        if (spawnedSuccessfully) {
            this.updateSpawns();
        }
    }

    private void updateSpawns () {
        if (this.maxSpawnDelay <= this.minSpawnDelay) this.spawnDelay = this.minSpawnDelay;
        else this.spawnDelay = this.minSpawnDelay + this.random.nextInt(this.maxSpawnDelay - this.minSpawnDelay);

        this.spawnPotentials.getOrEmpty(this.random).ifPresent(spawnPotential -> this.setSpawnEntry(spawnPotential.data()));
    }

    public void setEntityType (EntityType<?> type) {
        this.getSpawnEntry().getNbt().putString("id", Registries.ENTITY_TYPE.getId(type).toString());
        this.dataTracker.set(RENDERED_ENTITY, this.getSpawnEntry().getNbt());
        this.renderedEntity = null;
    }

    @Nullable
    public Entity getRenderedEntity () {
        if (this.renderedEntity == null && !this.getRenderedEntityNbt().isEmpty())
            this.renderedEntity = EntityType.loadEntityWithPassengers(this.getRenderedEntityNbt(), this.getWorld(), Function.identity());

        return this.renderedEntity;
    }

    public double getMobRotation () {
        return this.mobEntryRotation;
    }

    public double getPrevMobRotation () {
        return this.prevMobEntryRotation;
    }

    protected void setSpawnEntry (@Nullable MobSpawnerEntry spawnEntry) {
        this.spawnEntry = spawnEntry;
        if (spawnEntry != null) this.dataTracker.set(RENDERED_ENTITY, spawnEntry.getNbt());
    }

    private MobSpawnerEntry getSpawnEntry () {
        if (this.spawnEntry == null) {
            this.setSpawnEntry(this.spawnPotentials.getOrEmpty(this.random).map(Weighted.Present::data).orElseGet(MobSpawnerEntry::new));
        }
        return this.spawnEntry;
    }

    public void setRunningSpawnAnimation (boolean value) {
        this.getDataTracker().set(RUNNING_SPAWN_ANIMATION, value);
    }

    public boolean isRunningSpawnAnimation () {
        return this.getDataTracker().get(RUNNING_SPAWN_ANIMATION);
    }

    public NbtCompound getRenderedEntityNbt () {
        return this.dataTracker.get(RENDERED_ENTITY);
    }

    protected static class MimicAttackGoal extends MeleeAttackGoal {
        public MimicAttackGoal (SpawnerMimicEntity mob, double speed, boolean pauseWhenMobIdle) {
            super(mob, speed, pauseWhenMobIdle);
        }
    }

    protected static class MimicAppearGoal extends Goal {
        private static final int INITIAL_AGE = -100;
        private final SpawnerMimicEntity self;
        private int startingAge = INITIAL_AGE;

        public MimicAppearGoal (SpawnerMimicEntity mob) {
            this.self = mob;
            this.setControls(EnumSet.of(Control.MOVE, Control.LOOK, Control.JUMP, Control.TARGET));
        }

        @Override
        public boolean canStart () {
            return this.startingAge == INITIAL_AGE || this.self.isRunningSpawnAnimation();
        }

        @Override
        public void start () {
            this.startingAge = this.self.age;
        }

        @Override
        public void stop () {
            this.self.setRunningSpawnAnimation(false);
        }

        @Override
        public boolean shouldContinue () {
            return this.self.age <= this.startingAge + 40;
        }
    }
}
