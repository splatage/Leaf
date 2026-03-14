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

    public static double frequencyStartPercent = -1.0D;
    public static double frequencyMaxPercent = -1.0D;
    public static int frequencyDistanceToMax = -1;

    public static double veinSizeStartPercent = -1.0D;
    public static double veinSizeMaxPercent = -1.0D;
    public static int veinSizeDistanceToMax = -1;

    public static Set<String> blacklist = Collections.emptySet();

    public static boolean frequencyEnabled = false;
    public static boolean veinSizeEnabled = false;
    public static double frequencyDistanceToMaxChunks = -1.0D;
    public static double frequencyDistanceToMaxChunksSq = -1.0D;
    public static double veinSizeDistanceToMaxChunks = -1.0D;
    public static double veinSizeDistanceToMaxChunksSq = -1.0D;

    @Override
    public void onLoaded() {
        config.addComment(BASE_PATH, """
            Distance-based ore richness tuning for The Wild.
            Percent values are relative to vanilla generation where 100.0 means vanilla.
            -1 disables a subsystem. Blacklisted ores remain fully vanilla.
            Distance values are configured in blocks and resolved internally in chunk-space.
            Blacklist entries match ore output block keys such as minecraft:coal_ore.
            """);

        enabled = config.getBoolean(
            BASE_PATH + ".enabled",
            enabled,
            "Enable The Wild ore richness system."
        );

        frequencyStartPercent = config.getDouble(
            BASE_PATH + ".frequency.start-percent",
            frequencyStartPercent,
            "Ore frequency percent at spawn. 100.0 means vanilla. -1 disables frequency scaling."
        );

        frequencyMaxPercent = config.getDouble(
            BASE_PATH + ".frequency.max-percent",
            frequencyMaxPercent,
            "Ore frequency percent at or beyond distance-to-max. 100.0 means vanilla. -1 disables frequency scaling."
        );

        frequencyDistanceToMax = config.getInt(
            BASE_PATH + ".frequency.distance-to-max",
            frequencyDistanceToMax,
            "Distance in blocks where frequency reaches max-percent. -1 disables frequency scaling."
        );

        veinSizeStartPercent = config.getDouble(
            BASE_PATH + ".vein-size.start-percent",
            veinSizeStartPercent,
            "Ore vein size percent at spawn. 100.0 means vanilla. -1 disables vein-size scaling."
        );

        veinSizeMaxPercent = config.getDouble(
            BASE_PATH + ".vein-size.max-percent",
            veinSizeMaxPercent,
            "Ore vein size percent at or beyond distance-to-max. 100.0 means vanilla. -1 disables vein-size scaling."
        );

        veinSizeDistanceToMax = config.getInt(
            BASE_PATH + ".vein-size.distance-to-max",
            veinSizeDistanceToMax,
            "Distance in blocks where vein size reaches max-percent. -1 disables vein-size scaling."
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

        veinSizeEnabled = enabled
            && veinSizeStartPercent >= 0.0D
            && veinSizeMaxPercent >= 0.0D
            && veinSizeDistanceToMax > 0;

        frequencyDistanceToMaxChunks = frequencyEnabled ? frequencyDistanceToMax / 16.0D : -1.0D;
        frequencyDistanceToMaxChunksSq = frequencyEnabled ? frequencyDistanceToMaxChunks * frequencyDistanceToMaxChunks : -1.0D;
        veinSizeDistanceToMaxChunks = veinSizeEnabled ? veinSizeDistanceToMax / 16.0D : -1.0D;
        veinSizeDistanceToMaxChunksSq = veinSizeEnabled ? veinSizeDistanceToMaxChunks * veinSizeDistanceToMaxChunks : -1.0D;
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
}
