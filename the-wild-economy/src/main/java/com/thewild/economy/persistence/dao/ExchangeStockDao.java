package com.thewild.economy.persistence.dao;

import com.thewild.economy.persistence.DatabaseManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ExchangeStockDao {
    private final DatabaseManager databaseManager;

    public ExchangeStockDao(DatabaseManager databaseManager) {
        this.databaseManager = databaseManager;
    }

    public long getQuantity(String itemKey) throws SQLException {
        try (Connection connection = databaseManager.getConnection();
             PreparedStatement ps = connection.prepareStatement("SELECT quantity FROM exchange_stock WHERE item_key = ?")) {
            ps.setString(1, itemKey);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getLong("quantity");
                }
            }
        }
        return 0L;
    }

    public void adjustStock(Connection connection, String itemKey, long delta) throws SQLException {
        long next = Math.max(0, getCurrent(connection, itemKey) + delta);
        String upsert = isSqlite(connection)
                ? "INSERT INTO exchange_stock(item_key, quantity, updated_at) VALUES (?, ?, ?) ON CONFLICT(item_key) DO UPDATE SET quantity = excluded.quantity, updated_at = excluded.updated_at"
                : "INSERT INTO exchange_stock(item_key, quantity, updated_at) VALUES (?, ?, ?) ON DUPLICATE KEY UPDATE quantity = VALUES(quantity), updated_at = VALUES(updated_at)";
        try (PreparedStatement ps = connection.prepareStatement(upsert)) {
            ps.setString(1, itemKey);
            ps.setLong(2, next);
            ps.setLong(3, System.currentTimeMillis());
            ps.executeUpdate();
        }
    }

    private long getCurrent(Connection connection, String itemKey) throws SQLException {
        try (PreparedStatement get = connection.prepareStatement("SELECT quantity FROM exchange_stock WHERE item_key = ?")) {
            get.setString(1, itemKey);
            try (ResultSet rs = get.executeQuery()) {
                return rs.next() ? rs.getLong("quantity") : 0;
            }
        }
    }

    private boolean isSqlite(Connection connection) throws SQLException {
        return connection.getMetaData().getURL().startsWith("jdbc:sqlite");
    }
}
