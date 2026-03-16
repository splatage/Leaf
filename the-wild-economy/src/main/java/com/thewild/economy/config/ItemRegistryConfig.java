package com.thewild.economy.config;

import com.thewild.economy.model.ExchangeItemDefinition;

import java.util.Map;

public record ItemRegistryConfig(Map<String, ExchangeItemDefinition> items) {
}
