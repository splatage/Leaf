package com.thewild.economy.persistence;

import com.thewild.economy.config.EconomyConfig;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Logger;

public class DatabaseManager {
    private final EconomyConfig config;
    private final Logger logger;

    public DatabaseManager(EconomyConfig config, Logger logger) {
        this.config = config;
        this.logger = logger;
    }

    public Connection getConnection() throws SQLException {
        if ("mysql".equalsIgnoreCase(config.dbType())) {
            return DriverManager.getConnection(config.jdbcUrl(), config.username(), config.password());
        }
        return DriverManager.getConnection(config.jdbcUrl());
    }

    public void testConnection() throws SQLException {
        try (Connection ignored = getConnection()) {
            logger.info("Connected to economy database: " + config.dbType());
        }
    }
}
