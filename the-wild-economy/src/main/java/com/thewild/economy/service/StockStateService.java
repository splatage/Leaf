package com.thewild.economy.service;

import com.thewild.economy.model.ExchangeItemDefinition;
import com.thewild.economy.model.StockState;

public class StockStateService {
    public StockState stateFor(ExchangeItemDefinition definition, long quantity) {
        if (quantity < definition.shortageThreshold()) {
            return StockState.SHORTAGE;
        }
        if (quantity > definition.surplusThreshold()) {
            return StockState.SURPLUS;
        }
        return StockState.HEALTHY;
    }
}
