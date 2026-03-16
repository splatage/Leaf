package com.thewild.economy.model;

import java.time.Instant;
import java.util.UUID;

public record TransactionRecord(
        long transactionId,
        String transactionType,
        UUID actorUuid,
        UUID counterpartyUuid,
        String itemKey,
        Long listingId,
        int quantity,
        double unitPrice,
        double totalAmount,
        String notes,
        Instant createdAt
) {}
