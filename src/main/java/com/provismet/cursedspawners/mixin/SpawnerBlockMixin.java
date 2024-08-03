package com.provismet.cursedspawners.mixin;

import com.provismet.cursedspawners.entity.SpawnerMimicEntity;
import com.provismet.cursedspawners.imixin.IMixinMobSpawnerBlockEntity;
import com.provismet.cursedspawners.networking.ClientPacketReceiver;
import com.provismet.cursedspawners.utility.CSGamerules;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.SpawnerBlock;
import net.minecraft.block.entity.MobSpawnerBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;

import java.util.UUID;

@Mixin(SpawnerBlock.class)
public abstract class SpawnerBlockMixin extends BlockWithEntity {
    protected SpawnerBlockMixin (Settings settings) {
        super(settings);
    }

    @Override
    public BlockState onBreak (World world, BlockPos pos, BlockState state, PlayerEntity player) {
        state = super.onBreak(world, pos, state, player);
        if (player.isCreative()) return state;

        double worldMimicChance = world.getGameRules().get(CSGamerules.MIMIC_CHANCE).get();
        if (worldMimicChance >= 0 && world.getBlockEntity(pos) instanceof MobSpawnerBlockEntity blockEntity) {
            double blockMimicChance;
            if (((IMixinMobSpawnerBlockEntity)blockEntity).cursed_spawners$useWorldMimicChance()) blockMimicChance = worldMimicChance;
            else blockMimicChance = ((IMixinMobSpawnerBlockEntity)blockEntity).cursed_spawners$getMimicChance();

            if (world.getRandom().nextDouble() <= blockMimicChance) {
                NbtCompound nbt = blockEntity.createNbtWithIdentifyingData(world.getRegistryManager());
                SpawnerMimicEntity mimic = new SpawnerMimicEntity(world);
                UUID uuid = mimic.getUuid();
                mimic.readNbt(nbt);
                mimic.setUuid(uuid);
                mimic.refreshPositionAndAngles(pos, 0, 0);
                mimic.setAttacker(player);
                world.spawnEntity(mimic);
            }
        }
        return state;
    }

    @Override
    protected void onStateReplaced (BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        // This executes after the mimic is spawned.
        if (world.getNonSpectatingEntities(SpawnerMimicEntity.class, Box.of(pos.toCenterPos(), 0.1, 0.1, 0.1)).isEmpty())
            ItemScatterer.onStateReplaced(state, newState, world, pos);

        super.onStateReplaced(state, world, pos, newState, moved);
    }

    @Override
    protected float calcBlockBreakingDelta (BlockState state, PlayerEntity player, BlockView world, BlockPos pos) {
        float scale = super.calcBlockBreakingDelta(state, player, world, pos);

        if (player.getWorld().isClient()) scale *= ClientPacketReceiver.SPAWNER_BREAK_MODIFIER;
        else scale *= (float)player.getWorld().getGameRules().get(CSGamerules.BREAK_SPEED).get();

        return scale;
    }
}
