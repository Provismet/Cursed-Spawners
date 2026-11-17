package com.provismet.cursedspawners.mixin;

import com.provismet.cursedspawners.imixin.IMixinMobSpawnerLogic;
import com.provismet.cursedspawners.particle.effect.AOEChargingParticleEffect;
import com.provismet.cursedspawners.registries.CSParticleTypes;
import com.provismet.cursedspawners.registries.CSSoundEvents;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.ARGB;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.BaseSpawner;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

@Mixin(BaseSpawner.class)
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

    @Inject(method="load", at=@At("TAIL"))
    private void readExtendedNbt (Level world, BlockPos pos, ValueInput view, CallbackInfo ci) {
        this.canKnockback = view.getBooleanOr(CAN_KNOCKBACK, false);
        this.maxKnockbackTimer = view.getIntOr(MAX_KNOCKBACK_TIMER, 200);
        this.knockbackStrength = view.getDoubleOr(KNOCKBACK_STRENGTH, 0.2);
        this.knockbackRadius = view.getDoubleOr(KNOCKBACK_RADIUS, 4);
        this.canHeal = view.getBooleanOr(CAN_HEAL, false);
        this.maxHealTimer = view.getIntOr(MAX_HEAL_TIMER, 200);
        this.healAmount = view.getFloatOr(HEAL_AMOUNT, 0f);
        this.healRadius = view.getDoubleOr(HEAL_RADIUS, 0);
        this.canBoost = view.getBooleanOr(CAN_BOOST, false);
        this.maxBoostTimer = view.getIntOr(MAX_BOOST_TIMER, 200);
        this.boostRadius = view.getDoubleOr(BOOST_RADIUS, 0);

        this.knockbackTimer = Math.min(this.knockbackTimer, this.maxKnockbackTimer);
        this.healTimer = Math.min(this.healTimer, this.maxHealTimer);
        this.boostTimer = Math.min(this.boostTimer, this.maxBoostTimer);
    }

    @Inject(method="save", at=@At("TAIL"))
    private void writeExtendedNbt (ValueOutput view, CallbackInfo ci) {
        view.putBoolean(CAN_KNOCKBACK, this.canKnockback);
        view.putInt(MAX_KNOCKBACK_TIMER, this.maxKnockbackTimer);
        view.putDouble(KNOCKBACK_STRENGTH, this.knockbackStrength);
        view.putDouble(KNOCKBACK_RADIUS, this.knockbackRadius);

        view.putBoolean(CAN_HEAL, this.canHeal);
        view.putInt(MAX_HEAL_TIMER, this.maxHealTimer);
        view.putFloat(HEAL_AMOUNT, this.healAmount);
        view.putDouble(HEAL_RADIUS, this.healRadius);

        view.putBoolean(CAN_BOOST, this.canBoost);
        view.putInt(MAX_BOOST_TIMER, this.maxBoostTimer);
        view.putDouble(BOOST_RADIUS, this.boostRadius);
    }

    @Inject(method="serverTick", at=@At(
        value="INVOKE",
        target="Lnet/minecraft/world/level/BaseSpawner;isNearPlayer(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;)Z",
        shift=At.Shift.AFTER)
    )
    private void performActions (ServerLevel world, BlockPos pos, CallbackInfo ci) {
        Vec3 centrePos = pos.getCenter();

        if (this.canKnockback) {
            --this.knockbackTimer;

            if (this.knockbackTimer == this.maxKnockbackTimer / 2) {
                world.sendParticles(new AOEChargingParticleEffect(this.knockbackTimer, ARGB.vector3fFromRGB24(0xE2E2E2)), centrePos.x(), pos.getY() + 0.025, centrePos.z(), 1, 0, 0, 0, 0);
            }
            else if (this.knockbackTimer <= 0) {
                this.knockbackTimer = this.maxKnockbackTimer;
                world.sendParticles(ParticleTypes.GUST_EMITTER_SMALL, centrePos.x(), centrePos.y(), centrePos.z(), 1, 0, 0, 0, 0);
                List<ServerPlayer> players = world.getPlayers(player -> player.position().closerThan(centrePos, this.knockbackRadius, this.knockbackRadius) && !player.isCreative() && !player.isSpectator());
                for (ServerPlayer player : players) {
                    double strength = this.knockbackStrength * (1 - player.getAttributeValue(Attributes.KNOCKBACK_RESISTANCE));
                    if (strength > 0) {
                        Vec3 velocity = new Vec3(player.getX() - centrePos.x(), player.getY() >= pos.getY() ? 0.5 : -0.5, player.getZ() - centrePos.z()).normalize().scale(strength);
                        player.push(velocity);
                        player.needsSync = true;
                    }
                }
                world.playSound(null, pos, CSSoundEvents.BLOCK_SPAWNER_KNOCKBACK, SoundSource.BLOCKS, 1, 1);
            }
        }

        if (this.canHeal) {
            --this.healTimer;

            if (this.healTimer == this.maxHealTimer / 2) {
                world.sendParticles(new AOEChargingParticleEffect(this.healTimer, ARGB.vector3fFromRGB24(0x47BC78)), centrePos.x(), pos.getY() + 0.025, centrePos.z(), 1, 0, 0, 0, 0);
            }
            else if (this.healTimer <= 0) {
                this.healTimer = this.maxHealTimer;
                world.sendParticles(CSParticleTypes.HEAL, centrePos.x(), centrePos.y() + 0.5, centrePos.z(), 1, 0, 0, 0, 0);
                List<Monster> mobs = world.getEntitiesOfClass(Monster.class, AABB.ofSize(centrePos, this.healRadius, 3, this.healRadius), mob -> true);
                for (Monster hostile : mobs) {
                    hostile.heal(this.healAmount);
                    world.sendParticles(ParticleTypes.HAPPY_VILLAGER, hostile.getX(), hostile.getEyeY(), hostile.getZ(), 8, 0.35, 0.35, 0.35, 0);
                }
                world.playSound(null, pos, CSSoundEvents.BLOCK_SPAWNER_HEAL, SoundSource.BLOCKS, 1, 1);
            }
        }

        if (this.canBoost) {
            --this.boostTimer;

            if (this.boostTimer == this.maxBoostTimer / 2) {
                world.sendParticles(new AOEChargingParticleEffect(this.boostTimer, ARGB.vector3fFromRGB24(0xFF8459)), centrePos.x(), pos.getY() + 0.025, centrePos.z(), 1, 0, 0, 0, 0);
            }
            else if (this.boostTimer <= 0) {
                this.boostTimer = this.maxBoostTimer;
                world.sendParticles(CSParticleTypes.BOOST, centrePos.x(), centrePos.y() + 0.5, centrePos.z(), 1, 0, 0, 0, 0);
                List<Monster> mobs = world.getEntitiesOfClass(Monster.class, AABB.ofSize(centrePos, this.boostRadius, 3, this.boostRadius), mob -> true);
                for (Monster hostile : mobs) {
                    hostile.addEffect(new MobEffectInstance(MobEffects.SPEED, 30, 1));
                    world.sendParticles(ParticleTypes.HAPPY_VILLAGER, hostile.getX(), hostile.getEyeY(), hostile.getZ(), 8, 0.35, 0.35, 0.35, 0);
                }
                world.playSound(null, pos, CSSoundEvents.BLOCK_SPAWNER_BOOST, SoundSource.BLOCKS, 1, 1);
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
