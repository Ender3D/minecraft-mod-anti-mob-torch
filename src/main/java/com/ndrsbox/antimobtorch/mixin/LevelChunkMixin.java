package com.ndrsbox.antimobtorch.mixin;

import com.ndrsbox.antimobtorch.TorchManager;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LevelChunk.class)
public class LevelChunkMixin {

    @Inject(method = "setLoaded", at = @At("HEAD"))
    private void onChunkLoadStateChange(boolean loaded, CallbackInfo ci) {
        LevelChunk chunk = (LevelChunk) (Object) this;

        if (chunk.getLevel() != null && !chunk.getLevel().isClientSide()) {
            if (loaded) {
                int minX = chunk.getPos().getMinBlockX();
                int minZ = chunk.getPos().getMinBlockZ();

                // Scan the blocks ONCE when the chunk loads into server memory
                for (int x = 0; x < 16; x++) {
                    for (int z = 0; z < 16; z++) {
                        for (int y = chunk.getLevel().getMinY(); y < chunk.getLevel().getMaxY(); y++) {
                            BlockPos checkPos = new BlockPos(minX + x, y, minZ + z);
                            BlockState state = chunk.getBlockState(checkPos);

                            if (state.getBlock() == Blocks.SOUL_TORCH ||
                                    state.getBlock() == Blocks.SOUL_WALL_TORCH ||
                                    state.getBlock() == Blocks.SOUL_LANTERN) {

                                TorchManager.addTorch(chunk.getLevel(), checkPos);
                            }
                        }
                    }
                }
            } else {
                // Clear memory cache when the chunk unloads to prevent RAM leaks
                TorchManager.clearChunk(chunk.getLevel(), chunk.getPos());
            }
        }
    }
}
