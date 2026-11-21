package com.ndrsbox.antimobtorch;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ChunkPos;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class TorchManager {
    private static final Map<ResourceKey<Level>, Map<ChunkPos, Set<BlockPos>>> TORCH_REGISTRY = new ConcurrentHashMap<>();

    public static void addTorch(Level level, BlockPos pos) {
        TORCH_REGISTRY
                .computeIfAbsent(level.dimension(), k -> new ConcurrentHashMap<>())
                .computeIfAbsent(new ChunkPos(pos.getX() >> 4, pos.getZ() >> 4), k -> Collections.synchronizedSet(new HashSet<>()))
                .add(pos);
    }

    public static void removeTorch(Level level, BlockPos pos) {
        Map<ChunkPos, Set<BlockPos>> dimensionMap = TORCH_REGISTRY.get(level.dimension());
        if (dimensionMap != null) {
            Set<BlockPos> chunkTorches = dimensionMap.get(new ChunkPos(pos.getX() >> 4, pos.getZ() >> 4));
            if (chunkTorches != null) {
                chunkTorches.remove(pos);
            }
        }
    }

    public static void clearChunk(Level level, ChunkPos chunkPos) {
        Map<ChunkPos, Set<BlockPos>> dimensionMap = TORCH_REGISTRY.get(level.dimension());
        if (dimensionMap != null) {
            dimensionMap.remove(chunkPos);
        }
    }

    public static Set<BlockPos> getTorchesInChunk(Level level, ChunkPos chunkPos) {
        Map<ChunkPos, Set<BlockPos>> dimensionMap = TORCH_REGISTRY.get(level.dimension());
        if (dimensionMap == null) return Collections.emptySet();
        Set<BlockPos> torches = dimensionMap.get(chunkPos);
        return torches != null ? torches : Collections.emptySet();
    }
}
