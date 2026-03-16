package com.thewild.economy.model;

import org.bukkit.Material;

public record ExchangeItemDefinition(
        String itemKey,
        Material material,
        String displayName,
        double buyPrice,
        double sellPrice,
        int shortageThreshold,
        int surplusThreshold,
        boolean enabled,
        String metadataPolicy
) {}
