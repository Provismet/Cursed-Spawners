package com.provismet.cursedspawners.mixin;

import com.provismet.cursedspawners.imixin.IMixinMobSpawnerBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.MobSpawnerBlockEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.SilverfishEntity;
import net.minecraft.entity.mob.VexEntity;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Mixin(MobSpawnerBlockEntity.class)
public abstract class MobSpawnerBlockEntityMixin extends BlockEntity implements IMixinMobSpawnerBlockEntity {
    @Unique private static final String REFORGE_ACTIONS = "ReforgeActions";
    @Unique private static final String BREAK_ACTION = "BreakAction";
    @Unique private static final String NORMAL_BREAK = "normal";
    @Unique private static final String SUMMON_VEX = "vex";
    @Unique private static final String SUMMON_SILVERFISH = "silverfish";
    @Unique private static final String CURSE = "curse";

    @Unique private static final String MIMIC_CHANCE = "MimicChance";
    @Unique private static final double PASSTHROUGH_MIMIC_CHANCE = -1;

    public MobSpawnerBlockEntityMixin (BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Unique private boolean hasAssignedNbt = false;
    @Unique private double mimicChance;
    @Unique private List<String> reforgeActions;
    @Unique private String breakAction;

    @Inject(method="readNbt", at=@At("TAIL"))
    private void readExtendedNbt (NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup, CallbackInfo info) {
        this.hasAssignedNbt = true;

        if (nbt.contains(MIMIC_CHANCE)) this.mimicChance = nbt.getDouble(MIMIC_CHANCE);
        else this.mimicChance = PASSTHROUGH_MIMIC_CHANCE;

        if (nbt.contains(REFORGE_ACTIONS)) {
            reforgeActions = new ArrayList<>();
            reforgeActions.addAll(nbt.getList(REFORGE_ACTIONS, NbtElement.STRING_TYPE).stream().map(NbtElement::asString).toList());
        }
        else this.reforgeActions = new ArrayList<>();

        if (nbt.contains(BREAK_ACTION)) this.breakAction = nbt.getString(BREAK_ACTION);
        else this.breakAction = NORMAL_BREAK;
    }

    @Inject(method="writeNbt", at=@At("TAIL"))
    private void writeExtendedNbt (NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup, CallbackInfo info) {
        if (!this.hasAssignedNbt) return;

        nbt.putDouble(MIMIC_CHANCE, this.mimicChance);

        NbtList actions = new NbtList();
        for (String action : this.reforgeActions) {
            actions.add(NbtString.of(action));
        }
        nbt.put(REFORGE_ACTIONS, actions);
        nbt.putString(BREAK_ACTION, this.breakAction);
    }

    @Override
    public double cursed_spawners$getMimicChance () {
        return this.mimicChance;
    }

    @Override
    public boolean cursed_spawners$useWorldMimicChance () {
        return this.mimicChance == PASSTHROUGH_MIMIC_CHANCE;
    }

    @Unique
    private void performBreakAction (String actionType, ServerWorld world) {
        Vec3d centrePos = this.getPos().toCenterPos();
        if (Objects.equals(actionType, SUMMON_VEX)) {
            for (int i = 0; i < 3; ++i) {
                VexEntity vex = new VexEntity(EntityType.VEX, world);
                vex.refreshPositionAndAngles(centrePos.getX(), centrePos.getY() + 1, centrePos.getZ(), 0f, 0f);
                vex.equipStack(EquipmentSlot.MAINHAND, Items.STONE_SWORD.getDefaultStack());
                world.spawnNewEntityAndPassengers(vex);
            }
        }
        else if (Objects.equals(actionType, SUMMON_SILVERFISH)) {
            for (int i = 0; i < 5; ++i) {
                SilverfishEntity silverfish = new SilverfishEntity(EntityType.SILVERFISH, world);
                silverfish.refreshPositionAndAngles(centrePos.getX(), centrePos.getY() + 1, centrePos.getZ(), 0f, 0f);
                world.spawnNewEntityAndPassengers(silverfish);
            }
        }
        else if (Objects.equals(actionType, CURSE)) {
            List<ServerPlayerEntity> playersInRange = world.getPlayers(player -> player.getPos().isWithinRangeOf(this.getPos().toCenterPos(), 8, 8) && !player.isCreative() && !player.isSpectator());
            for (ServerPlayerEntity player : playersInRange) {
                player.addStatusEffect(new StatusEffectInstance(StatusEffects.BLINDNESS, 60));
                player.addStatusEffect(new StatusEffectInstance(StatusEffects.POISON, 60, 2));
            }
        }
    }

    @Override
    public boolean cursed_spawners$attemptBreak () {
        if (!(this.world instanceof ServerWorld serverWorld)) return true;
        if (this.reforgeActions.isEmpty()) {
            this.performBreakAction(this.breakAction, serverWorld);
            return true;
        }

        String nextAction = this.reforgeActions.removeFirst();
        Vec3d centrePos = this.getPos().toCenterPos();
        serverWorld.spawnParticles(ParticleTypes.POOF, centrePos.getX(), centrePos.getY(), centrePos.getZ(), 20, 0.5, 0.5, 0.5, 0);
        this.performBreakAction(nextAction, serverWorld);
        return false;
    }
}
