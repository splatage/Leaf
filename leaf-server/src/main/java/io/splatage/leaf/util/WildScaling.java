package io.splatage.leaf.util;

import io.splatage.leaf.config.modules.worldgen.OreRichness;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;

public final class WildScaling {

    private WildScaling() {
    }

    public static double getChunkDistanceFactor(final ServerLevel level, final ChunkPos chunkPos, final double maxChunkRadiusSq) {
        if (maxChunkRadiusSq <= 0.0D) {
            return 0.0D;
        }

        final double chunkCenterX = chunkPos.x + 0.5D;
        final double chunkCenterZ = chunkPos.z + 0.5D;
        final double dx = chunkCenterX - OreRichness.centerChunkX;
        final double dz = chunkCenterZ - OreRichness.centerChunkZ;
        final double chunkDistanceSq = dx * dx + dz * dz;

        if (chunkDistanceSq <= 0.0D) {
            return 0.0D;
        }

        if (chunkDistanceSq >= maxChunkRadiusSq) {
            return 1.0D;
        }

        return Math.sqrt(chunkDistanceSq / maxChunkRadiusSq);
    }

    public static double getBlockDistanceFactor(final ServerLevel level, final BlockPos origin, final int maxDistanceBlocks) {
        if (maxDistanceBlocks <= 0) {
            return 0.0D;
        }

        final double dx = origin.getX() - OreRichness.centerX;
        final double dz = origin.getZ() - OreRichness.centerZ;
        final double distance = Math.sqrt(dx * dx + dz * dz);

        if (distance <= 0.0D) {
            return 0.0D;
        }

        return Math.min(1.0D, distance / (double) maxDistanceBlocks);
    }

    public static double lerpPercent(final double factor, final double startPercent, final double maxPercent) {
        final double clamped = clamp01(factor);
        return startPercent + clamped * (maxPercent - startPercent);
    }

    private static double clamp01(final double value) {
        if (value <= 0.0D) {
            return 0.0D;
        }
        if (value >= 1.0D) {
            return 1.0D;
        }
        return value;
    }
}
