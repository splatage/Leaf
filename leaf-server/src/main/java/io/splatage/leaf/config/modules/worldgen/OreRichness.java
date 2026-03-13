package io.splatage.leaf.config.modules.worldgen;

import io.splatage.leaf.config.SplatageConfigModules;
import io.splatage.leaf.util.WildScaling;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public final class OreRichness extends SplatageConfigModules {

    private static final String BASE_PATH = "worldgen.ore-richness";

    public static boolean enabled = false;

    public static double maxChanceMultiplier = 1.0D;
    public static int chanceDistanceToMax = 10000;
    public static WildScaling.ScalingCurve chanceCurve = WildScaling.ScalingCurve.SMOOTHSTEP;

    public static double maxVeinSizeMultiplier = 1.0D;
    public static int veinSizeDistanceToMax = 10000;
    public static WildScaling.ScalingCurve veinSizeCurve = WildScaling.ScalingCurve.SMOOTHSTEP;

    public static int maxGeneratedVeinSize = 64;

    public static final Set<String> includedTargetBlocks = new LinkedHashSet<>();
    public static final Set<String> excludedTargetBlocks = new LinkedHashSet<>();

    @Override
    public void onLoaded() {
        config.addComment(
            BASE_PATH,
            "Distance-based ore richness for The Wild. Chance and vein-size scaling are controlled independently and are both anchored to world spawn."
        );

        enabled = config.getBoolean(
            BASE_PATH + ".enabled",
            enabled,
            "Enable distance-based ore richness scaling."
        );

        maxChanceMultiplier = Math.max(
            1.0D,
            config.getDouble(
                BASE_PATH + ".chance.max-multiplier",
                maxChanceMultiplier,
                "Maximum placement-pass multiplier for participating ore features. 1.0 keeps vanilla behavior."
            )
        );
        chanceDistanceToMax = Math.max(
            1,
            config.getInt(
                BASE_PATH + ".chance.distance-to-max",
                chanceDistanceToMax,
                "Horizontal distance from world spawn where chance scaling reaches its configured maximum."
            )
        );
        chanceCurve = WildScaling.ScalingCurve.fromConfig(
            config.getString(
                BASE_PATH + ".chance.curve",
                chanceCurve.configKey(),
                "Curve for chance scaling. Supported: linear, smoothstep, ease_in_quad, ease_out_quad."
            ),
            chanceCurve
        );

        maxVeinSizeMultiplier = Math.max(
            1.0D,
            config.getDouble(
                BASE_PATH + ".vein-size.max-multiplier",
                maxVeinSizeMultiplier,
                "Maximum vein-size multiplier for participating ore features. 1.0 keeps vanilla behavior."
            )
        );
        veinSizeDistanceToMax = Math.max(
            1,
            config.getInt(
                BASE_PATH + ".vein-size.distance-to-max",
                veinSizeDistanceToMax,
                "Horizontal distance from world spawn where vein-size scaling reaches its configured maximum."
            )
        );
        veinSizeCurve = WildScaling.ScalingCurve.fromConfig(
            config.getString(
                BASE_PATH + ".vein-size.curve",
                veinSizeCurve.configKey(),
                "Curve for vein-size scaling. Supported: linear, smoothstep, ease_in_quad, ease_out_quad."
            ),
            veinSizeCurve
        );

        maxGeneratedVeinSize = Math.max(
            1,
            config.getInt(
                BASE_PATH + ".max-generated-vein-size",
                maxGeneratedVeinSize,
                "Hard safety cap applied after vein-size scaling."
            )
        );

        includedTargetBlocks.clear();
        includedTargetBlocks.addAll(normalize(
            config.getList(
                BASE_PATH + ".included-target-blocks",
                List.of(),
                "Optional allow-list of target output blocks. Leave empty to allow all target blocks except excluded ones. Example: [minecraft:diamond_ore, minecraft:deepslate_diamond_ore]"
            )
        ));

        excludedTargetBlocks.clear();
        excludedTargetBlocks.addAll(normalize(
            config.getList(
                BASE_PATH + ".excluded-target-blocks",
                List.of(),
                "Optional block exclusions matched against OreConfiguration target output blocks. Example: [minecraft:coal_ore, minecraft:deepslate_coal_ore]"
            )
        ));
    }

    private static Set<String> normalize(final List<String> values) {
        final Set<String> normalized = new LinkedHashSet<>();
        for (final String value : values) {
            if (value == null) {
                continue;
            }
            final String trimmed = value.trim();
            if (!trimmed.isEmpty()) {
                normalized.add(trimmed.toLowerCase(Locale.ROOT));
            }
        }
        return normalized;
    }
}
