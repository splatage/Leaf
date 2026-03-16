package com.thewild.economy.config;

import org.bukkit.configuration.file.FileConfiguration;

public record MarketplaceConfig(double listingFee, double salesTaxRate, int maxListingsPerPlayer, int defaultExpiryHours) {
    public static MarketplaceConfig from(FileConfiguration config) {
        return new MarketplaceConfig(
                config.getDouble("marketplace.listing-fee", 10.0),
                config.getDouble("marketplace.sales-tax-rate", 0.05),
                config.getInt("marketplace.max-active-listings-per-player", 25),
                config.getInt("marketplace.default-expiry-hours", 72)
        );
    }
}
