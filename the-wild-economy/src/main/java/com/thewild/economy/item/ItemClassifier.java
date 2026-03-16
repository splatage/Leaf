package com.thewild.economy.item;

import com.thewild.economy.model.ExchangeItemDefinition;

import java.util.Map;
import org.bukkit.inventory.ItemStack;

public class ItemClassifier {
    private final Map<String, ExchangeItemDefinition> exchangeDefinitions;

    public ItemClassifier(Map<String, ExchangeItemDefinition> exchangeDefinitions) {
        this.exchangeDefinitions = exchangeDefinitions;
    }

    public boolean isExchangeEligible(ItemStack stack) {
        return exchangeDefinitions.containsKey(ItemKey.fromItem(stack));
    }
}
