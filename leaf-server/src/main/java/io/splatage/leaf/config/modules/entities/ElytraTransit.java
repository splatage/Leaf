package io.splatage.leaf.config.modules.entities;

import ca.spottedleaf.moonrise.common.util.MoonriseConstants;
import io.splatage.leaf.config.SplatageConfigModules;

public final class ElytraTransit extends SplatageConfigModules {

    private static final String BASE_PATH = "entities.elytra-transit";

    public static boolean enabled = true;

    public static int transitThresholdY = 319;
    public static int reentryGenerationBufferBlocks = 32;
    public static int transitBubbleRadiusChunks = 2;

    public static int transitTickViewDistance = 2;
    public static int transitLoadViewDistance = 3;
    public static int transitSendViewDistance = 2;
    public static int transitGenerationResumeY = 351;

    @Override
    public void onLoaded() {
        this.config.createTitledSection("Elytra transit", BASE_PATH);

        this.config.addComment(BASE_PATH, """
            Elytra transit mode.

            While a player is fall-flying above the configured threshold, the server uses a
            reduced chunk bubble around that player to lower elytra transit cost.

            World generation is re-enabled before the reduced bubble expands back to normal.
            This softens re-entry by allowing terrain generation to resume inside the reduced
            transit bubble first, then restoring the normal bubble only once the player drops
            to or below the transit threshold.
            """);

        this.config.addComment(BASE_PATH + ".enabled", """
            Enables the Splatage elytra transit behaviour.
            """);
        enabled = this.config.getBoolean(BASE_PATH + ".enabled", true);

        this.config.addComment(BASE_PATH + ".transit-threshold-y", """
            Y level above which elytra transit mode becomes active.

            While a player is fall-flying above this height, the reduced transit bubble is used.
            Once the player drops to or below this height, the normal chunk bubble is restored.
            """);
        transitThresholdY = this.config.getInt(BASE_PATH + ".transit-threshold-y", 319);

        this.config.addComment(BASE_PATH + ".reentry-generation-buffer-blocks", """
            Extra blocks above the transit threshold where world generation is allowed again
            before the reduced transit bubble is removed.

            Example:
            threshold = 319
            buffer = 32

            In that case, world generation resumes at Y=351 and below, but the reduced transit
            bubble stays active until the player reaches Y=319 or below.
            """);
        reentryGenerationBufferBlocks = Math.max(0, this.config.getInt(BASE_PATH + ".reentry-generation-buffer-blocks", 32));

        this.config.addComment(BASE_PATH + ".transit-bubble-radius-chunks", """
            Radius in chunks for the reduced transit bubble.

            This single value drives all transit view distances:
            - send view distance = radius
            - tick view distance = radius
            - load view distance = radius + 1
            """);
        transitBubbleRadiusChunks = clampBubbleRadiusChunks(this.config.getInt(BASE_PATH + ".transit-bubble-radius-chunks", 2));

        transitTickViewDistance = transitBubbleRadiusChunks;
        transitSendViewDistance = transitBubbleRadiusChunks;
        transitLoadViewDistance = transitBubbleRadiusChunks + 1;
        transitGenerationResumeY = transitThresholdY + reentryGenerationBufferBlocks;
    }

    private static int clampBubbleRadiusChunks(final int radius) {
        return Math.max(2, Math.min(radius, MoonriseConstants.MAX_VIEW_DISTANCE));
    }
}

