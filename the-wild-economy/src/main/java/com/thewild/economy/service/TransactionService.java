package com.thewild.economy.service;

import com.thewild.economy.model.TransactionRecord;
import com.thewild.economy.persistence.dao.TransactionDao;

import java.sql.Connection;
import java.sql.SQLException;

public class TransactionService {
    private final TransactionDao transactionDao;

    public TransactionService(TransactionDao transactionDao) {
        this.transactionDao = transactionDao;
    }

    public void record(Connection connection, TransactionRecord record) throws SQLException {
        transactionDao.create(connection, record);
    }
}
