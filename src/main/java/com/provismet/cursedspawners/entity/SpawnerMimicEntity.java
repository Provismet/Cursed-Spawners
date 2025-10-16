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
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.registry.Registries;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.storage.NbtReadView;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ErrorReporter;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.TypeFilter;
import net.minecraft.util.collection.Pool;
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
    private Pool<MobSpawnerEntry> spawnPotentials = Pool.empty();
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
    private static final TrackedData<String> RENDERED_ENTITY_TYPE = DataTracker.registerData(SpawnerMimicEntity.class, TrackedDataHandlerRegistry.STRING);

    public SpawnerMimicEntity (World world) {
        this(CSEntityTypes.SPAWNER_MIMIC, world);
    }

    public SpawnerMimicEntity (EntityType<? extends SpawnerMimicEntity> entityType, World world) {
        super(entityType, world);
        this.experiencePoints = 30;
    }

    public static DefaultAttributeContainer.Builder getSpawnerMimicAttributes () {
        return HostileEntity.createHostileAttributes()
            .add(EntityAttributes.MAX_HEALTH, 10)
            .add(EntityAttributes.KNOCKBACK_RESISTANCE, 0.8)
            .add(EntityAttributes.ARMOR, 25)
            .add(EntityAttributes.ARMOR_TOUGHNESS, 5)
            .add(EntityAttributes.MOVEMENT_SPEED, 0.275)
            .add(EntityAttributes.ATTACK_DAMAGE, 2)
            .add(EntityAttributes.ATTACK_KNOCKBACK, 1);
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
        builder.add(RENDERED_ENTITY_TYPE, "");
    }

    @Override
    public void onSpawnPacket (EntitySpawnS2CPacket packet) {
        super.onSpawnPacket(packet);
        this.spawnState.start(this.age);
        this.setRunningSpawnAnimation(true);
    }

    @Override
    protected void readCustomData (ReadView view) {
        super.readCustomData(view);

        this.spawnDelay = view.getShort("Delay", (short)20);
        view.read("SpawnData", MobSpawnerEntry.CODEC).ifPresent(this::setSpawnEntry);
        view.read("SpawnPotentials", MobSpawnerEntry.DATA_POOL_CODEC).ifPresentOrElse(
            potentials -> this.spawnPotentials = potentials,
            () -> this.spawnPotentials = Pool.of(this.spawnEntry != null ? this.spawnEntry : new MobSpawnerEntry())
        );
        this.minSpawnDelay = view.getShort("MinSpawnDelay", (short)this.minSpawnDelay);
        this.maxSpawnDelay = view.getShort("MaxSpawnDelay", (short)this.maxSpawnDelay);
        this.spawnCount = view.getShort("SpawnCount", (short)this.spawnCount);
        this.maxNearbyEntities = view.getShort("MaxNearbyEntities", (short)this.maxNearbyEntities);
        this.requiredPlayerRange = view.getShort("RequiredPlayerRange", (short)this.requiredPlayerRange);
        this.spawnRange = view.getShort("SpawnRange", (short)this.spawnRange);

        this.renderedEntity = null;
    }

    @Override
    protected void writeCustomData (WriteView view) {
        super.writeCustomData(view);
        view.putShort("Delay", (short)this.spawnDelay);
        view.putShort("MinSpawnDelay", (short)this.minSpawnDelay);
        view.putShort("MaxSpawnDelay", (short)this.maxSpawnDelay);
        view.putShort("SpawnCount", (short)this.spawnCount);
        view.putShort("MaxNearbyEntities", (short)this.maxNearbyEntities);
        view.putShort("RequiredPlayerRange", (short)this.requiredPlayerRange);
        view.putShort("SpawnRange", (short)this.spawnRange);
        if (this.spawnEntry != null) {
            view.put("SpawnData", MobSpawnerEntry.CODEC, this.spawnEntry);
        }

        view.put("SpawnPotentials", MobSpawnerEntry.DATA_POOL_CODEC, this.spawnPotentials);
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

        if (!this.isAiDisabled() && this.getEntityWorld() instanceof ServerWorld serverWorld) {
            if (this.spawnDelay < 0) this.updateSpawns();
            if (this.spawnDelay > 0) this.spawnDelay--;
            else this.spawn(serverWorld);
        }

        if (this.getEntityWorld().isClient() && this.getRenderedEntity() != null) {
            double x = this.getX() + this.random.nextDouble() - 0.5;
            double y = this.getY() + this.random.nextDouble();
            double z = this.getZ() + this.random.nextDouble() - 0.5;
            this.getEntityWorld().addParticleClient(ParticleTypes.SMOKE, x, y, z, 0.0, 0.0, 0.0);
            this.getEntityWorld().addParticleClient(ParticleTypes.FLAME, x, y, z, 0.0, 0.0, 0.0);
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
    public boolean tryAttack (ServerWorld world, Entity target) {
        this.getEntityWorld().sendEntityStatus(this, EntityStatuses.PLAY_ATTACK_SOUND);
        return super.tryAttack(world, target);
    }

    @Override
    public void handleStatus (byte status) {
        super.handleStatus(status);
        if (status == EntityStatuses.PLAY_ATTACK_SOUND) {
            this.attackState.start(this.age);
        }
    }

    @Override
    public boolean isCollidable (@Nullable Entity entity) {
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
            this.getEntityWorld().emitGameEvent(player, GameEvent.ENTITY_INTERACT, this.getBlockPos());
            held.decrement(1);
            this.spawnDelay = 20;
            return ActionResult.SUCCESS;
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
            try (ErrorReporter.Logging logging = new ErrorReporter.Logging(this::toString, CursedSpawnersMain.LOGGER)) {
                ReadView readView = NbtReadView.create(logging, serverWorld.getRegistryManager(), mobSpawnerEntry.getNbt());
                Optional<EntityType<?>> optionalEntityType = EntityType.fromData(readView);
                if (optionalEntityType.isEmpty()) {
                    this.updateSpawns();
                    return;
                }

                Vec3d mobPos = readView.read("Pos", Vec3d.CODEC).orElseGet(
                    () -> new Vec3d(
                        this.getX() + (random.nextDouble() - random.nextDouble()) * this.spawnRange + 0.5,
                        this.getY() + random.nextInt(3) - 1,
                        this.getZ() + (random.nextDouble() - random.nextDouble()) * this.spawnRange + 0.5
                    )
                );

                if (this.getEntityWorld().isSpaceEmpty(optionalEntityType.get().getSpawnBox(mobPos.getX(), mobPos.getY(), mobPos.getZ()))) {
                    BlockPos mobBlockPos = BlockPos.ofFloored(mobPos);
                    if (mobSpawnerEntry.getCustomSpawnRules().isPresent()) {
                        if (!optionalEntityType.get().getSpawnGroup().isPeaceful() && this.getEntityWorld().getDifficulty() == Difficulty.PEACEFUL) {
                            continue;
                        }

                        MobSpawnerEntry.CustomSpawnRules customSpawnRules = mobSpawnerEntry.getCustomSpawnRules().get();
                        if (!customSpawnRules.canSpawn(mobBlockPos, serverWorld)) {
                            continue;
                        }
                    } else if (!SpawnRestriction.canSpawn(optionalEntityType.get(), serverWorld, SpawnReason.SPAWNER, mobBlockPos, this.getRandom())) {
                        continue;
                    }

                    Entity entity = EntityType.loadEntityWithPassengers(readView, serverWorld, SpawnReason.SPAWNER, entityx -> {
                        entityx.refreshPositionAndAngles(mobPos.getX(), mobPos.getY(), mobPos.getZ(), entityx.getYaw(), entityx.getPitch());
                        return entityx;
                    });

                    if (entity == null) {
                        this.updateSpawns();
                        return;
                    }

                    int nearbyEntityCount = this.getEntityWorld().getEntitiesByType(
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

                        if (mobSpawnerEntry.getNbt().getSize() == 1 && mobSpawnerEntry.getNbt().contains("id")) {
                            mobEntity.initialize(serverWorld, serverWorld.getLocalDifficulty(entity.getBlockPos()), SpawnReason.SPAWNER, null);
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
        }

        if (spawnedSuccessfully) {
            this.updateSpawns();
        }
    }

    private void updateSpawns () {
        if (this.maxSpawnDelay <= this.minSpawnDelay) this.spawnDelay = this.minSpawnDelay;
        else this.spawnDelay = this.minSpawnDelay + this.random.nextInt(this.maxSpawnDelay - this.minSpawnDelay);

        this.spawnPotentials.getOrEmpty(this.random).ifPresent(this::setSpawnEntry);
    }

    public void setEntityType (EntityType<?> type) {
        String idString = Registries.ENTITY_TYPE.getId(type).toString();
        this.getSpawnEntry().getNbt().putString("id", idString);
        this.dataTracker.set(RENDERED_ENTITY_TYPE, idString);
        this.renderedEntity = null;
    }

    @Nullable
    public Entity getRenderedEntity () {
        if (this.renderedEntity == null && this.getRenderedEntityType() != null)
            this.renderedEntity = EntityType.loadEntityWithPassengers(this.getRenderedEntityType(), new NbtCompound(), this.getEntityWorld(), SpawnReason.SPAWNER, Function.identity());

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
        if (spawnEntry != null) this.dataTracker.set(RENDERED_ENTITY_TYPE, spawnEntry.entity().getString("id", ""));
    }

    private MobSpawnerEntry getSpawnEntry () {
        if (this.spawnEntry == null) {
            this.setSpawnEntry(this.spawnPotentials.getOrEmpty(this.random).orElseGet(MobSpawnerEntry::new));
        }
        return this.spawnEntry;
    }

    public void setRunningSpawnAnimation (boolean value) {
        this.getDataTracker().set(RUNNING_SPAWN_ANIMATION, value);
    }

    public boolean isRunningSpawnAnimation () {
        return this.getDataTracker().get(RUNNING_SPAWN_ANIMATION);
    }

    @Nullable
    public EntityType<?> getRenderedEntityType () {
        Identifier id = Identifier.tryParse(this.dataTracker.get(RENDERED_ENTITY_TYPE));
        if (id == null) return null;

        return Registries.ENTITY_TYPE.getOptionalValue(id).orElse(null);
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
