package io.splatage.leaf.config.modules.entities;

import io.splatage.leaf.config.SplatageConfigModules;

public final class ConfinedMobThrottleConfig extends SplatageConfigModules {

    private static final String BASE_PATH = "entities.confined-mob-throttle";

    public static int skipDurationTicks = 20;

    @Override
    public void onLoaded() {
        config.addComment(
            BASE_PATH,
            "Throttle heavy mob AI after repeated failed navigation attempts."
        );

        skipDurationTicks = clampMin(
            config.getInt(
                BASE_PATH + ".skip-duration-ticks",
                skipDurationTicks,
                "Number of ticks to skip heavy AI after repeated navigation failure. Set to 0 to disable."
            ),
            0
        );
    }

    private static int clampMin(final int value, final int min) {
        return Math.max(value, min);
    }
}
