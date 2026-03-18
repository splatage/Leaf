package io.splatage.leaf.config.modules.entities;

import io.splatage.leaf.config.SplatageConfigModules;

public final class WildEncounters extends SplatageConfigModules {

    private static final String BASE_PATH = "entities.wild-encounters";

    public static boolean enabled = true;

    public static int centerX = 0;
    public static int centerZ = 0;

    public static double encounterStartPercent = -1.0D;
    public static double encounterMaxPercent = -1.0D;
    public static int encounterDistanceToMax = -1;
    public static boolean encounterScalingEnabled = false;

    public enum WildEncounterType {
        ZOMBIE_CHICKEN_JOCKEY,
        SPIDER_JOCKEY,
        STRIDER_ZOMBIFIED_PIGLIN_JOCKEY,
        STRIDER_BABY_PASSENGER,
        HUSK_CAMEL_PARCHED,
        DROWNED_ZOMBIE_NAUTILUS,
        ZOMBIE_HORSEMAN
    }

    @Override
    public void onLoaded() {
        config.addComment(BASE_PATH, """
            Distance-based special encounter tuning.
            Percent values are relative to the existing vanilla/fork encounter chance where 100.0 means unchanged.
            -1 disables encounter scaling.
            Distance values are configured in blocks.
            Encounter scaling is measured from configurable center-x and center-z block coordinates.
            This applies one shared encounter curve to all supported special encounter rolls in current spawn/finalizeSpawn hooks.
            """);

        enabled = config.getBoolean(
            BASE_PATH + ".enabled",
            enabled,
            "Enable distance-based special encounter scaling. Disabled preserves existing encounter chances."
        );

        centerX = config.getInt(
            BASE_PATH + ".center-x",
            centerX,
            "Block X coordinate used as the center of special encounter scaling."
        );

        centerZ = config.getInt(
            BASE_PATH + ".center-z",
            centerZ,
            "Block Z coordinate used as the center of special encounter scaling."
        );

        encounterStartPercent = config.getDouble(
            BASE_PATH + ".encounter.start-percent",
            encounterStartPercent,
            "Special encounter chance percent at the configured center. 100.0 means unchanged. -1 disables encounter scaling."
        );

        encounterMaxPercent = config.getDouble(
            BASE_PATH + ".encounter.max-percent",
            encounterMaxPercent,
            "Special encounter chance percent at or beyond distance-to-max. 100.0 means unchanged. -1 disables encounter scaling."
        );

        encounterDistanceToMax = config.getInt(
            BASE_PATH + ".encounter.distance-to-max",
            encounterDistanceToMax,
            "Distance in blocks from the configured center where special encounter chance reaches max-percent. -1 disables encounter scaling."
        );

        encounterScalingEnabled = enabled
            && encounterStartPercent >= 0.0D
            && encounterMaxPercent >= 0.0D
            && encounterDistanceToMax > 0;
    }

    public static boolean isScalingEnabled(final WildEncounterType type) {
        return encounterScalingEnabled;
    }

    public static double getStartPercent(final WildEncounterType type) {
        return encounterStartPercent;
    }

    public static double getMaxPercent(final WildEncounterType type) {
        return encounterMaxPercent;
    }

    public static int getDistanceToMax(final WildEncounterType type) {
        return encounterDistanceToMax;
    }
}
