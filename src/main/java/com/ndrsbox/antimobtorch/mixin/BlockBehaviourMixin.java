package com.ndrsbox.antimobtorch.mixin;

import com.ndrsbox.antimobtorch.TorchManager;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BlockBehaviour.class)
public class BlockBehaviourMixin {

    @Inject(method = "onPlace", at = @At("TAIL"))
    private void onTorchPlaced(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean isMoving, CallbackInfo ci) {
        if (level instanceof net.minecraft.server.level.ServerLevel &&
                (state.getBlock() == Blocks.SOUL_TORCH || state.getBlock() == Blocks.SOUL_WALL_TORCH || state.getBlock() == Blocks.SOUL_LANTERN)) {
            TorchManager.addTorch(level, pos);
        }
    }
}
