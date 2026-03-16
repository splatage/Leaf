package com.thewild.economy.persistence.dao;

import com.thewild.economy.model.ListingStatus;
import com.thewild.economy.model.MarketplaceListing;
import com.thewild.economy.persistence.DatabaseManager;

import java.sql.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class MarketplaceListingDao {
    private final DatabaseManager databaseManager;

    public MarketplaceListingDao(DatabaseManager databaseManager) {
        this.databaseManager = databaseManager;
    }

    public long create(Connection connection, MarketplaceListing listing) throws SQLException {
        try (PreparedStatement ps = connection.prepareStatement("""
                INSERT INTO marketplace_listings(seller_uuid, seller_name, item_data, item_summary_key, price, quantity, status, created_at, expires_at)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
                """, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, listing.sellerUuid().toString());
            ps.setString(2, listing.sellerName());
            ps.setString(3, listing.itemData());
            ps.setString(4, listing.itemSummaryKey());
            ps.setDouble(5, listing.price());
            ps.setInt(6, listing.quantity());
            ps.setString(7, listing.status().name());
            ps.setLong(8, listing.createdAt().toEpochMilli());
            ps.setLong(9, listing.expiresAt().toEpochMilli());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    return keys.getLong(1);
                }
            }
        }
        throw new SQLException("Listing creation did not return generated key");
    }

    public MarketplaceListing findById(long listingId) throws SQLException {
        try (Connection connection = databaseManager.getConnection();
             PreparedStatement ps = connection.prepareStatement("SELECT * FROM marketplace_listings WHERE listing_id = ?")) {
            ps.setLong(1, listingId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return map(rs);
                }
            }
        }
        return null;
    }

    public List<MarketplaceListing> browseActive(String search) throws SQLException {
        String sql = "SELECT * FROM marketplace_listings WHERE status = 'ACTIVE'";
        boolean hasSearch = search != null && !search.isBlank();
        if (hasSearch) {
            sql += " AND item_summary_key LIKE ?";
        }
        sql += " ORDER BY created_at DESC LIMIT 100";

        try (Connection connection = databaseManager.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            if (hasSearch) {
                ps.setString(1, "%" + search.toUpperCase() + "%");
            }
            try (ResultSet rs = ps.executeQuery()) {
                List<MarketplaceListing> listings = new ArrayList<>();
                while (rs.next()) {
                    listings.add(map(rs));
                }
                return listings;
            }
        }
    }

    public int countActiveBySeller(UUID sellerUuid) throws SQLException {
        try (Connection connection = databaseManager.getConnection();
             PreparedStatement ps = connection.prepareStatement("SELECT COUNT(*) FROM marketplace_listings WHERE seller_uuid = ? AND status = 'ACTIVE'")) {
            ps.setString(1, sellerUuid.toString());
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getInt(1) : 0;
            }
        }
    }

    public void markSold(Connection connection, long listingId, UUID buyerUuid) throws SQLException {
        try (PreparedStatement ps = connection.prepareStatement("""
                UPDATE marketplace_listings
                SET status = 'SOLD', sold_at = ?, buyer_uuid = ?
                WHERE listing_id = ? AND status = 'ACTIVE'
                """)) {
            ps.setLong(1, System.currentTimeMillis());
            ps.setString(2, buyerUuid.toString());
            ps.setLong(3, listingId);
            if (ps.executeUpdate() != 1) {
                throw new SQLException("Listing already sold or unavailable");
            }
        }
    }

    public void markWithdrawn(Connection connection, long listingId) throws SQLException {
        try (PreparedStatement ps = connection.prepareStatement("""
                UPDATE marketplace_listings
                SET status = 'WITHDRAWN', withdrawn_at = ?
                WHERE listing_id = ? AND status = 'ACTIVE'
                """)) {
            ps.setLong(1, System.currentTimeMillis());
            ps.setLong(2, listingId);
            if (ps.executeUpdate() != 1) {
                throw new SQLException("Listing not active");
            }
        }
    }

    public int expireListings() throws SQLException {
        try (Connection connection = databaseManager.getConnection();
             PreparedStatement ps = connection.prepareStatement("""
                UPDATE marketplace_listings SET status = 'EXPIRED'
                WHERE status = 'ACTIVE' AND expires_at <= ?
                """)) {
            ps.setLong(1, System.currentTimeMillis());
            return ps.executeUpdate();
        }
    }

    private MarketplaceListing map(ResultSet rs) throws SQLException {
        return new MarketplaceListing(
                rs.getLong("listing_id"),
                UUID.fromString(rs.getString("seller_uuid")),
                rs.getString("seller_name"),
                rs.getString("item_data"),
                rs.getString("item_summary_key"),
                rs.getDouble("price"),
                rs.getInt("quantity"),
                ListingStatus.valueOf(rs.getString("status")),
                Instant.ofEpochMilli(rs.getLong("created_at")),
                Instant.ofEpochMilli(rs.getLong("expires_at")),
                rs.getLong("sold_at") == 0 ? null : Instant.ofEpochMilli(rs.getLong("sold_at")),
                rs.getString("buyer_uuid") == null ? null : UUID.fromString(rs.getString("buyer_uuid")),
                rs.getLong("withdrawn_at") == 0 ? null : Instant.ofEpochMilli(rs.getLong("withdrawn_at"))
        );
    }
}
