package com.provismet.cursedspawners.entity;

import com.provismet.cursedspawners.CursedSpawnersMain;
import com.provismet.cursedspawners.registries.CSEntityTypes;
import com.provismet.cursedspawners.registries.CSSoundEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.ProblemReporter;
import net.minecraft.util.random.WeightedList;
import net.minecraft.world.Difficulty;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AnimationState;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityEvent;
import net.minecraft.world.entity.EntityProcessor;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.SpawnData;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LevelEvent;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.entity.EntityTypeTest;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.storage.TagValueInput;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.EnumSet;
import java.util.Optional;

public class SpawnerMimicEntity extends Monster {
    private int spawnDelay = 20;
    private WeightedList<SpawnData> spawnPotentials = WeightedList.of();
    private SpawnData spawnEntry;
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

    private static final EntityDataAccessor<Boolean> RUNNING_SPAWN_ANIMATION = SynchedEntityData.defineId(SpawnerMimicEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<String> RENDERED_ENTITY_TYPE = SynchedEntityData.defineId(SpawnerMimicEntity.class, EntityDataSerializers.STRING);

    public SpawnerMimicEntity (Level world) {
        this(CSEntityTypes.SPAWNER_MIMIC, world);
    }

    public SpawnerMimicEntity (EntityType<? extends SpawnerMimicEntity> entityType, Level world) {
        super(entityType, world);
        this.xpReward = 30;
    }

    public static AttributeSupplier.Builder getSpawnerMimicAttributes () {
        return Monster.createMonsterAttributes()
            .add(Attributes.MAX_HEALTH, 10)
            .add(Attributes.KNOCKBACK_RESISTANCE, 0.8)
            .add(Attributes.ARMOR, 25)
            .add(Attributes.ARMOR_TOUGHNESS, 5)
            .add(Attributes.MOVEMENT_SPEED, 0.275)
            .add(Attributes.ATTACK_DAMAGE, 2)
            .add(Attributes.ATTACK_KNOCKBACK, 1);
    }

    @Override
    protected void registerGoals () {
        super.registerGoals();
        this.targetSelector.addGoal(0, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, Player.class, true));

        this.goalSelector.addGoal(0, new MimicAppearGoal(this));
        this.goalSelector.addGoal(1, new MimicAttackGoal(this, 1, false));
        this.goalSelector.addGoal(2, new WaterAvoidingRandomStrollGoal(this, 1));
        this.goalSelector.addGoal(3, new LookAtPlayerGoal(this, Player.class, 16));
        this.goalSelector.addGoal(4, new RandomLookAroundGoal(this));
    }

    @Override
    protected void defineSynchedData (SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(RUNNING_SPAWN_ANIMATION, false);
        builder.define(RENDERED_ENTITY_TYPE, "");
    }

    @Override
    public void recreateFromPacket (ClientboundAddEntityPacket packet) {
        super.recreateFromPacket(packet);
        this.spawnState.start(this.tickCount);
        this.setRunningSpawnAnimation(true);
    }

    @Override
    protected void readAdditionalSaveData (ValueInput view) {
        super.readAdditionalSaveData(view);

        this.spawnDelay = view.getShortOr("Delay", (short)20);
        view.read("SpawnData", SpawnData.CODEC).ifPresent(this::setSpawnEntry);
        view.read("SpawnPotentials", SpawnData.LIST_CODEC).ifPresentOrElse(
            potentials -> this.spawnPotentials = potentials,
            () -> this.spawnPotentials = WeightedList.of(this.spawnEntry != null ? this.spawnEntry : new SpawnData())
        );
        this.minSpawnDelay = view.getShortOr("MinSpawnDelay", (short)this.minSpawnDelay);
        this.maxSpawnDelay = view.getShortOr("MaxSpawnDelay", (short)this.maxSpawnDelay);
        this.spawnCount = view.getShortOr("SpawnCount", (short)this.spawnCount);
        this.maxNearbyEntities = view.getShortOr("MaxNearbyEntities", (short)this.maxNearbyEntities);
        this.requiredPlayerRange = view.getShortOr("RequiredPlayerRange", (short)this.requiredPlayerRange);
        this.spawnRange = view.getShortOr("SpawnRange", (short)this.spawnRange);

        this.renderedEntity = null;
    }

    @Override
    protected void addAdditionalSaveData (ValueOutput view) {
        super.addAdditionalSaveData(view);
        view.putShort("Delay", (short)this.spawnDelay);
        view.putShort("MinSpawnDelay", (short)this.minSpawnDelay);
        view.putShort("MaxSpawnDelay", (short)this.maxSpawnDelay);
        view.putShort("SpawnCount", (short)this.spawnCount);
        view.putShort("MaxNearbyEntities", (short)this.maxNearbyEntities);
        view.putShort("RequiredPlayerRange", (short)this.requiredPlayerRange);
        view.putShort("SpawnRange", (short)this.spawnRange);
        if (this.spawnEntry != null) {
            view.store("SpawnData", SpawnData.CODEC, this.spawnEntry);
        }

        view.store("SpawnPotentials", SpawnData.LIST_CODEC, this.spawnPotentials);
    }

    @Override
    public void onSyncedDataUpdated (EntityDataAccessor<?> data) {
        super.onSyncedDataUpdated(data);
    }

    @Override
    public void tick () {
        this.prevMobEntryRotation = this.mobEntryRotation;
        this.mobEntryRotation = (this.mobEntryRotation + (double)(1000f / ((float)this.spawnDelay + 200f))) % 360.0;
        this.setupAnimations();

        if (!this.isNoAi() && this.level() instanceof ServerLevel serverWorld) {
            if (this.spawnDelay < 0) this.updateSpawns();
            if (this.spawnDelay > 0) this.spawnDelay--;
            else this.spawn(serverWorld);
        }

        if (this.level().isClientSide() && this.getRenderedEntity() != null) {
            double x = this.getX() + this.random.nextDouble() - 0.5;
            double y = this.getY() + this.random.nextDouble();
            double z = this.getZ() + this.random.nextDouble() - 0.5;
            this.level().addParticle(ParticleTypes.SMOKE, x, y, z, 0.0, 0.0, 0.0);
            this.level().addParticle(ParticleTypes.FLAME, x, y, z, 0.0, 0.0, 0.0);
        }

        super.tick();
    }

    private void setupAnimations () {
        if (!this.isPathFinding()) this.idleState.startIfStopped(this.tickCount);
        else this.idleState.stop();

        if (this.isRunningSpawnAnimation()) this.spawnState.startIfStopped(this.tickCount);

        if (!this.isAggressive()) this.attackState.stop();
    }

    @Override
    public boolean doHurtTarget (ServerLevel world, Entity target) {
        this.level().broadcastEntityEvent(this, EntityEvent.START_ATTACKING);
        return super.doHurtTarget(world, target);
    }

    @Override
    public void handleEntityEvent (byte status) {
        super.handleEntityEvent(status);
        if (status == EntityEvent.START_ATTACKING) {
            this.attackState.start(this.tickCount);
        }
    }

    @Override
    public boolean canBeCollidedWith (@Nullable Entity entity) {
        return this.isAlive();
    }

    @Override
    public void makeStuckInBlock (BlockState state, Vec3 multiplier) {
        if (!state.is(Blocks.COBWEB)) {
            super.makeStuckInBlock(state, multiplier);
        }
    }

    @Override
    protected void playAttackSound () {
        this.makeSound(CSSoundEvents.ENTITY_MIMIC_ATTACK);
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
        this.makeSound(CSSoundEvents.ENTITY_MIMIC_STEP);
    }

    @Override
    protected InteractionResult mobInteract (Player player, InteractionHand hand) {
        ItemStack held = player.getItemInHand(hand);
        if (held.getItem() instanceof SpawnEggItem spawnEgg) {
            EntityType<?> entityType = spawnEgg.getType(held);
            this.setEntityType(entityType);
            this.level().gameEvent(player, GameEvent.ENTITY_INTERACT, this.blockPosition());
            held.shrink(1);
            this.spawnDelay = 20;
            return InteractionResult.SUCCESS;
        }
        else {
            return super.mobInteract(player, hand);
        }
    }

    @Override
    protected InteractionResult checkAndHandleImportantInteractions (Player player, InteractionHand hand) {
        ItemStack held = player.getItemInHand(hand);
        if (held.getItem() instanceof SpawnEggItem) return InteractionResult.PASS;
        return super.checkAndHandleImportantInteractions(player, hand);
    }

    protected void spawn (ServerLevel serverWorld) {
        boolean spawnedSuccessfully = false;
        SpawnData mobSpawnerEntry = this.getSpawnEntry();

        for (int i = 0; i < this.spawnCount; i++) {
            try (ProblemReporter.ScopedCollector logging = new ProblemReporter.ScopedCollector(this::toString, CursedSpawnersMain.LOGGER)) {
                ValueInput readView = TagValueInput.create(logging, serverWorld.registryAccess(), mobSpawnerEntry.getEntityToSpawn());
                Optional<EntityType<?>> optionalEntityType = EntityType.by(readView);
                if (optionalEntityType.isEmpty()) {
                    this.updateSpawns();
                    return;
                }

                Vec3 mobPos = readView.read("Pos", Vec3.CODEC).orElseGet(
                    () -> new Vec3(
                        this.getX() + (random.nextDouble() - random.nextDouble()) * this.spawnRange + 0.5,
                        this.getY() + random.nextInt(3) - 1,
                        this.getZ() + (random.nextDouble() - random.nextDouble()) * this.spawnRange + 0.5
                    )
                );

                if (this.level().noCollision(optionalEntityType.get().getSpawnAABB(mobPos.x(), mobPos.y(), mobPos.z()))) {
                    BlockPos mobBlockPos = BlockPos.containing(mobPos);
                    if (mobSpawnerEntry.getCustomSpawnRules().isPresent()) {
                        if (!optionalEntityType.get().getCategory().isFriendly() && this.level().getDifficulty() == Difficulty.PEACEFUL) {
                            continue;
                        }

                        SpawnData.CustomSpawnRules customSpawnRules = mobSpawnerEntry.getCustomSpawnRules().get();
                        if (!customSpawnRules.isValidPosition(mobBlockPos, serverWorld)) {
                            continue;
                        }
                    } else if (!SpawnPlacements.checkSpawnRules(optionalEntityType.get(), serverWorld, EntitySpawnReason.SPAWNER, mobBlockPos, this.getRandom())) {
                        continue;
                    }

                    Entity entity = EntityType.loadEntityRecursive(readView, serverWorld, EntitySpawnReason.SPAWNER, entityx -> {
                        entityx.snapTo(mobPos.x(), mobPos.y(), mobPos.z(), entityx.getYRot(), entityx.getXRot());
                        return entityx;
                    });

                    if (entity == null) {
                        this.updateSpawns();
                        return;
                    }

                    int nearbyEntityCount = this.level().getEntities(
                        EntityTypeTest.forExactClass(entity.getClass()),
                        new AABB(this.getX(), this.getY(), this.getZ(), this.getX() + 1, this.getY() + 1, this.getZ() + 1).inflate(this.spawnRange),
                        EntitySelector.NO_SPECTATORS
                    ).size();
                    if (nearbyEntityCount >= this.maxNearbyEntities) {
                        this.updateSpawns();
                        return;
                    }

                    entity.snapTo(entity.getX(), entity.getY(), entity.getZ(), this.random.nextFloat() * 360.0F, 0.0F);
                    if (entity instanceof Mob mobEntity) {
                        if (mobSpawnerEntry.getCustomSpawnRules().isEmpty() && !mobEntity.checkSpawnRules(serverWorld, EntitySpawnReason.SPAWNER) || !mobEntity.checkSpawnObstruction(serverWorld)) {
                            continue;
                        }

                        if (mobSpawnerEntry.getEntityToSpawn().size() == 1 && mobSpawnerEntry.getEntityToSpawn().contains("id")) {
                            mobEntity.finalizeSpawn(serverWorld, serverWorld.getCurrentDifficultyAt(entity.blockPosition()), EntitySpawnReason.SPAWNER, null);
                        }

                        mobSpawnerEntry.getEquipment().ifPresent(mobEntity::equip);
                    }

                    if (!serverWorld.tryAddFreshEntityWithPassengers(entity)) {
                        this.updateSpawns();
                        return;
                    }

                    serverWorld.levelEvent(LevelEvent.PARTICLES_MOBBLOCK_SPAWN, this.blockPosition(), 0);
                    serverWorld.gameEvent(entity, GameEvent.ENTITY_PLACE, mobBlockPos);
                    if (entity instanceof Mob mobEntity) {
                        mobEntity.spawnAnim();
                    }

                    spawnedSuccessfully = true;
                }
            }
        }

        if (spawnedSuccessfully) {
            this.updateSpawns();
        }
    }

    private void updateSpawns () {
        if (this.maxSpawnDelay <= this.minSpawnDelay) this.spawnDelay = this.minSpawnDelay;
        else this.spawnDelay = this.minSpawnDelay + this.random.nextInt(this.maxSpawnDelay - this.minSpawnDelay);

        this.spawnPotentials.getRandom(this.random).ifPresent(this::setSpawnEntry);
    }

    public void setEntityType (EntityType<?> type) {
        String idString = BuiltInRegistries.ENTITY_TYPE.getKey(type).toString();
        this.getSpawnEntry().getEntityToSpawn().putString("id", idString);
        this.entityData.set(RENDERED_ENTITY_TYPE, idString);
        this.renderedEntity = null;
    }

    @Nullable
    public Entity getRenderedEntity () {
        if (this.renderedEntity == null && this.getRenderedEntityType() != null)
            this.renderedEntity = EntityType.loadEntityRecursive(this.getRenderedEntityType(), new CompoundTag(), this.level(), EntitySpawnReason.SPAWNER, EntityProcessor.NOP);

        return this.renderedEntity;
    }

    public double getMobRotation () {
        return this.mobEntryRotation;
    }

    public double getPrevMobRotation () {
        return this.prevMobEntryRotation;
    }

    protected void setSpawnEntry (@Nullable SpawnData spawnEntry) {
        this.spawnEntry = spawnEntry;
        if (spawnEntry != null) this.entityData.set(RENDERED_ENTITY_TYPE, spawnEntry.entityToSpawn().getStringOr("id", ""));
    }

    private SpawnData getSpawnEntry () {
        if (this.spawnEntry == null) {
            this.setSpawnEntry(this.spawnPotentials.getRandom(this.random).orElseGet(SpawnData::new));
        }
        return this.spawnEntry;
    }

    public void setRunningSpawnAnimation (boolean value) {
        this.getEntityData().set(RUNNING_SPAWN_ANIMATION, value);
    }

    public boolean isRunningSpawnAnimation () {
        return this.getEntityData().get(RUNNING_SPAWN_ANIMATION);
    }

    @Nullable
    public EntityType<?> getRenderedEntityType () {
        Identifier id = Identifier.tryParse(this.entityData.get(RENDERED_ENTITY_TYPE));
        if (id == null) return null;

        return BuiltInRegistries.ENTITY_TYPE.getOptional(id).orElse(null);
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
            this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK, Flag.JUMP, Flag.TARGET));
        }

        @Override
        public boolean canUse () {
            return this.startingAge == INITIAL_AGE || this.self.isRunningSpawnAnimation();
        }

        @Override
        public void start () {
            this.startingAge = this.self.tickCount;
        }

        @Override
        public void stop () {
            this.self.setRunningSpawnAnimation(false);
        }

        @Override
        public boolean canContinueToUse () {
            return this.self.tickCount <= this.startingAge + 40;
        }
    }
}
