package com.thewild.economy.config;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import com.thewild.economy.model.ExchangeItemDefinition;

import java.util.HashMap;
import java.util.Map;
import org.bukkit.Material;

public record ExchangeConfig(boolean strictMetadata, Map<String, ExchangeItemDefinition> definitions) {
    public static ExchangeConfig from(FileConfiguration config) {
        ConfigurationSection section = config.getConfigurationSection("exchange.items");
        Map<String, ExchangeItemDefinition> items = new HashMap<>();
        if (section != null) {
            for (String key : section.getKeys(false)) {
                ConfigurationSection item = section.getConfigurationSection(key);
                if (item == null) {
                    continue;
                }
                Material material = Material.matchMaterial(key);
                if (material == null) {
                    continue;
                }
                items.put(key, new ExchangeItemDefinition(
                        key,
                        material,
                        item.getString("display-name", key),
                        item.getDouble("buy-price", 0.0),
                        item.getDouble("sell-price", 0.0),
                        item.getInt("shortage-threshold", 1),
                        item.getInt("surplus-threshold", 10_000),
                        item.getBoolean("enabled", true),
                        item.getString("metadata-policy", "STRICT")
                ));
            }
        }
        return new ExchangeConfig(config.getBoolean("exchange.strict-metadata", true), items);
    }
}
