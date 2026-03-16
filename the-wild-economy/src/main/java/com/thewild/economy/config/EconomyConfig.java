package com.thewild.economy.config;

import org.bukkit.configuration.file.FileConfiguration;

public record EconomyConfig(String dbType, String jdbcUrl, String username, String password) {
    public static EconomyConfig from(FileConfiguration config) {
        String type = config.getString("database.type", "sqlite").toLowerCase();
        if ("mysql".equals(type)) {
            String host = config.getString("database.mysql.host", "localhost");
            int port = config.getInt("database.mysql.port", 3306);
            String database = config.getString("database.mysql.database", "thewild");
            String params = config.getString("database.mysql.parameters", "useSSL=false");
            String url = "jdbc:mysql://" + host + ":" + port + "/" + database + "?" + params;
            return new EconomyConfig(type, url,
                    config.getString("database.mysql.username", "root"),
                    config.getString("database.mysql.password", ""));
        }
        String file = config.getString("database.sqlite.file", "plugins/TheWildEconomy/economy.db");
        return new EconomyConfig(type, "jdbc:sqlite:" + file, "", "");
    }
}
