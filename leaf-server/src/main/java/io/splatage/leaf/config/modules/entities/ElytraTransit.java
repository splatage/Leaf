package io.splatage.leaf.config.modules.entities;

import io.splatage.leaf.config.SplatageConfigModules;

public final class ElytraTransit extends SplatageConfigModules {

    private static final String BASE_PATH = "entities.elytra-transit";

    public static boolean enabled = false;

    public static int transitThresholdY = 319;

    public static int transitSendViewDistance = 2;
    public static int transitLoadViewDistance = 3;
    public static int transitTickViewDistance = 2;

    @Override
    public void onLoaded() {
        config.addComment(
            BASE_PATH,
            "Reduces chunk send/load/tick distances and suppresses new chunk generation while a player is in elytra transit mode.\n"
                + "Transit mode is determined by runtime logic and typically applies while fall-flying above the configured Y threshold."
        );

        enabled = config.getBoolean(
            BASE_PATH + ".enabled",
            enabled,
            "Enable elytra transit optimizations."
        );

        config.addComment(
            BASE_PATH + ".transit-threshold-y",
            "Players at or above this Y level may enter transit mode while fall-flying.\n"
                + "Default: 319."
        );

        transitThresholdY = config.getInt(
            BASE_PATH + ".transit-threshold-y",
            transitThresholdY,
            "Y level threshold for transit mode."
        );

        config.addComment(
            BASE_PATH + ".transit-send-view-distance",
            "Client chunk send distance used during transit mode.\n"
                + "Lower values reduce chunk send bandwidth and client-facing chunk churn."
        );

        transitSendViewDistance = config.getInt(
            BASE_PATH + ".transit-send-view-distance",
            transitSendViewDistance,
            "Chunk send view distance while in transit mode."
        );

        config.addComment(
            BASE_PATH + ".transit-load-view-distance",
            "Server chunk load distance used during transit mode.\n"
                + "This should be at least transit-tick-view-distance + 1."
        );

        transitLoadViewDistance = config.getInt(
            BASE_PATH + ".transit-load-view-distance",
            transitLoadViewDistance,
            "Chunk load view distance while in transit mode."
        );

        config.addComment(
            BASE_PATH + ".transit-tick-view-distance",
            "Server chunk ticking distance used during transit mode.\n"
                + "Lower values reduce entity/block ticking work around fast-travelling elytra players."
        );

        transitTickViewDistance = config.getInt(
            BASE_PATH + ".transit-tick-view-distance",
            transitTickViewDistance,
            "Chunk tick view distance while in transit mode."
        );
    }
}
