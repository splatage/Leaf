package io.splatage.leaf.config.modules.spawning;

import io.splatage.leaf.config.SplatageConfigModules;

public final class PickupPersistenceExpiry extends SplatageConfigModules {

    private static final String BASE_PATH = "spawning.pickup-persistence-expiry";

    public static boolean enabled = false;
    public static boolean monstersOnly = true;
    public static boolean naturalAndChunkGenerationOnly = true;
    public static int ticks = 20 * 60 * 15;

    @Override
    public void onLoaded() {
        config.addComment(
            BASE_PATH,
            "Temporarily grant persistence to eligible mobs that pick up items, then allow natural despawn again after the configured number of ticks."
        );

        enabled = config.getBoolean(
            BASE_PATH + ".enabled",
            enabled,
            "Enable temporary pickup persistence expiry."
        );

        monstersOnly = config.getBoolean(
            BASE_PATH + ".monsters-only",
            monstersOnly,
            "Only apply to monster-category mobs."
        );

        naturalAndChunkGenerationOnly = config.getBoolean(
            BASE_PATH + ".natural-and-chunk-generation-only",
            naturalAndChunkGenerationOnly,
            "Only apply to mobs spawned naturally or from chunk generation."
        );

        ticks = Math.max(
            0,
            config.getInt(
                BASE_PATH + ".ticks",
                ticks,
                "How long temporary pickup persistence lasts, in ticks."
            )
        );
    }
}
