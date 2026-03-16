package com.thewild.economy.service;

import com.thewild.economy.config.MarketplaceConfig;
import com.thewild.economy.economy.EconomyBridge;
import com.thewild.economy.item.ItemSerialization;
import com.thewild.economy.model.ListingStatus;
import com.thewild.economy.model.MarketplaceListing;
import com.thewild.economy.model.TransactionRecord;
import com.thewild.economy.persistence.DatabaseManager;
import com.thewild.economy.persistence.dao.MarketplaceListingDao;

import java.sql.Connection;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class MarketplaceService {
    private final MarketplaceConfig config;
    private final MarketplaceListingDao listingDao;
    private final TransactionService transactionService;
    private final DatabaseManager databaseManager;
    private final EconomyBridge economyBridge;

    public MarketplaceService(MarketplaceConfig config, MarketplaceListingDao listingDao, TransactionService transactionService,
                              DatabaseManager databaseManager, EconomyBridge economyBridge) {
        this.config = config;
        this.listingDao = listingDao;
        this.transactionService = transactionService;
        this.databaseManager = databaseManager;
        this.economyBridge = economyBridge;
    }

    public String createListing(Player seller, double price) throws Exception {
        ItemStack hand = seller.getInventory().getItemInMainHand();
        if (hand.getType().isAir() || hand.getAmount() <= 0) {
            return "Hold the item to list in main hand.";
        }
        if (price <= 0) {
            return "Price must be positive.";
        }
        if (listingDao.countActiveBySeller(seller.getUniqueId()) >= config.maxListingsPerPlayer()) {
            return "You reached the active listing cap.";
        }
        if (!economyBridge.has(seller, config.listingFee()) || !economyBridge.withdraw(seller, config.listingFee())) {
            return "Unable to charge listing fee.";
        }

        ItemStack listed = hand.clone();
        seller.getInventory().setItemInMainHand(null);
        MarketplaceListing listing = new MarketplaceListing(
                0,
                seller.getUniqueId(),
                seller.getName(),
                ItemSerialization.serialize(listed),
                listed.getType().name(),
                price,
                listed.getAmount(),
                ListingStatus.ACTIVE,
                Instant.now(),
                Instant.now().plus(config.defaultExpiryHours(), ChronoUnit.HOURS),
                null,
                null,
                null
        );

        try (Connection connection = databaseManager.getConnection()) {
            connection.setAutoCommit(false);
            long listingId = listingDao.create(connection, listing);
            transactionService.record(connection, new TransactionRecord(0, "MARKET_LISTING_CREATE", seller.getUniqueId(), null,
                    listed.getType().name(), listingId, listed.getAmount(), price, config.listingFee(), "Listing fee charged", Instant.now()));
            connection.commit();
            return "Listed item as #" + listingId + " for " + price;
        }
    }

    public List<MarketplaceListing> browse(String search) throws Exception {
        return listingDao.browseActive(search);
    }

    public String buy(Player buyer, long listingId) throws Exception {
        MarketplaceListing listing = listingDao.findById(listingId);
        if (listing == null || listing.status() != ListingStatus.ACTIVE) {
            return "Listing not available.";
        }
        if (!economyBridge.has(buyer, listing.price()) || !economyBridge.withdraw(buyer, listing.price())) {
            return "Unable to charge buyer funds.";
        }
        double sellerPayout = listing.price() * (1.0 - config.salesTaxRate());

        try (Connection connection = databaseManager.getConnection()) {
            connection.setAutoCommit(false);
            listingDao.markSold(connection, listingId, buyer.getUniqueId());
            transactionService.record(connection, new TransactionRecord(0, "MARKET_LISTING_SOLD", buyer.getUniqueId(), listing.sellerUuid(),
                    listing.itemSummaryKey(), listingId, listing.quantity(), listing.price(), listing.price(), "Buyer paid listing", Instant.now()));
            connection.commit();
        }

        ItemStack item = ItemSerialization.deserialize(listing.itemData());
        buyer.getInventory().addItem(item).values().forEach(leftover -> buyer.getWorld().dropItemNaturally(buyer.getLocation(), leftover));

        var sellerOffline = Bukkit.getOfflinePlayer(listing.sellerUuid());
        if (!economyBridge.deposit(sellerOffline, sellerPayout)) {
            return "Purchased, but seller payout failed. Admin attention required.";
        }
        return "Purchased listing #" + listingId + " for " + listing.price();
    }

    public String withdraw(Player seller, long listingId) throws Exception {
        MarketplaceListing listing = listingDao.findById(listingId);
        if (listing == null || listing.status() != ListingStatus.ACTIVE) {
            return "Listing is not active.";
        }
        if (!listing.sellerUuid().equals(seller.getUniqueId())) {
            return "You do not own this listing.";
        }

        try (Connection connection = databaseManager.getConnection()) {
            connection.setAutoCommit(false);
            listingDao.markWithdrawn(connection, listingId);
            transactionService.record(connection, new TransactionRecord(0, "MARKET_LISTING_WITHDRAW", seller.getUniqueId(), null,
                    listing.itemSummaryKey(), listingId, listing.quantity(), listing.price(), 0, "Seller withdrew listing", Instant.now()));
            connection.commit();
        }

        ItemStack item = ItemSerialization.deserialize(listing.itemData());
        seller.getInventory().addItem(item).values().forEach(leftover -> seller.getWorld().dropItemNaturally(seller.getLocation(), leftover));
        return "Withdrawn listing #" + listingId;
    }
}
