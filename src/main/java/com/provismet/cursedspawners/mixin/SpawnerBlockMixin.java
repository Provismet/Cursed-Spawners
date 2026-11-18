package com.provismet.cursedspawners.mixin;

import com.provismet.cursedspawners.CursedSpawnersMain;
import com.provismet.cursedspawners.entity.SpawnerMimicEntity;
import com.provismet.cursedspawners.networking.ClientPacketReceiver;
import com.provismet.cursedspawners.utility.CSGamerules;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.Containers;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.SpawnerBlock;
import net.minecraft.world.level.block.entity.SpawnerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.TagValueInput;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.UUID;

@Mixin(SpawnerBlock.class)
public abstract class SpawnerBlockMixin extends BaseEntityBlock {
    protected SpawnerBlockMixin (Properties settings) {
        super(settings);
    }

    @Override
    public BlockState playerWillDestroy (Level world, BlockPos pos, BlockState state, Player player) {
        state = super.playerWillDestroy(world, pos, state, player);
        if (player.isCreative() || !(world instanceof ServerLevel serverWorld)) return state;

        if (world.getBlockEntity(pos) instanceof SpawnerBlockEntity blockEntity) {
            if (world.getRandom().nextDouble() <= blockEntity.cursed_spawners$getSpawnerData().getEffectiveMimicChance(serverWorld)) {
                CompoundTag nbt = blockEntity.saveWithFullMetadata(world.registryAccess()).copy();
                SpawnerMimicEntity mimic = new SpawnerMimicEntity(world);
                UUID uuid = mimic.getUUID();

                if (nbt.getShort("MinSpawnDelay").isPresent()) nbt.putShort("MinSpawnDelay", (short)(nbt.getShort("MinSpawnDelay").get() / 1.5));
                if (nbt.getShort("MaxSpawnDelay").isPresent()) nbt.putShort("MaxSpawnDelay", (short)(nbt.getShort("MaxSpawnDelay").get() / 1.5));
                if (nbt.contains("Delay")) nbt.putShort("Delay", (short)20);

                try (ProblemReporter.ScopedCollector logging = new ProblemReporter.ScopedCollector(this::toString, CursedSpawnersMain.LOGGER)) {
                    ValueInput view = TagValueInput.create(logging, serverWorld.registryAccess(), nbt);
                    mimic.load(view);
                    mimic.setUUID(uuid);
                    mimic.snapTo(pos, 0, 0);
                    mimic.setLastHurtByMob(player);
                    world.addFreshEntity(mimic);
                }
            }
            else {
                Containers.updateNeighboursAfterDestroy(state, world, pos);
            }
        }
        return state;
    }

    @Override
    protected float getDestroyProgress (BlockState state, Player player, BlockGetter world, BlockPos pos) {
        float scale = super.getDestroyProgress(state, player, world, pos);

        if (player.level().isClientSide()) scale *= ClientPacketReceiver.SPAWNER_BREAK_MODIFIER;
        else if (player.level() instanceof ServerLevel serverWorld) scale *= serverWorld.getGameRules().get(CSGamerules.BREAK_SPEED).floatValue();

        return scale;
    }

    @Inject(method="spawnAfterBreak", at=@At("HEAD"), cancellable=true)
    private void preventExp (BlockState state, ServerLevel world, BlockPos pos, ItemStack tool, boolean dropExperience, CallbackInfo info) {
        if (!world.getEntitiesOfClass(SpawnerMimicEntity.class, AABB.ofSize(Vec3.atCenterOf(pos), 0.1, 0.1, 0.1),entity -> true).isEmpty())
            info.cancel();
    }
}
