package io.splatage.leaf.util;

import io.splatage.leaf.config.modules.spawning.HostileSpawnDensity;
import java.util.Locale;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.ChunkPos;

public final class HostileSpawnDensityHelper {

    private HostileSpawnDensityHelper() {
    }

    public static boolean isEnabledFor(final ServerLevel level, final MobCategory category) {
        return HostileSpawnDensity.densityEnabled
            && category == MobCategory.MONSTER
            && isEnabledWorld(level);
    }

    public static double resolveChunkPercent(final ServerLevel level, final ChunkPos chunkPos, final MobCategory category) {
        if (!isEnabledFor(level, category)) {
            return 100.0D;
        }

        final double factor = WildScaling.getChunkDistanceFactor(
            chunkPos,
            HostileSpawnDensity.centerChunkX,
            HostileSpawnDensity.centerChunkZ,
            HostileSpawnDensity.distanceToMaxChunksSq
        );
        return WildScaling.lerpPercent(factor, HostileSpawnDensity.startPercent, HostileSpawnDensity.maxPercent);
    }

    public static int resolveChunkLimit(final ServerLevel level, final ChunkPos chunkPos, final MobCategory category, final int vanillaLimit) {
        if (!isEnabledFor(level, category)) {
            return vanillaLimit;
        }

        return scaleLimit(vanillaLimit, resolveChunkPercent(level, chunkPos, category));
    }

    public static int resolveGlobalLimit(final ServerLevel level, final MobCategory category, final int vanillaLimit) {
        if (!isEnabledFor(level, category)) {
            return vanillaLimit;
        }

        return scaleLimit(vanillaLimit, HostileSpawnDensity.maxPercent);
    }

    private static boolean isEnabledWorld(final ServerLevel level) {
        final String worldName = level.getWorld().getName();
        return worldName != null
            && HostileSpawnDensity.enabledWorlds.contains(worldName.toLowerCase(Locale.ROOT));
    }

    private static int scaleLimit(final int vanillaLimit, final double percent) {
        if (vanillaLimit <= 0) {
            return vanillaLimit;
        }

        final double multiplier = Math.max(0.0D, percent / 100.0D);
        return Math.max(0, Mth.floor(vanillaLimit * multiplier));
    }
}
