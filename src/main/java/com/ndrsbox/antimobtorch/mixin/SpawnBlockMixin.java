package com.ndrsbox.antimobtorch.mixin;

import com.ndrsbox.antimobtorch.ModConfig;
import com.ndrsbox.antimobtorch.TorchManager;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.util.RandomSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import java.util.Set;

@Mixin(SpawnPlacements.class)
public class SpawnBlockMixin {

    @Inject(method = "checkSpawnRules", at = @At("HEAD"), cancellable = true)
    private static void onCheckSpawnRules(EntityType<?> entityType, ServerLevelAccessor levelAccessor, EntitySpawnReason spawnReason, BlockPos pos, RandomSource random, CallbackInfoReturnable<Boolean> cir) {
        if (ModConfig.get().preventNaturalSpawns) {
            if (entityType.getCategory() == MobCategory.MONSTER) {
                Level level = levelAccessor.getLevel();

                int targetChunkX = pos.getX() >> 4;
                int targetChunkZ = pos.getZ() >> 4;
                boolean shouldBlock = false;

                // Scan the 5x5 memory grid to see if this spawn position is inside a bubble dome
                for (int cx = -2; cx <= 2 && !shouldBlock; cx++) {
                    for (int cz = -2; cz <= 2 && !shouldBlock; cz++) {
                        ChunkPos checkChunk = new ChunkPos(targetChunkX + cx, targetChunkZ + cz);
                        Set<BlockPos> torches = TorchManager.getTorchesInChunk(level, checkChunk);

                        for (BlockPos torchPos : torches) {
                            net.minecraft.world.level.block.state.BlockState torchState = level.getBlockState(torchPos);
                            boolean isTorch = torchState.getBlock() == net.minecraft.world.level.block.Blocks.SOUL_TORCH ||
                                    torchState.getBlock() == net.minecraft.world.level.block.Blocks.SOUL_WALL_TORCH;
                            boolean isLantern = torchState.getBlock() == net.minecraft.world.level.block.Blocks.SOUL_LANTERN;

                            if (isTorch || isLantern) {
                                double radius = isLantern ? ModConfig.get().lanternRadius : ModConfig.get().torchRadius;

                                double dx = torchPos.getX() - pos.getX();
                                double dy = torchPos.getY() - pos.getY();
                                double dz = torchPos.getZ() - pos.getZ();
                                double distanceSqr = (dx * dx) + (dy * dy) + (dz * dz);

                                if (distanceSqr <= (radius * radius)) {
                                    shouldBlock = true;
                                    break;
                                }
                            }
                        }
                    }
                }

                if (shouldBlock) {
                    cir.setReturnValue(false);
                }
            }
        }
    }
}
