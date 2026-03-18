package io.splatage.leaf.config.modules.entities;

import io.splatage.leaf.config.SplatageConfigModules;
import java.util.Arrays;

public final class WildComposition extends SplatageConfigModules {

    private static final String BASE_PATH = "entities.wild-composition";

    public static boolean enabled = true;

    public static int centerX = 0;
    public static int centerZ = 0;

    public static final double[] startPercents = new double[WildCompositionType.values().length];
    public static final double[] maxPercents = new double[WildCompositionType.values().length];
    public static final int[] distanceToMax = new int[WildCompositionType.values().length];
    public static final boolean[] scalingEnabled = new boolean[WildCompositionType.values().length];

    public enum WildCompositionType {
        ZOMBIE_BABY_PACK("zombie-baby-pack", "Distance scaling for zombie-line baby pack rolls."),
        ZOMBIE_LEADER("zombie-leader", "Distance scaling for zombie-line leader and door-breaker rolls.");

        private final String configKey;
        private final String description;

        WildCompositionType(final String configKey, final String description) {
            this.configKey = configKey;
            this.description = description;
        }

        public String configKey() {
            return this.configKey;
        }

        public String description() {
            return this.description;
        }
    }

    static {
        Arrays.fill(startPercents, -1.0D);
        Arrays.fill(maxPercents, -1.0D);
        Arrays.fill(distanceToMax, -1);
        Arrays.fill(scalingEnabled, false);
    }

    @Override
    public void onLoaded() {
        config.addComment(BASE_PATH, """
            Distance-based hostile pack composition tuning.
            Percent values are relative to the existing vanilla/fork roll where 100.0 means unchanged.
            -1 disables scaling for an individual composition roll and preserves its existing chance.
            Distance values are configured in blocks.
            Composition scaling is measured from configurable center-x and center-z block coordinates.
            This only affects existing vanilla/fork composition hooks already present in mob finalizeSpawn or attribute setup.
            """);

        enabled = config.getBoolean(
            BASE_PATH + ".enabled",
            enabled,
            "Enable distance-based hostile pack composition scaling. Disabled preserves existing composition rolls."
        );

        centerX = config.getInt(
            BASE_PATH + ".center-x",
            centerX,
            "Block X coordinate used as the center of hostile pack composition scaling."
        );

        centerZ = config.getInt(
            BASE_PATH + ".center-z",
            centerZ,
            "Block Z coordinate used as the center of hostile pack composition scaling."
        );

        for (final WildCompositionType type : WildCompositionType.values()) {
            final int index = type.ordinal();
            final String path = BASE_PATH + "." + type.configKey();

            startPercents[index] = config.getDouble(
                path + ".start-percent",
                startPercents[index],
                type.description() + " 100.0 means unchanged at the configured center. -1 disables scaling for this composition roll."
            );

            maxPercents[index] = config.getDouble(
                path + ".max-percent",
                maxPercents[index],
                type.description() + " 100.0 means unchanged at or beyond distance-to-max. -1 disables scaling for this composition roll."
            );

            distanceToMax[index] = config.getInt(
                path + ".distance-to-max",
                distanceToMax[index],
                "Distance in blocks from the configured center where this composition roll reaches max-percent. -1 disables scaling for this composition roll."
            );

            scalingEnabled[index] = enabled
                && startPercents[index] >= 0.0D
                && maxPercents[index] >= 0.0D
                && distanceToMax[index] > 0;
        }
    }

    public static boolean isScalingEnabled(final WildCompositionType type) {
        return scalingEnabled[type.ordinal()];
    }

    public static double getStartPercent(final WildCompositionType type) {
        return startPercents[type.ordinal()];
    }

    public static double getMaxPercent(final WildCompositionType type) {
        return maxPercents[type.ordinal()];
    }

    public static int getDistanceToMax(final WildCompositionType type) {
        return distanceToMax[type.ordinal()];
    }
}
