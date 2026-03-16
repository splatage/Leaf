package com.thewild.economy.persistence.dao;

import com.thewild.economy.model.ExchangeItemDefinition;
import com.thewild.economy.persistence.DatabaseManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.Instant;
import java.util.Collection;

public class ItemDefinitionDao {
    private final DatabaseManager databaseManager;

    public ItemDefinitionDao(DatabaseManager databaseManager) {
        this.databaseManager = databaseManager;
    }

    public void upsertAll(Collection<ExchangeItemDefinition> definitions) throws SQLException {
        try (Connection connection = databaseManager.getConnection()) {
            String sql = connection.getMetaData().getURL().startsWith("jdbc:sqlite")
                    ? """
                    INSERT INTO exchange_item_definitions(item_key, material, display_name, buy_price, sell_price, shortage_threshold, surplus_threshold, enabled, metadata_policy, created_at, updated_at)
                    VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                    ON CONFLICT(item_key) DO UPDATE SET
                        material = excluded.material,
                        display_name = excluded.display_name,
                        buy_price = excluded.buy_price,
                        sell_price = excluded.sell_price,
                        shortage_threshold = excluded.shortage_threshold,
                        surplus_threshold = excluded.surplus_threshold,
                        enabled = excluded.enabled,
                        metadata_policy = excluded.metadata_policy,
                        updated_at = excluded.updated_at
                    """
                    : """
                    INSERT INTO exchange_item_definitions(item_key, material, display_name, buy_price, sell_price, shortage_threshold, surplus_threshold, enabled, metadata_policy, created_at, updated_at)
                    VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                    ON DUPLICATE KEY UPDATE
                        material = VALUES(material),
                        display_name = VALUES(display_name),
                        buy_price = VALUES(buy_price),
                        sell_price = VALUES(sell_price),
                        shortage_threshold = VALUES(shortage_threshold),
                        surplus_threshold = VALUES(surplus_threshold),
                        enabled = VALUES(enabled),
                        metadata_policy = VALUES(metadata_policy),
                        updated_at = VALUES(updated_at)
                    """;
            for (ExchangeItemDefinition definition : definitions) {
                try (PreparedStatement ps = connection.prepareStatement(sql)) {
                    long now = Instant.now().toEpochMilli();
                    ps.setString(1, definition.itemKey());
                    ps.setString(2, definition.material().name());
                    ps.setString(3, definition.displayName());
                    ps.setDouble(4, definition.buyPrice());
                    ps.setDouble(5, definition.sellPrice());
                    ps.setInt(6, definition.shortageThreshold());
                    ps.setInt(7, definition.surplusThreshold());
                    ps.setBoolean(8, definition.enabled());
                    ps.setString(9, definition.metadataPolicy());
                    ps.setLong(10, now);
                    ps.setLong(11, now);
                    ps.executeUpdate();
                }
            }
        }
    }
}
