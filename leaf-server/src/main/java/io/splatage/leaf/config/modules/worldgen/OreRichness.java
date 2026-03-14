package io.splatage.leaf.config.modules.worldgen;

import io.splatage.leaf.config.SplatageConfigModules;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public final class OreRichness extends SplatageConfigModules {

    private static final String BASE_PATH = "worldgen.ore-richness";

    public static boolean enabled = true;

    public static int centerX = 0;
    public static int centerZ = 0;

    public static double frequencyStartPercent = -1.0D;
    public static double frequencyMaxPercent = -1.0D;
    public static int frequencyDistanceToMax = -1;

    public static Set<String> blacklist = Collections.emptySet();

    public static boolean frequencyEnabled = false;
    public static double centerChunkX = 0.0D;
    public static double centerChunkZ = 0.0D;
    public static double frequencyDistanceToMaxChunksSq = -1.0D;

    @Override
    public void onLoaded() {
        config.addComment(BASE_PATH, """
            Distance-based ore richness tuning for The Wild.
            Percent values are relative to vanilla generation where 100.0 means vanilla.
            -1 disables frequency scaling. Blacklisted ores remain fully vanilla.
            Distance values are configured in blocks and resolved internally in chunk-space.
            Richness is measured from configurable center-x and center-z block coordinates.
            Blacklist entries match ore output block keys such as minecraft:coal_ore.
            """);

        enabled = config.getBoolean(
            BASE_PATH + ".enabled",
            enabled,
            "Enable The Wild ore richness system."
        );

        centerX = config.getInt(
            BASE_PATH + ".center-x",
            centerX,
            "Block X coordinate used as the center of Wild richness scaling."
        );

        centerZ = config.getInt(
            BASE_PATH + ".center-z",
            centerZ,
            "Block Z coordinate used as the center of Wild richness scaling."
        );

        frequencyStartPercent = config.getDouble(
            BASE_PATH + ".frequency.start-percent",
            frequencyStartPercent,
            "Ore frequency percent at the configured center. 100.0 means vanilla. -1 disables frequency scaling."
        );

        frequencyMaxPercent = config.getDouble(
            BASE_PATH + ".frequency.max-percent",
            frequencyMaxPercent,
            "Ore frequency percent at or beyond distance-to-max. 100.0 means vanilla. -1 disables frequency scaling."
        );

        frequencyDistanceToMax = config.getInt(
            BASE_PATH + ".frequency.distance-to-max",
            frequencyDistanceToMax,
            "Distance in blocks from the configured center where frequency reaches max-percent. -1 disables frequency scaling."
        );

        blacklist = parseBlockKeySet(
            config.getList(
                BASE_PATH + ".blacklist",
                List.of(),
                "Ores excluded from Wild richness scaling and left at vanilla generation."
            )
        );

        frequencyEnabled = enabled
            && frequencyStartPercent >= 0.0D
            && frequencyMaxPercent >= 0.0D
            && frequencyDistanceToMax > 0;

        centerChunkX = centerX / 16.0D;
        centerChunkZ = centerZ / 16.0D;
        frequencyDistanceToMaxChunksSq = frequencyEnabled ? square(frequencyDistanceToMax / 16.0D) : -1.0D;
    }

    private static Set<String> parseBlockKeySet(final List<String> raw) {
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

    private static double square(final double value) {
        return value * value;
    }
}
