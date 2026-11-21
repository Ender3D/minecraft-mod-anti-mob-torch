package com.ndrsbox.antimobtorch;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.monster.Phantom;
import net.minecraft.world.phys.Vec3;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Set;
import java.util.HashSet;

public class Antimobtorch implements ModInitializer {
	public static final String MOD_ID = "antimobtorch";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		LOGGER.info("Initializing Anti Mob Torch!");

		ModConfig.load();

		// 1. Safe Block Break Hook: Cleans up data when a player breaks a torch/lantern
		PlayerBlockBreakEvents.AFTER.register((level, player, pos, state, blockEntity) -> {
			if (!level.isClientSide() &&
					(state.getBlock() == net.minecraft.world.level.block.Blocks.SOUL_TORCH ||
							state.getBlock() == net.minecraft.world.level.block.Blocks.SOUL_WALL_TORCH ||
							state.getBlock() == net.minecraft.world.level.block.Blocks.SOUL_LANTERN)) {
				TorchManager.removeTorch(level, pos);
			}
		});

		// 2. Physics Repulsion Loop: Pulls coordinates instantly from RAM memory cache every single tick
		ServerTickEvents.END_LEVEL_TICK.register(level -> {
			// Collect unique monsters near ANY player first so overlapping search boxes don't double-push
			Set<Mob> uniqueMobs = new HashSet<>();
			for (net.minecraft.server.level.ServerPlayer player : level.players()) {
				net.minecraft.world.phys.AABB searchBox = player.getBoundingBox().inflate(48.0);
				uniqueMobs.addAll(level.getEntitiesOfClass(Mob.class, searchBox, m -> m.getType().getCategory() == MobCategory.MONSTER));
			}

			for (Mob mob : uniqueMobs) {
				BlockPos mobPos = mob.blockPosition();
				int targetChunkX = mobPos.getX() >> 4;
				int targetChunkZ = mobPos.getZ() >> 4;
				boolean appliedPush = false;

				// Scan a tight 5x5 chunk grid from memory (flawless, instantaneous lookup)
				for (int cx = -2; cx <= 2 && !appliedPush; cx++) {
					for (int cz = -2; cz <= 2 && !appliedPush; cz++) {
						ChunkPos checkChunk = new ChunkPos(targetChunkX + cx, targetChunkZ + cz);
						Set<BlockPos> torches = TorchManager.getTorchesInChunk(level, checkChunk);

						for (BlockPos torchPos : torches) {
							net.minecraft.world.level.block.state.BlockState torchState = level.getBlockState(torchPos);
							boolean isTorch = torchState.getBlock() == net.minecraft.world.level.block.Blocks.SOUL_TORCH ||
									torchState.getBlock() == net.minecraft.world.level.block.Blocks.SOUL_WALL_TORCH;
							boolean isLantern = torchState.getBlock() == net.minecraft.world.level.block.Blocks.SOUL_LANTERN;

							if (isTorch || isLantern) {
								double radius = isLantern ? ModConfig.get().lanternRadius : ModConfig.get().torchRadius;
								double baseStrength = isLantern ? ModConfig.get().lanternPushStrength : ModConfig.get().torchPushStrength;

								Vec3 torchVec = new Vec3(torchPos.getX() + 0.5, mob.getY(), torchPos.getZ() + 0.5);
								Vec3 mobVec = mob.position();
								double distance = mobVec.distanceTo(torchVec);

								if (distance < radius && distance > 0.1) {
									Vec3 pushVector = mobVec.subtract(torchVec).normalize();
									double forceFactor = (radius - distance) / radius;

									// Scale the base passive push strength up slightly to overcome vanilla pathing friction
									double pushStrength = baseStrength * forceFactor * 1.5;

									double verticalPush = 0.02; // Keep ground mobs tight to the floor unless hit
									if (mob instanceof Phantom) {
										pushStrength *= 1.8;
										verticalPush = 0.35;
									}

									// Get current velocity to safely append forces without exponential multiplication spikes
									Vec3 currentVelocity = mob.getDeltaMovement();

									// If a mob is traveling toward the torch, aggressively counter its movement.
									// If it's already flying backward from a hit, dampen our force so it doesn't break orbit.
									double finalX = (currentVelocity.x * 0.8) + (pushVector.x * pushStrength);
									double finalZ = (currentVelocity.z * 0.8) + (pushVector.z * pushStrength);
									double finalY = currentVelocity.y + verticalPush;

									mob.setDeltaMovement(new Vec3(finalX, finalY, finalZ));
									mob.syncVelocity = true;

									appliedPush = true;
									break;
								}
							}
						}
					}
				}
			}
		});

	}

	public static Identifier id(String path) {
		return Identifier.fromNamespaceAndPath(MOD_ID, path);
	}
}
