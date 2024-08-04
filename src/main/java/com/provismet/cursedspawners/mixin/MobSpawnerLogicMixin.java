package com.provismet.cursedspawners.mixin;

import com.provismet.cursedspawners.imixin.IMixinMobSpawnerLogic;
import com.provismet.cursedspawners.particle.effect.WindChargingParticleEffect;
import net.minecraft.block.spawner.MobSpawnerLogic;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
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
    @Unique private static final String MAX_KNOCKBACK_TIMER = "MaxKnockbackTimer";
    @Unique private static final String KNOCKBACK_STRENGTH = "KnockbackStrength";
    @Unique private static final String KNOCKBACK_RADIUS = "KnockbackRadius";

    @Unique private boolean canKnockback = false;
    @Unique private int knockbackTimer = 1;
    @Unique private int maxKnockbackTimer = 200;
    @Unique private double knockbackStrength = 0.2;
    @Unique private double knockbackRadius = 4;

    @Inject(method="readNbt", at=@At("TAIL"))
    private void readExtendedNbt (World world, BlockPos pos, NbtCompound nbt, CallbackInfo info) {
        if (nbt.contains(CAN_KNOCKBACK)) this.canKnockback = nbt.getBoolean(CAN_KNOCKBACK);
        else this.canKnockback = false;

        if (nbt.contains(MAX_KNOCKBACK_TIMER)) this.maxKnockbackTimer = nbt.getInt(MAX_KNOCKBACK_TIMER);
        else this.maxKnockbackTimer = 200;

        if (nbt.contains(KNOCKBACK_STRENGTH)) this.knockbackStrength = nbt.getDouble(KNOCKBACK_STRENGTH);
        else this.knockbackStrength = 0.2;

        if (nbt.contains(KNOCKBACK_RADIUS)) this.knockbackRadius = nbt.getDouble(KNOCKBACK_RADIUS);
        else this.knockbackRadius = 4;
    }

    @Inject(method="writeNbt", at=@At("TAIL"))
    private void writeExtendedNbt (NbtCompound nbt, CallbackInfoReturnable<NbtCompound> cir) {
        nbt.putBoolean(CAN_KNOCKBACK, this.canKnockback);
        nbt.putInt(MAX_KNOCKBACK_TIMER, this.maxKnockbackTimer);
        nbt.putDouble(KNOCKBACK_STRENGTH, this.knockbackStrength);
        nbt.putDouble(KNOCKBACK_RADIUS, this.knockbackRadius);
    }

    @Inject(method="serverTick", at=@At(
        value="INVOKE",
        target="Lnet/minecraft/block/spawner/MobSpawnerLogic;isPlayerInRange(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;)Z",
        shift=At.Shift.AFTER)
    )
    private void performActions (ServerWorld world, BlockPos pos, CallbackInfo ci) {
        if (this.canKnockback) {
            --this.knockbackTimer;
            if (this.knockbackTimer < 0) this.knockbackTimer = this.maxKnockbackTimer;
            if (this.knockbackTimer > this.maxKnockbackTimer) this.knockbackTimer = this.maxKnockbackTimer;

            if (this.knockbackTimer == this.maxKnockbackTimer / 2) {
                world.spawnParticles(new WindChargingParticleEffect(this.knockbackTimer), pos.getX() + 0.5, pos.getY() + 0.05, pos.getZ() + 0.5, 1, 0, 0, 0, 0);
            }
            else if (this.knockbackTimer <= 0) {
                Vec3d centrePos = pos.toCenterPos();
                world.spawnParticles(ParticleTypes.GUST_EMITTER_SMALL, centrePos.getX(), centrePos.getY(), centrePos.getZ(), 1, 0, 0, 0, 0);
                List<ServerPlayerEntity> players = world.getPlayers(player -> player.getPos().isWithinRangeOf(centrePos, this.knockbackRadius, this.knockbackRadius) && !player.isCreative() && !player.isSpectator());
                for (ServerPlayerEntity player : players) {
                    double strength = this.knockbackStrength * (1 - player.getAttributeValue(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE));
                    if (strength > 0) {
                        Vec3d velocity = new Vec3d(player.getX() - centrePos.getX(), player.getY() - centrePos.getY() - 0.6, player.getZ() - centrePos.getZ()).normalize().multiply(strength);
                        player.addVelocity(velocity);
                        player.velocityModified = true;
                    }
                }
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
        this.knockbackStrength = strength;
        this.knockbackRadius = radius;
    }
}
