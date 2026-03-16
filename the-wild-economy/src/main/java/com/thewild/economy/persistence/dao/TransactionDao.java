package com.thewild.economy.persistence.dao;

import com.thewild.economy.model.TransactionRecord;
import com.thewild.economy.persistence.DatabaseManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class TransactionDao {
    private final DatabaseManager databaseManager;

    public TransactionDao(DatabaseManager databaseManager) {
        this.databaseManager = databaseManager;
    }

    public void create(Connection connection, TransactionRecord record) throws SQLException {
        try (PreparedStatement ps = connection.prepareStatement("""
                INSERT INTO economy_transactions(transaction_type, actor_uuid, counterparty_uuid, item_key, listing_id, quantity, unit_price, total_amount, notes, created_at)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """)) {
            ps.setString(1, record.transactionType());
            ps.setString(2, record.actorUuid() == null ? null : record.actorUuid().toString());
            ps.setString(3, record.counterpartyUuid() == null ? null : record.counterpartyUuid().toString());
            ps.setString(4, record.itemKey());
            ps.setObject(5, record.listingId());
            ps.setInt(6, record.quantity());
            ps.setDouble(7, record.unitPrice());
            ps.setDouble(8, record.totalAmount());
            ps.setString(9, record.notes());
            ps.setLong(10, record.createdAt().toEpochMilli());
            ps.executeUpdate();
        }
    }
}
