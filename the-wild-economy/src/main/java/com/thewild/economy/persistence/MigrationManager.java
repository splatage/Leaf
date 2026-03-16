package com.thewild.economy.persistence;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class MigrationManager {
    private final DatabaseManager databaseManager;

    public MigrationManager(DatabaseManager databaseManager) {
        this.databaseManager = databaseManager;
    }

    public void migrate() throws SQLException {
        try (Connection connection = databaseManager.getConnection(); Statement statement = connection.createStatement()) {
            boolean sqlite = connection.getMetaData().getURL().startsWith("jdbc:sqlite");
            String autoKey = sqlite ? "INTEGER PRIMARY KEY AUTOINCREMENT" : "BIGINT PRIMARY KEY AUTO_INCREMENT";
            statement.executeUpdate("""
                    CREATE TABLE IF NOT EXISTS exchange_item_definitions (
                        item_key VARCHAR(64) PRIMARY KEY,
                        material VARCHAR(64) NOT NULL,
                        display_name VARCHAR(128) NOT NULL,
                        buy_price DOUBLE NOT NULL,
                        sell_price DOUBLE NOT NULL,
                        shortage_threshold INT NOT NULL,
                        surplus_threshold INT NOT NULL,
                        enabled BOOLEAN NOT NULL,
                        metadata_policy VARCHAR(32) NOT NULL,
                        created_at BIGINT NOT NULL,
                        updated_at BIGINT NOT NULL
                    )
                    """);
            statement.executeUpdate("""
                    CREATE TABLE IF NOT EXISTS exchange_stock (
                        item_key VARCHAR(64) PRIMARY KEY,
                        quantity BIGINT NOT NULL,
                        updated_at BIGINT NOT NULL
                    )
                    """);
            statement.executeUpdate("""
                    CREATE TABLE IF NOT EXISTS marketplace_listings (
                        listing_id """ + autoKey + """,
                        seller_uuid VARCHAR(36) NOT NULL,
                        seller_name VARCHAR(32) NOT NULL,
                        item_data TEXT NOT NULL,
                        item_summary_key VARCHAR(64) NOT NULL,
                        price DOUBLE NOT NULL,
                        quantity INT NOT NULL,
                        status VARCHAR(16) NOT NULL,
                        created_at BIGINT NOT NULL,
                        expires_at BIGINT NOT NULL,
                        sold_at BIGINT,
                        buyer_uuid VARCHAR(36),
                        withdrawn_at BIGINT
                    )
                    """);
            statement.executeUpdate("""
                    CREATE TABLE IF NOT EXISTS economy_transactions (
                        transaction_id """ + autoKey + """,
                        transaction_type VARCHAR(64) NOT NULL,
                        actor_uuid VARCHAR(36),
                        counterparty_uuid VARCHAR(36),
                        item_key VARCHAR(64),
                        listing_id BIGINT,
                        quantity INT NOT NULL,
                        unit_price DOUBLE NOT NULL,
                        total_amount DOUBLE NOT NULL,
                        notes TEXT,
                        created_at BIGINT NOT NULL
                    )
                    """);
            statement.executeUpdate("""
                    CREATE TABLE IF NOT EXISTS admin_audit_log (
                        audit_id """ + autoKey + """,
                        admin_uuid VARCHAR(36) NOT NULL,
                        action_type VARCHAR(64) NOT NULL,
                        target_type VARCHAR(64) NOT NULL,
                        target_id VARCHAR(128),
                        details TEXT,
                        created_at BIGINT NOT NULL
                    )
                    """);
        }
    }
}
