package io.splatage.leaf.config.modules.entities;

import io.splatage.leaf.config.SplatageConfigModules;

public final class WildStrength extends SplatageConfigModules {

    private static final String BASE_PATH = "entities.wild-strength";

    public static boolean enabled = true;

    public static int centerX = 0;
    public static int centerZ = 0;

    public static double movementSpeedStartPercent = -1.0D;
    public static double movementSpeedMaxPercent = -1.0D;
    public static int movementSpeedDistanceToMax = -1;

    public static double attackDamageStartPercent = -1.0D;
    public static double attackDamageMaxPercent = -1.0D;
    public static int attackDamageDistanceToMax = -1;

    public static double equipmentSpawnChanceStartPercent = -1.0D;
    public static double equipmentSpawnChanceMaxPercent = -1.0D;
    public static int equipmentSpawnChanceDistanceToMax = -1;

    public static double equipmentTierBonusStart = -1.0D;
    public static double equipmentTierBonusMax = -1.0D;
    public static int equipmentTierBonusDistanceToMax = -1;

    public static double equipmentEnchantChanceStartPercent = -1.0D;
    public static double equipmentEnchantChanceMaxPercent = -1.0D;
    public static int equipmentEnchantChanceDistanceToMax = -1;

    public static boolean movementSpeedEnabled = false;
    public static boolean attackDamageEnabled = false;
    public static boolean equipmentSpawnChanceEnabled = false;
    public static boolean equipmentTierBonusEnabled = false;
    public static boolean equipmentEnchantChanceEnabled = false;
    public static boolean wildStrengthEnabled = false;

    @Override
    public void onLoaded() {
        config.addComment(BASE_PATH, """
            Distance-based hostile strength tuning.
            Percent values are relative to vanilla where 100.0 means vanilla.
            -1 disables an individual percent-based curve.
            Distance values are configured in blocks.
            Strength is measured from configurable center-x and center-z block coordinates.
            This only affects hostile mobs when they spawn.
            Equipment tier bonus uses raw vanilla armor material tiers where 0.0 means no extra tier bonus.
            Fractional tier bonus values are resolved probabilistically.
            """);

        enabled = config.getBoolean(
            BASE_PATH + ".enabled",
            enabled,
            "Enable distance-based hostile strength scaling."
        );

        centerX = config.getInt(
            BASE_PATH + ".center-x",
            centerX,
            "Block X coordinate used as the center of hostile strength scaling."
        );

        centerZ = config.getInt(
            BASE_PATH + ".center-z",
            centerZ,
            "Block Z coordinate used as the center of hostile strength scaling."
        );

        movementSpeedStartPercent = config.getDouble(
            BASE_PATH + ".movement-speed.start-percent",
            movementSpeedStartPercent,
            "Hostile movement speed percent at the configured center. 100.0 means vanilla. -1 disables movement speed scaling."
        );

        movementSpeedMaxPercent = config.getDouble(
            BASE_PATH + ".movement-speed.max-percent",
            movementSpeedMaxPercent,
            "Hostile movement speed percent at or beyond distance-to-max. 100.0 means vanilla. -1 disables movement speed scaling."
        );

        movementSpeedDistanceToMax = config.getInt(
            BASE_PATH + ".movement-speed.distance-to-max",
            movementSpeedDistanceToMax,
            "Distance in blocks from the configured center where movement speed reaches max-percent. -1 disables movement speed scaling."
        );

        attackDamageStartPercent = config.getDouble(
            BASE_PATH + ".attack-damage.start-percent",
            attackDamageStartPercent,
            "Hostile attack damage percent at the configured center. 100.0 means vanilla. -1 disables attack damage scaling."
        );

        attackDamageMaxPercent = config.getDouble(
            BASE_PATH + ".attack-damage.max-percent",
            attackDamageMaxPercent,
            "Hostile attack damage percent at or beyond distance-to-max. 100.0 means vanilla. -1 disables attack damage scaling."
        );

        attackDamageDistanceToMax = config.getInt(
            BASE_PATH + ".attack-damage.distance-to-max",
            attackDamageDistanceToMax,
            "Distance in blocks from the configured center where attack damage reaches max-percent. -1 disables attack damage scaling."
        );

        equipmentSpawnChanceStartPercent = config.getDouble(
            BASE_PATH + ".equipment.spawn-chance.start-percent",
            equipmentSpawnChanceStartPercent,
            "Hostile equipment roll chance percent at the configured center. 100.0 means vanilla. -1 disables equipment spawn chance scaling."
        );

        equipmentSpawnChanceMaxPercent = config.getDouble(
            BASE_PATH + ".equipment.spawn-chance.max-percent",
            equipmentSpawnChanceMaxPercent,
            "Hostile equipment roll chance percent at or beyond distance-to-max. 100.0 means vanilla. -1 disables equipment spawn chance scaling."
        );

        equipmentSpawnChanceDistanceToMax = config.getInt(
            BASE_PATH + ".equipment.spawn-chance.distance-to-max",
            equipmentSpawnChanceDistanceToMax,
            "Distance in blocks from the configured center where equipment roll chance reaches max-percent. -1 disables equipment spawn chance scaling."
        );

        equipmentTierBonusStart = config.getDouble(
            BASE_PATH + ".equipment.tier-bonus.start",
            equipmentTierBonusStart,
            "Additional hostile equipment material tier bonus at the configured center. 0.0 means no extra tier. -1 disables equipment tier scaling."
        );

        equipmentTierBonusMax = config.getDouble(
            BASE_PATH + ".equipment.tier-bonus.max",
            equipmentTierBonusMax,
            "Additional hostile equipment material tier bonus at or beyond distance-to-max. 0.0 means no extra tier. -1 disables equipment tier scaling."
        );

        equipmentTierBonusDistanceToMax = config.getInt(
            BASE_PATH + ".equipment.tier-bonus.distance-to-max",
            equipmentTierBonusDistanceToMax,
            "Distance in blocks from the configured center where equipment tier bonus reaches max. -1 disables equipment tier scaling."
        );

        equipmentEnchantChanceStartPercent = config.getDouble(
            BASE_PATH + ".equipment.enchant-chance.start-percent",
            equipmentEnchantChanceStartPercent,
            "Hostile equipment enchant chance percent at the configured center. 100.0 means vanilla. -1 disables equipment enchant chance scaling."
        );

        equipmentEnchantChanceMaxPercent = config.getDouble(
            BASE_PATH + ".equipment.enchant-chance.max-percent",
            equipmentEnchantChanceMaxPercent,
            "Hostile equipment enchant chance percent at or beyond distance-to-max. 100.0 means vanilla. -1 disables equipment enchant chance scaling."
        );

        equipmentEnchantChanceDistanceToMax = config.getInt(
            BASE_PATH + ".equipment.enchant-chance.distance-to-max",
            equipmentEnchantChanceDistanceToMax,
            "Distance in blocks from the configured center where equipment enchant chance reaches max-percent. -1 disables equipment enchant chance scaling."
        );

        movementSpeedEnabled = enabled
            && movementSpeedStartPercent >= 0.0D
            && movementSpeedMaxPercent >= 0.0D
            && movementSpeedDistanceToMax > 0;

        attackDamageEnabled = enabled
            && attackDamageStartPercent >= 0.0D
            && attackDamageMaxPercent >= 0.0D
            && attackDamageDistanceToMax > 0;

        equipmentSpawnChanceEnabled = enabled
            && equipmentSpawnChanceStartPercent >= 0.0D
            && equipmentSpawnChanceMaxPercent >= 0.0D
            && equipmentSpawnChanceDistanceToMax > 0;

        equipmentTierBonusEnabled = enabled
            && equipmentTierBonusStart >= 0.0D
            && equipmentTierBonusMax >= 0.0D
            && equipmentTierBonusDistanceToMax > 0;

        equipmentEnchantChanceEnabled = enabled
            && equipmentEnchantChanceStartPercent >= 0.0D
            && equipmentEnchantChanceMaxPercent >= 0.0D
            && equipmentEnchantChanceDistanceToMax > 0;

        wildStrengthEnabled = movementSpeedEnabled
            || attackDamageEnabled
            || equipmentSpawnChanceEnabled
            || equipmentTierBonusEnabled
            || equipmentEnchantChanceEnabled;
    }
}
