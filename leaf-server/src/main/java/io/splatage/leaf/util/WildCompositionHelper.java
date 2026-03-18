package io.splatage.leaf.util;

import io.splatage.leaf.config.modules.entities.WildComposition;
import io.splatage.leaf.config.modules.entities.WildComposition.WildCompositionType;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Mob;

public final class WildCompositionHelper {

    private WildCompositionHelper() {
    }

    public static boolean roll(final Mob mob, final WildCompositionType compositionType, final double baseChance) {
        if (baseChance <= 0.0D) {
            return false;
        }

        if (!(mob.level() instanceof ServerLevel serverLevel)) {
            return mob.getRandom().nextDouble() < clampChance(baseChance);
        }

        return mob.getRandom().nextDouble() < resolveChance(
            serverLevel,
            mob.blockPosition(),
            compositionType,
            baseChance
        );
    }

    public static double resolveChance(
        final ServerLevel level,
        final BlockPos origin,
        final WildCompositionType compositionType,
        final double baseChance
    ) {
        if (baseChance <= 0.0D) {
            return 0.0D;
        }

        if (!WildComposition.isScalingEnabled(compositionType)) {
            return clampChance(baseChance);
        }

        final double factor = WildScaling.getBlockDistanceFactor(
            origin,
            WildComposition.centerX,
            WildComposition.centerZ,
            WildComposition.getDistanceToMax(compositionType)
        );

        final double percent = WildScaling.lerpPercent(
            factor,
            WildComposition.getStartPercent(compositionType),
            WildComposition.getMaxPercent(compositionType)
        );

        return clampChance(baseChance * Math.max(0.0D, percent / 100.0D));
    }

    private static double clampChance(final double chance) {
        if (chance <= 0.0D) {
            return 0.0D;
        }
        if (chance >= 1.0D) {
            return 1.0D;
        }
        return chance;
    }
}
