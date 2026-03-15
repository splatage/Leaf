package io.splatage.leaf.config.modules.blocks;

import io.splatage.leaf.config.SplatageConfigModules;

public final class TrialVaultResetConfig extends SplatageConfigModules {

    private static final String BASE_PATH = "blocks.trial-vault-reset";

    public static boolean enabled = false;
    public static long resetAfterTicks = 0L;

    @Override
    public void onLoaded() {
        config.addComment(
            BASE_PATH,
            "Trial vault rewarded-player reset tuning."
        );

        enabled = config.getBoolean(
            BASE_PATH + ".enabled",
            enabled,
            "Allow trial vaults to forget rewarded players after a cooldown."
        );

        resetAfterTicks = clampMin(
            config.getLong(
                BASE_PATH + ".reset-after-ticks",
                resetAfterTicks,
                "How long a trial vault remembers rewarded players before forgetting them. 20 ticks = 1 second, 1200 ticks = 1 minute. 0 disables."
            ),
            0L
        );
    }

    private static long clampMin(final long value, final long min) {
        return Math.max(value, min);
    }
}
