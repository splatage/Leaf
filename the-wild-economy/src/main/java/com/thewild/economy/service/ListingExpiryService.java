package com.thewild.economy.service;

import com.thewild.economy.persistence.dao.MarketplaceListingDao;
import org.bukkit.plugin.java.JavaPlugin;

public class ListingExpiryService {
    private final JavaPlugin plugin;
    private final MarketplaceListingDao listingDao;

    public ListingExpiryService(JavaPlugin plugin, MarketplaceListingDao listingDao) {
        this.plugin = plugin;
        this.listingDao = listingDao;
    }

    public void start() {
        plugin.getServer().getScheduler().runTaskTimerAsynchronously(plugin, () -> {
            try {
                int expired = listingDao.expireListings();
                if (expired > 0) {
                    plugin.getLogger().info("Expired " + expired + " marketplace listings.");
                }
            } catch (Exception exception) {
                plugin.getLogger().warning("Failed to expire listings: " + exception.getMessage());
            }
        }, 20L * 60L, 20L * 60L);
    }
}
