package com.thewild.economy.model;

import java.time.Instant;
import java.util.UUID;

public record MarketplaceListing(
        long listingId,
        UUID sellerUuid,
        String sellerName,
        String itemData,
        String itemSummaryKey,
        double price,
        int quantity,
        ListingStatus status,
        Instant createdAt,
        Instant expiresAt,
        Instant soldAt,
        UUID buyerUuid,
        Instant withdrawnAt
) {}
