package io.splatage.leaf.config.modules.spawning;

import io.splatage.leaf.config.SplatageConfigModules;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public final class HostileSpawnChance extends SplatageConfigModules {

    private static final String BASE_PATH = "spawning.hostile-spawn-chance";

    public static boolean enabled = true;

    public static int centerX = 0;
    public static int centerZ = 0;

    public static Set<String> enabledWorlds = Collections.emptySet();

    public static double startPercent = -1.0D;
    public static double maxPercent = -1.0D;
    public static int distanceToMax = -1;

    public static boolean chanceEnabled = false;

    @Override
    public void onLoaded() {
        config.addComment(BASE_PATH, """
            Distance-based hostile natural spawn chance tuning.
            Percent values are relative to vanilla hostile natural spawn acceptance where 100.0 means vanilla.
            0.0 blocks hostile natural spawns completely.
            Values above 100.0 behave the same as 100.0 because spawn attempts cannot exceed vanilla acceptance.
            -1 disables spawn chance scaling.
            Distance values are configured in blocks.
            Spawn chance is measured from configurable center-x and center-z block coordinates.
            This only affects hostile natural spawning.
            Hostile spawn chance is opt-in per world using enabled-worlds.
            Empty enabled-worlds means hostile spawn chance scaling is disabled in every world.
            """);

        enabled = config.getBoolean(
            BASE_PATH + ".enabled",
            enabled,
            "Enable distance-based hostile natural spawn chance scaling."
        );

        centerX = config.getInt(
            BASE_PATH + ".center-x",
            centerX,
            "Block X coordinate used as the center of hostile spawn chance scaling."
        );

        centerZ = config.getInt(
            BASE_PATH + ".center-z",
            centerZ,
            "Block Z coordinate used as the center of hostile spawn chance scaling."
        );

        enabledWorlds = parseWorldNameSet(
            config.getList(
                BASE_PATH + ".enabled-worlds",
                List.of(),
                "World names where hostile spawn chance scaling is enabled. Empty means disabled in every world."
            )
        );

        startPercent = config.getDouble(
            BASE_PATH + ".chance.start-percent",
            startPercent,
            "Hostile natural spawn chance percent at the configured center. 100.0 means vanilla, 0.0 blocks hostile natural spawns, -1 disables spawn chance scaling."
        );

        maxPercent = config.getDouble(
            BASE_PATH + ".chance.max-percent",
            maxPercent,
            "Hostile natural spawn chance percent at or beyond distance-to-max. 100.0 means vanilla, 0.0 blocks hostile natural spawns, -1 disables spawn chance scaling."
        );

        distanceToMax = config.getInt(
            BASE_PATH + ".chance.distance-to-max",
            distanceToMax,
            "Distance in blocks from the configured center where hostile spawn chance reaches max-percent. -1 disables spawn chance scaling."
        );

        chanceEnabled = !enabledWorlds.isEmpty()
            && enabled
            && startPercent >= 0.0D
            && maxPercent >= 0.0D
            && distanceToMax > 0;
    }

    private static Set<String> parseWorldNameSet(final List<String> raw) {
        if (raw == null || raw.isEmpty()) {
            return Collections.emptySet();
        }

        final Set<String> values = new LinkedHashSet<>();
        for (final String token : raw) {
            if (token == null) {
                continue;
            }
            final String value = token.trim().toLowerCase(Locale.ROOT);
            if (!value.isEmpty()) {
                values.add(value);
            }
        }

        return Collections.unmodifiableSet(values);
    }
}
