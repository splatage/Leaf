package io.splatage.leaf.config.modules.spawning;

import io.splatage.leaf.config.SplatageConfigModules;

public final class HostileSpawnDensity extends SplatageConfigModules {

    private static final String BASE_PATH = "spawning.hostile-density";

    public static boolean enabled = true;

    public static int centerX = 0;
    public static int centerZ = 0;

    public static double startPercent = -1.0D;
    public static double maxPercent = -1.0D;
    public static int distanceToMax = -1;

    public static boolean densityEnabled = false;
    public static double centerChunkX = 0.0D;
    public static double centerChunkZ = 0.0D;
    public static double distanceToMaxChunksSq = -1.0D;

    @Override
    public void onLoaded() {
        config.addComment(BASE_PATH, """
            Distance-based hostile natural spawn density tuning.
            Percent values are relative to vanilla hostile spawning where 100.0 means vanilla.
            -1 disables density scaling.
            Distance values are configured in blocks and resolved internally in chunk-space.
            Density is measured from configurable center-x and center-z block coordinates.
            This only affects hostile natural spawning.
            """);

        enabled = config.getBoolean(
            BASE_PATH + ".enabled",
            enabled,
            "Enable distance-based hostile natural spawn density scaling."
        );

        centerX = config.getInt(
            BASE_PATH + ".center-x",
            centerX,
            "Block X coordinate used as the center of hostile density scaling."
        );

        centerZ = config.getInt(
            BASE_PATH + ".center-z",
            centerZ,
            "Block Z coordinate used as the center of hostile density scaling."
        );

        startPercent = config.getDouble(
            BASE_PATH + ".density.start-percent",
            startPercent,
            "Hostile spawn density percent at the configured center. 100.0 means vanilla. -1 disables density scaling."
        );

        maxPercent = config.getDouble(
            BASE_PATH + ".density.max-percent",
            maxPercent,
            "Hostile spawn density percent at or beyond distance-to-max. 100.0 means vanilla. -1 disables density scaling."
        );

        distanceToMax = config.getInt(
            BASE_PATH + ".density.distance-to-max",
            distanceToMax,
            "Distance in blocks from the configured center where hostile density reaches max-percent. -1 disables density scaling."
        );

        densityEnabled = enabled
            && startPercent >= 0.0D
            && maxPercent >= 0.0D
            && distanceToMax > 0;

        centerChunkX = centerX / 16.0D;
        centerChunkZ = centerZ / 16.0D;
        distanceToMaxChunksSq = densityEnabled ? square(distanceToMax / 16.0D) : -1.0D;
    }

    private static double square(final double value) {
        return value * value;
    }
}
