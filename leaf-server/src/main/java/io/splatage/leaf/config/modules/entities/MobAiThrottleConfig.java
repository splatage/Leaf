package io.splatage.leaf.config.modules.entities;

import io.splatage.leaf.config.SplatageConfigModules;

public final class MobAiThrottleConfig extends SplatageConfigModules {

    private static final String BASE_PATH = "entities";

    // Preserved existing failed-navigation throttle setting
    public static int skipDurationTicks = 20;

    // Layer 1 companion settings
    public static int fullAiStepInterval = 2;
    public static int goalAiUpdateInterval = 1;

    @Override
    public void onLoaded() {
        config.addComment(
            BASE_PATH,
            "Mob AI throttle settings."
        );

        skipDurationTicks = config.getInt(
            BASE_PATH + ".confined-mob-ai-throttle",
            skipDurationTicks,
            "Sleep expensive AI when pathfinding fails. 0 or negative disables."
        );

        fullAiStepInterval = clampMin(
            config.getInt(
                BASE_PATH + ".full-ai-step-interval",
                fullAiStepInterval,
                "How often mobs run the full selector AI in Mob.serverAiStep(). 2 matches the current default cadence."
            ),
            1
        );

        goalAiUpdateInterval = clampMin(
            config.getInt(
                BASE_PATH + ".goal-ai-update-interval",
                goalAiUpdateInterval,
                "How often GoalSelector runs its expensive goalUpdate phase. 1 matches the current default."
            ),
            1
        );
    }

    private static int clampMin(final int value, final int min) {
        return Math.max(value, min);
    }
}
