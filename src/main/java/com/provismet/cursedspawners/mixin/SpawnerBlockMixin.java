package com.provismet.cursedspawners.mixin;

import com.provismet.cursedspawners.entity.SpawnerMimicEntity;
import com.provismet.cursedspawners.imixin.IMixinMobSpawnerBlockEntity;
import com.provismet.cursedspawners.networking.ClientPacketReceiver;
import com.provismet.cursedspawners.utility.CSGamerules;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.Blocks;
import net.minecraft.block.SpawnerBlock;
import net.minecraft.block.entity.MobSpawnerBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

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
                NbtCompound nbt = blockEntity.createNbtWithIdentifyingData(world.getRegistryManager()).copy();
                SpawnerMimicEntity mimic = new SpawnerMimicEntity(world);
                UUID uuid = mimic.getUuid();

                if (nbt.contains("MinSpawnDelay", NbtElement.NUMBER_TYPE)) nbt.putShort("MinSpawnDelay", (short)(nbt.getShort("MinSpawnDelay") / 1.5));
                if (nbt.contains("MaxSpawnDelay", NbtElement.NUMBER_TYPE)) nbt.putShort("MaxSpawnDelay", (short)(nbt.getShort("MaxSpawnDelay") / 1.5));
                if (nbt.contains("Delay", NbtElement.NUMBER_TYPE)) nbt.putShort("Delay", (short)20);

                mimic.readNbt(nbt);
                mimic.setUuid(uuid);
                mimic.refreshPositionAndAngles(pos, 0, 0);
                mimic.setAttacker(player);
                world.spawnEntity(mimic);
            }
            else {
                BlockState air = Blocks.AIR.getDefaultState();
                ItemScatterer.onStateReplaced(state, air, world, pos);
            }
        }
        return state;
    }

    @Override
    protected float calcBlockBreakingDelta (BlockState state, PlayerEntity player, BlockView world, BlockPos pos) {
        float scale = super.calcBlockBreakingDelta(state, player, world, pos);

        if (player.getWorld().isClient()) scale *= ClientPacketReceiver.SPAWNER_BREAK_MODIFIER;
        else scale *= (float)player.getWorld().getGameRules().get(CSGamerules.BREAK_SPEED).get();

        return scale;
    }

    @Inject(method="onStacksDropped", at=@At("HEAD"), cancellable=true)
    private void preventExp (BlockState state, ServerWorld world, BlockPos pos, ItemStack tool, boolean dropExperience, CallbackInfo info) {
        if (!world.getEntitiesByClass(SpawnerMimicEntity.class, Box.of(pos.toCenterPos(), 0.1, 0.1, 0.1), entity -> true).isEmpty())
            info.cancel();
    }
}
