package io.splatage.leaf.config.modules.spawning;

import io.splatage.leaf.config.SplatageConfigModules;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.EntityType;
import org.jspecify.annotations.Nullable;

public final class HostilePackComposition extends SplatageConfigModules {

    private static final String BASE_PATH = "spawning.hostile-pack-composition";

    public static boolean enabled = true;

    public static int centerX = 0;
    public static int centerZ = 0;

    public static double weightStartPercent = -1.0D;
    public static double weightMaxPercent = -1.0D;
    public static int weightDistanceToMax = -1;
    public static boolean weightScalingEnabled = false;

    public static boolean packCompositionEnabled = false;

    public enum HostilePackType {
        ZOMBIE("minecraft:zombie"),
        ZOMBIE_VILLAGER("minecraft:zombie_villager"),
        HUSK("minecraft:husk"),
        DROWNED("minecraft:drowned"),
        SKELETON("minecraft:skeleton"),
        STRAY("minecraft:stray"),
        BOGGED("minecraft:bogged"),
        SPIDER("minecraft:spider"),
        CAVE_SPIDER("minecraft:cave_spider"),
        CREEPER("minecraft:creeper"),
        ENDERMAN("minecraft:enderman"),
        WITCH("minecraft:witch"),
        SLIME("minecraft:slime"),
        PHANTOM("minecraft:phantom"),
        SILVERFISH("minecraft:silverfish"),
        ENDERMITE("minecraft:endermite"),
        BLAZE("minecraft:blaze"),
        MAGMA_CUBE("minecraft:magma_cube"),
        GHAST("minecraft:ghast"),
        WITHER_SKELETON("minecraft:wither_skeleton"),
        ZOMBIFIED_PIGLIN("minecraft:zombified_piglin"),
        PIGLIN("minecraft:piglin"),
        HOGLIN("minecraft:hoglin");

        private final String entityTypeKey;

        HostilePackType(final String entityTypeKey) {
            this.entityTypeKey = entityTypeKey;
        }

        public String entityTypeKey() {
            return this.entityTypeKey;
        }
    }

    @Override
    public void onLoaded() {
        config.addComment(BASE_PATH, """
            Distance-based hostile natural spawn-weight tuning.
            Percent values are relative to vanilla biome/structure hostile spawn data where 100.0 means unchanged.
            -1 disables weight scaling.
            Distance values are configured in blocks.
            Scaling is measured from configurable center-x and center-z block coordinates.
            This only affects natural hostile spawn-list weight selection.
            It does not create any synthetic spawn system and does not modify pack size.
            """);

        enabled = config.getBoolean(
            BASE_PATH + ".enabled",
            enabled,
            "Enable distance-based hostile natural spawn-weight scaling."
        );

        centerX = config.getInt(
            BASE_PATH + ".center-x",
            centerX,
            "Block X coordinate used as the center of hostile spawn-weight scaling."
        );

        centerZ = config.getInt(
            BASE_PATH + ".center-z",
            centerZ,
            "Block Z coordinate used as the center of hostile spawn-weight scaling."
        );

        weightStartPercent = config.getDouble(
            BASE_PATH + ".weight.start-percent",
            weightStartPercent,
            "Relative hostile spawn weight percent at the configured center. 100.0 means unchanged. -1 disables weight scaling."
        );

        weightMaxPercent = config.getDouble(
            BASE_PATH + ".weight.max-percent",
            weightMaxPercent,
            "Relative hostile spawn weight percent at or beyond distance-to-max. 100.0 means unchanged. -1 disables weight scaling."
        );

        weightDistanceToMax = config.getInt(
            BASE_PATH + ".weight.distance-to-max",
            weightDistanceToMax,
            "Distance in blocks from the configured center where hostile spawn weight reaches max-percent. -1 disables weight scaling."
        );

        weightScalingEnabled = enabled
            && weightStartPercent >= 0.0D
            && weightMaxPercent >= 0.0D
            && weightDistanceToMax > 0;

        packCompositionEnabled = weightScalingEnabled;
    }

    public static @Nullable HostilePackType resolveType(final EntityType<?> entityType) {
        final String entityTypeKey = BuiltInRegistries.ENTITY_TYPE.getKey(entityType).toString();
        for (final HostilePackType type : HostilePackType.values()) {
            if (type.entityTypeKey().equals(entityTypeKey)) {
                return type;
            }
        }
        return null;
    }
}
