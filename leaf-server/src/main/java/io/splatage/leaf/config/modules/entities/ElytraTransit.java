package io.splatage.leaf.config.modules.entities;

import ca.spottedleaf.moonrise.common.util.MoonriseConstants;
import io.splatage.leaf.config.SplatageConfigModules;

public final class ElytraTransit extends SplatageConfigModules {

    private static final String BASE_PATH = "entities.elytra-transit";

    public static boolean enabled = true;

    public static int transitThresholdY = 319;
    public static int reentryGenerationBufferBlocks = 16;
    public static int transitBubbleRadiusChunks = 2;
    public static boolean transitNoElytraWear = true;

    public static int glideBubbleRadiusChunks = 6;
    public static int glideCorridorHalfWidthChunks = 3;
    public static int glideCorridorDepthChunks = 6;

    public static int transitTickViewDistance = 2;
    public static int transitLoadViewDistance = 3;
    public static int transitSendViewDistance = 2;

    public static int glideTickViewDistance = 5;
    public static int glideLoadViewDistance = 6;
    public static int glideSendViewDistance = 5;

    @Override
    public void onLoaded() {
        this.config.createTitledSection("Elytra glide controller", BASE_PATH);

        enabled = this.config.getBoolean(BASE_PATH + ".enabled", true);
        transitThresholdY = this.config.getInt(BASE_PATH + ".transit-threshold-y", 319);
        reentryGenerationBufferBlocks = Math.max(0, this.config.getInt(BASE_PATH + ".reentry-generation-buffer-blocks", 64));
        transitBubbleRadiusChunks = clampBubbleRadiusChunks(this.config.getInt(BASE_PATH + ".transit-bubble-radius-chunks", 2));
        transitNoElytraWear = this.config.getBoolean(BASE_PATH + ".transit-no-elytra-wear", true);
        glideBubbleRadiusChunks = clampBubbleRadiusChunks(this.config.getInt(BASE_PATH + ".glide-bubble-radius-chunks", 4));

        glideCorridorHalfWidthChunks = Math.max(0, glideBubbleRadiusChunks / 2);
        glideCorridorDepthChunks = glideBubbleRadiusChunks;

        transitTickViewDistance = transitBubbleRadiusChunks;
        transitSendViewDistance = transitBubbleRadiusChunks;
        transitLoadViewDistance = transitBubbleRadiusChunks + 1;

        glideTickViewDistance = glideBubbleRadiusChunks;
        glideSendViewDistance = glideBubbleRadiusChunks;
        glideLoadViewDistance = glideBubbleRadiusChunks + 1;
    }

    private static int clampBubbleRadiusChunks(final int radius) {
        return Math.max(2, Math.min(radius, MoonriseConstants.MAX_VIEW_DISTANCE));
    }
}
