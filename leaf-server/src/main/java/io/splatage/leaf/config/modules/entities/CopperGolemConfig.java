package io.splatage.leaf.config.modules.entities;

import io.splatage.leaf.config.SplatageConfigModules;

public final class CopperGolemConfig extends SplatageConfigModules {

    private static final String BASE_PATH = "entities.copper-golem";

    public static boolean enabled = true;

    public static int maxItemsHeld = 64;
    public static int pickupCooldownTicks = 20;
    public static int putdownCooldownTicks = 10;
    public static int retryDelayTicks = 40;

    public static int horizontalSearchDistance = 32;
    public static int verticalSearchDistance = 8;
    public static int decisionIntervalTicks = 10;
    public static int rememberFailedTargetsTicks = 200;

    public static boolean rememberLastItem = true;
    public static boolean rememberDestination = true;
    public static int memoryTtlTicks = 2400;
    public static int destinationLockTicks = 600;

    public static SelectionStrategy selectionStrategy = SelectionStrategy.LAST_ITEM_THEN_BIGGEST_STACK;

    public enum SelectionStrategy {
        LAST_ITEM_THEN_BIGGEST_STACK,
        BIGGEST_STACK_ONLY
    }

    @Override
    public void onLoaded() {
        config.addComment(
            BASE_PATH,
            "Copper golem item transport tuning."
        );

        enabled = config.getBoolean(
            BASE_PATH + ".enabled",
            enabled,
            "Enable Splatage copper golem transport tuning."
        );

        maxItemsHeld = clamp(
            config.getInt(
                BASE_PATH + ".pickup.max-items-held",
                maxItemsHeld,
                "Maximum number of items a copper golem may hold per pickup."
            ),
            1,
            64
        );

        pickupCooldownTicks = clampMin(
            config.getInt(
                BASE_PATH + ".pickup.pickup-cooldown-ticks",
                pickupCooldownTicks,
                "Cooldown after a successful pickup."
            ),
            0
        );

        putdownCooldownTicks = clampMin(
            config.getInt(
                BASE_PATH + ".pickup.putdown-cooldown-ticks",
                putdownCooldownTicks,
                "Cooldown after a successful putdown."
            ),
            0
        );

        retryDelayTicks = clampMin(
            config.getInt(
                BASE_PATH + ".pickup.retry-delay-ticks",
                retryDelayTicks,
                "Cooldown after no usable target is found."
            ),
            0
        );

        horizontalSearchDistance = clampMin(
            config.getInt(
                BASE_PATH + ".search.horizontal-radius",
                horizontalSearchDistance,
                "Horizontal target search radius."
            ),
            0
        );

        verticalSearchDistance = clampMin(
            config.getInt(
                BASE_PATH + ".search.vertical-radius",
                verticalSearchDistance,
                "Vertical target search radius."
            ),
            0
        );

        decisionIntervalTicks = clampMin(
            config.getInt(
                BASE_PATH + ".search.decision-interval-ticks",
                decisionIntervalTicks,
                "Only attempt to acquire a new target on this interval."
            ),
            0
        );

        rememberFailedTargetsTicks = clampMin(
            config.getInt(
                BASE_PATH + ".search.remember-failed-targets-ticks",
                rememberFailedTargetsTicks,
                "How long failed targets are remembered."
            ),
            0
        );

        rememberLastItem = config.getBoolean(
            BASE_PATH + ".intelligence.remember-last-item",
            rememberLastItem,
            "Prefer continuing the last successful item type."
        );

        rememberDestination = config.getBoolean(
            BASE_PATH + ".intelligence.remember-destination",
            rememberDestination,
            "Remember the last successful destination chest for fast-path selection."
        );

        memoryTtlTicks = clampMin(
            config.getInt(
                BASE_PATH + ".intelligence.memory-ttl-ticks",
                memoryTtlTicks,
                "TTL for remembered last item."
            ),
            0
        );

        destinationLockTicks = clampMin(
            config.getInt(
                BASE_PATH + ".intelligence.destination-lock-ticks",
                destinationLockTicks,
                "TTL for remembered destination lock."
            ),
            0
        );

        final String strategyName = config.getString(
            BASE_PATH + ".prioritization.selection-strategy",
            selectionStrategy.name(),
            "Supported values: LAST_ITEM_THEN_BIGGEST_STACK, BIGGEST_STACK_ONLY"
        );

        try {
            selectionStrategy = SelectionStrategy.valueOf(strategyName.trim().toUpperCase(java.util.Locale.ROOT));
        } catch (IllegalArgumentException ex) {
            selectionStrategy = SelectionStrategy.LAST_ITEM_THEN_BIGGEST_STACK;
        }
    }

    private static int clampMin(final int value, final int min) {
        return Math.max(value, min);
    }

    private static int clamp(final int value, final int min, final int max) {
        return Math.min(Math.max(value, min), max);
    }
}
