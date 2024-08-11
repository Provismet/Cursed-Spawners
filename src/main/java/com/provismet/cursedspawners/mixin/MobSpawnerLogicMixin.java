package com.provismet.cursedspawners.mixin;

import com.provismet.cursedspawners.imixin.IMixinMobSpawnerLogic;
import com.provismet.cursedspawners.particle.effect.AOEChargingParticleEffect;
import com.provismet.cursedspawners.registries.CSParticleTypes;
import com.provismet.cursedspawners.registries.CSSoundEvents;
import net.minecraft.block.spawner.MobSpawnerLogic;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(MobSpawnerLogic.class)
public abstract class MobSpawnerLogicMixin implements IMixinMobSpawnerLogic {
    @Unique private static final String CAN_KNOCKBACK = "CanKnockback";
    @Unique private static final String MAX_KNOCKBACK_TIMER = "KnockbackInterval";
    @Unique private static final String KNOCKBACK_STRENGTH = "KnockbackStrength";
    @Unique private static final String KNOCKBACK_RADIUS = "KnockbackRadius";

    @Unique private static final String CAN_HEAL = "CanHeal";
    @Unique private static final String MAX_HEAL_TIMER = "HealInterval";
    @Unique private static final String HEAL_AMOUNT = "HealAmount";
    @Unique private static final String HEAL_RADIUS = "HealRadius";

    @Unique private static final String CAN_BOOST = "CanBoost";
    @Unique private static final String MAX_BOOST_TIMER = "BoostInterval";
    @Unique private static final String BOOST_RADIUS = "BoostRadius";

    @Unique private boolean canKnockback = false;
    @Unique private int knockbackTimer = 200;
    @Unique private int maxKnockbackTimer = 200;
    @Unique private double knockbackStrength = 0.2;
    @Unique private double knockbackRadius = 4;

    @Unique private boolean canHeal = false;
    @Unique private int healTimer = 200;
    @Unique private int maxHealTimer = 200;
    @Unique private float healAmount = 0f;
    @Unique private double healRadius = 0;

    @Unique private boolean canBoost = false;
    @Unique private int boostTimer = 200;
    @Unique private int maxBoostTimer = 200;
    @Unique private double boostRadius = 0f;

    @Inject(method="readNbt", at=@At("TAIL"))
    private void readExtendedNbt (World world, BlockPos pos, NbtCompound nbt, CallbackInfo info) {
        if (nbt.contains(CAN_KNOCKBACK)) this.canKnockback = nbt.getBoolean(CAN_KNOCKBACK);
        else this.canKnockback = false;

        if (nbt.contains(MAX_KNOCKBACK_TIMER, NbtElement.INT_TYPE)) this.maxKnockbackTimer = nbt.getInt(MAX_KNOCKBACK_TIMER);
        else this.maxKnockbackTimer = 200;

        if (nbt.contains(KNOCKBACK_STRENGTH, NbtElement.DOUBLE_TYPE)) this.knockbackStrength = nbt.getDouble(KNOCKBACK_STRENGTH);
        else this.knockbackStrength = 0.2;

        if (nbt.contains(KNOCKBACK_RADIUS, NbtElement.DOUBLE_TYPE)) this.knockbackRadius = nbt.getDouble(KNOCKBACK_RADIUS);
        else this.knockbackRadius = 4;


        if (nbt.contains(CAN_HEAL)) this.canHeal = nbt.getBoolean(CAN_HEAL);
        else this.canHeal = false;

        if (nbt.contains(MAX_HEAL_TIMER, NbtElement.INT_TYPE)) this.maxHealTimer = nbt.getInt(MAX_HEAL_TIMER);
        else this.maxHealTimer = 200;

        if (nbt.contains(HEAL_AMOUNT, NbtElement.FLOAT_TYPE)) this.healAmount = nbt.getFloat(HEAL_AMOUNT);
        else this.healAmount = 0f;

        if (nbt.contains(HEAL_RADIUS, NbtElement.DOUBLE_TYPE)) this.healRadius = nbt.getDouble(HEAL_RADIUS);
        else this.healRadius = 0;


        if (nbt.contains(CAN_BOOST)) this.canBoost = nbt.getBoolean(CAN_BOOST);
        else this.canBoost = false;

        if (nbt.contains(MAX_BOOST_TIMER, NbtElement.INT_TYPE)) this.maxBoostTimer = nbt.getInt(MAX_BOOST_TIMER);
        else this.maxKnockbackTimer = 200;

        if (nbt.contains(BOOST_RADIUS, NbtElement.DOUBLE_TYPE)) this.boostRadius = nbt.getDouble(BOOST_RADIUS);
        else this.boostRadius = 0;

        this.knockbackTimer = Math.min(this.knockbackTimer, this.maxKnockbackTimer);
        this.healTimer = Math.min(this.healTimer, this.maxHealTimer);
        this.boostTimer = Math.min(this.boostTimer, this.maxBoostTimer);
    }

    @Inject(method="writeNbt", at=@At("TAIL"))
    private void writeExtendedNbt (NbtCompound nbt, CallbackInfoReturnable<NbtCompound> cir) {
        nbt.putBoolean(CAN_KNOCKBACK, this.canKnockback);
        nbt.putInt(MAX_KNOCKBACK_TIMER, this.maxKnockbackTimer);
        nbt.putDouble(KNOCKBACK_STRENGTH, this.knockbackStrength);
        nbt.putDouble(KNOCKBACK_RADIUS, this.knockbackRadius);

        nbt.putBoolean(CAN_HEAL, this.canHeal);
        nbt.putInt(MAX_HEAL_TIMER, this.maxHealTimer);
        nbt.putFloat(HEAL_AMOUNT, this.healAmount);
        nbt.putDouble(HEAL_RADIUS, this.healRadius);

        nbt.putBoolean(CAN_BOOST, this.canBoost);
        nbt.putInt(MAX_BOOST_TIMER, this.maxBoostTimer);
        nbt.putDouble(BOOST_RADIUS, this.boostRadius);
    }

    @Inject(method="serverTick", at=@At(
        value="INVOKE",
        target="Lnet/minecraft/block/spawner/MobSpawnerLogic;isPlayerInRange(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;)Z",
        shift=At.Shift.AFTER)
    )
    private void performActions (ServerWorld world, BlockPos pos, CallbackInfo ci) {
        Vec3d centrePos = pos.toCenterPos();

        if (this.canKnockback) {
            --this.knockbackTimer;

            if (this.knockbackTimer == this.maxKnockbackTimer / 2) {
                world.spawnParticles(new AOEChargingParticleEffect(this.knockbackTimer, Vec3d.unpackRgb(0xE2E2E2).toVector3f()), centrePos.getX(), pos.getY() + 0.025, centrePos.getZ(), 1, 0, 0, 0, 0);
            }
            else if (this.knockbackTimer <= 0) {
                this.knockbackTimer = this.maxKnockbackTimer;
                world.spawnParticles(ParticleTypes.GUST_EMITTER_SMALL, centrePos.getX(), centrePos.getY(), centrePos.getZ(), 1, 0, 0, 0, 0);
                List<ServerPlayerEntity> players = world.getPlayers(player -> player.getPos().isWithinRangeOf(centrePos, this.knockbackRadius, this.knockbackRadius) && !player.isCreative() && !player.isSpectator());
                for (ServerPlayerEntity player : players) {
                    double strength = this.knockbackStrength * (1 - player.getAttributeValue(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE));
                    if (strength > 0) {
                        Vec3d velocity = new Vec3d(player.getX() - centrePos.getX(), player.getY() >= pos.getY() ? 0.5 : -0.5, player.getZ() - centrePos.getZ()).normalize().multiply(strength);
                        player.addVelocity(velocity);
                        player.velocityModified = true;
                    }
                }
                world.playSound(null, centrePos.getX(), centrePos.getY(), centrePos.getZ(), SoundEvents.ENTITY_WIND_CHARGE_WIND_BURST, SoundCategory.BLOCKS, 1, 1);
            }
        }

        if (this.canHeal) {
            --this.healTimer;

            if (this.healTimer == this.maxHealTimer / 2) {
                world.spawnParticles(new AOEChargingParticleEffect(this.healTimer, Vec3d.unpackRgb(0x47BC78).toVector3f()), centrePos.getX(), pos.getY() + 0.025, centrePos.getZ(), 1, 0, 0, 0, 0);
            }
            else if (this.healTimer <= 0) {
                this.healTimer = this.maxHealTimer;
                world.spawnParticles(CSParticleTypes.HEAL, centrePos.getX(), centrePos.getY() + 0.5, centrePos.getZ(), 1, 0, 0, 0, 0);
                List<HostileEntity> mobs = world.getEntitiesByClass(HostileEntity.class, Box.of(centrePos, this.healRadius, 3, this.healRadius), mob -> true);
                for (HostileEntity hostile : mobs) {
                    hostile.heal(this.healAmount);
                    world.spawnParticles(ParticleTypes.HAPPY_VILLAGER, hostile.getX(), hostile.getEyeY(), hostile.getZ(), 8, 0.35, 0.35, 0.35, 0);
                }
                world.playSound(null, pos, CSSoundEvents.BLOCK_SPAWNER_HEAL, SoundCategory.BLOCKS, 1, 1);
            }
        }

        if (this.canBoost) {
            --this.boostTimer;

            if (this.boostTimer == this.maxBoostTimer / 2) {
                world.spawnParticles(new AOEChargingParticleEffect(this.boostTimer, Vec3d.unpackRgb(0xFF8459).toVector3f()), centrePos.getX(), pos.getY() + 0.025, centrePos.getZ(), 1, 0, 0, 0, 0);
            }
            else if (this.boostTimer <= 0) {
                this.boostTimer = this.maxBoostTimer;
                world.spawnParticles(CSParticleTypes.BOOST, centrePos.getX(), centrePos.getY() + 0.5, centrePos.getZ(), 1, 0, 0, 0, 0);
                List<HostileEntity> mobs = world.getEntitiesByClass(HostileEntity.class, Box.of(centrePos, this.boostRadius, 3, this.boostRadius), mob -> true);
                for (HostileEntity hostile : mobs) {
                    hostile.addStatusEffect(new StatusEffectInstance(StatusEffects.SPEED, 30, 1));
                    world.spawnParticles(ParticleTypes.HAPPY_VILLAGER, hostile.getX(), hostile.getEyeY(), hostile.getZ(), 8, 0.35, 0.35, 0.35, 0);
                }
                world.playSound(null, pos, CSSoundEvents.BLOCK_SPAWNER_BOOST, SoundCategory.BLOCKS, 1, 1);
            }
        }
    }

    @Override
    public boolean cursed_spawners$getCanKnockback () {
        return this.canKnockback;
    }

    @Override
    public void cursed_spawners$setCanKnockback (boolean value) {
        this.canKnockback = value;
    }

    @Override
    public void cursed_spawners$setKnockbackParams (int interval, double strength, double radius) {
        this.maxKnockbackTimer = interval;
        this.knockbackTimer = Math.min(this.knockbackTimer, this.maxKnockbackTimer);
        this.knockbackStrength = strength;
        this.knockbackRadius = radius;
    }

    @Override
    public boolean cursed_spawners$getCanHeal () {
        return this.canHeal;
    }

    @Override
    public void cursed_spawners$setCanHeal (boolean value) {
        this.canHeal = value;
    }

    @Override
    public void cursed_spawners$setHealParams (int interval, float amount, double radius) {
        this.maxHealTimer = interval;
        this.healTimer = Math.min(this.healTimer, this.maxHealTimer);
        this.healAmount = amount;
        this.healRadius = radius;
    }

    @Override
    public boolean cursed_spawners$getCanBoost () {
        return this.canBoost;
    }

    @Override
    public void cursed_spawners$setCanBoost (boolean value) {
        this.canBoost = value;
    }

    @Override
    public void cursed_spawners$setBoostParams (int interval, double radius) {
        this.maxBoostTimer = interval;
        this.boostTimer = Math.min(this.boostTimer, this.maxBoostTimer);
        this.boostRadius = radius;
    }
}
