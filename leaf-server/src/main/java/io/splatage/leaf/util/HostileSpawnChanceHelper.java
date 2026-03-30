package io.splatage.leaf.util;

import io.splatage.leaf.config.modules.spawning.HostileSpawnChance;
import java.util.Locale;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.level.LevelAccessor;

public final class HostileSpawnChanceHelper {

    private HostileSpawnChanceHelper() {
    }

    public static boolean passesSpawnChance(
        final LevelAccessor level,
        final BlockPos pos,
        final EntitySpawnReason spawnReason,
        final RandomSource random
    ) {
        if (!isEnabledFor(level, spawnReason)) {
            return true;
        }

        final double percent = resolveSpawnChancePercent(pos);
        if (percent <= 0.0D) {
            return false;
        }
        if (percent >= 100.0D) {
            return true;
        }

        return random.nextDouble() * 100.0D < percent;
    }

    public static boolean isEnabledFor(final LevelAccessor level, final EntitySpawnReason spawnReason) {
        return HostileSpawnChance.chanceEnabled
            && spawnReason == EntitySpawnReason.NATURAL
            && isEnabledWorld(level);
    }

    public static double resolveSpawnChancePercent(final BlockPos pos) {
        if (!HostileSpawnChance.chanceEnabled) {
            return 100.0D;
        }

        final double factor = resolveDistanceFactor(pos);
        final double percent = HostileSpawnChance.startPercent
            + factor * (HostileSpawnChance.maxPercent - HostileSpawnChance.startPercent);

        return clampPercent(percent);
    }

    private static boolean isEnabledWorld(final LevelAccessor level) {
        final String worldName = level.getMinecraftWorld().getWorld().getName();
        return worldName != null
            && HostileSpawnChance.enabledWorlds.contains(worldName.toLowerCase(Locale.ROOT));
    }

    private static double resolveDistanceFactor(final BlockPos pos) {
        if (HostileSpawnChance.distanceToMax <= 0) {
            return 0.0D;
        }

        final double dx = pos.getX() - HostileSpawnChance.centerX;
        final double dz = pos.getZ() - HostileSpawnChance.centerZ;
        final double distance = Math.sqrt(dx * dx + dz * dz);

        if (distance <= 0.0D) {
            return 0.0D;
        }

        return Math.min(1.0D, distance / (double) HostileSpawnChance.distanceToMax);
    }

    private static double clampPercent(final double percent) {
        if (percent <= 0.0D) {
            return 0.0D;
        }
        if (percent >= 100.0D) {
            return 100.0D;
        }
        return percent;
    }
}
