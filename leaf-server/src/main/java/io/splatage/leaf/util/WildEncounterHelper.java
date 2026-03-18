package io.splatage.leaf.util;

import io.splatage.leaf.config.modules.entities.WildEncounters;
import io.splatage.leaf.config.modules.entities.WildEncounters.WildEncounterType;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Mob;

public final class WildEncounterHelper {

    private WildEncounterHelper() {
    }

    public static boolean rollEncounter(final Mob mob, final WildEncounterType encounterType, final double baseChance) {
        if (baseChance <= 0.0D) {
            return false;
        }

        if (!(mob.level() instanceof ServerLevel serverLevel)) {
            return mob.getRandom().nextDouble() < clampChance(baseChance);
        }

        return mob.getRandom().nextDouble() < resolveChance(serverLevel, mob.blockPosition(), encounterType, baseChance);
    }

    public static double resolveChance(
        final ServerLevel level,
        final BlockPos origin,
        final WildEncounterType encounterType,
        final double baseChance
    ) {
        if (baseChance <= 0.0D) {
            return 0.0D;
        }

        if (!WildEncounters.encounterScalingEnabled) {
            return clampChance(baseChance);
        }

        final double factor = WildScaling.getBlockDistanceFactor(
            origin,
            WildEncounters.centerX,
            WildEncounters.centerZ,
            WildEncounters.encounterDistanceToMax
        );
        final double percent = WildScaling.lerpPercent(
            factor,
            WildEncounters.encounterStartPercent,
            WildEncounters.encounterMaxPercent
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
