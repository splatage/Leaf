package com.thewild.economy.service;

import com.thewild.economy.model.ExchangeItemDefinition;

public class PricingService {
    public double exchangeSellPayout(ExchangeItemDefinition definition, int quantity) {
        return definition.sellPrice() * quantity;
    }

    public double exchangeBuyCost(ExchangeItemDefinition definition, int quantity) {
        return definition.buyPrice() * quantity;
    }
}
