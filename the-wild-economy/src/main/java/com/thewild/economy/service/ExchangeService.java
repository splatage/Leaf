package com.thewild.economy.service;

import com.thewild.economy.config.ExchangeConfig;
import com.thewild.economy.economy.EconomyBridge;
import com.thewild.economy.item.ItemValidation;
import com.thewild.economy.model.ExchangeItemDefinition;
import com.thewild.economy.model.TransactionRecord;
import com.thewild.economy.persistence.DatabaseManager;
import com.thewild.economy.persistence.dao.ExchangeStockDao;

import java.sql.Connection;
import java.time.Instant;
import java.util.Map;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class ExchangeService {
    private final ExchangeConfig exchangeConfig;
    private final ExchangeStockDao exchangeStockDao;
    private final PricingService pricingService;
    private final TransactionService transactionService;
    private final DatabaseManager databaseManager;
    private final EconomyBridge economyBridge;

    public ExchangeService(ExchangeConfig exchangeConfig,
                           ExchangeStockDao exchangeStockDao,
                           PricingService pricingService,
                           TransactionService transactionService,
                           DatabaseManager databaseManager,
                           EconomyBridge economyBridge) {
        this.exchangeConfig = exchangeConfig;
        this.exchangeStockDao = exchangeStockDao;
        this.pricingService = pricingService;
        this.transactionService = transactionService;
        this.databaseManager = databaseManager;
        this.economyBridge = economyBridge;
    }

    public Map<String, ExchangeItemDefinition> definitions() {
        return exchangeConfig.definitions();
    }

    public long stock(String itemKey) throws Exception {
        return exchangeStockDao.getQuantity(itemKey);
    }

    public String sellFromHand(Player player, int amount) throws Exception {
        ItemStack hand = player.getInventory().getItemInMainHand();
        if (hand.getType().isAir()) {
            return "Hold an item in hand.";
        }
        String key = hand.getType().name();
        ExchangeItemDefinition def = exchangeConfig.definitions().get(key);
        if (def == null || !def.enabled()) {
            return "Item is not accepted by the Exchange.";
        }
        if (!ItemValidation.isStrictExchangeMatch(hand, def, exchangeConfig.strictMetadata())) {
            return "Item metadata does not match Exchange policy.";
        }
        int sellAmount = Math.min(amount, hand.getAmount());
        double payout = pricingService.exchangeSellPayout(def, sellAmount);

        try (Connection connection = databaseManager.getConnection()) {
            connection.setAutoCommit(false);
            hand.setAmount(hand.getAmount() - sellAmount);
            exchangeStockDao.adjustStock(connection, key, sellAmount);
            transactionService.record(connection, new TransactionRecord(0, "EXCHANGE_SELL", player.getUniqueId(), null,
                    key, null, sellAmount, def.sellPrice(), payout, "Immediate stock payout", Instant.now()));
            connection.commit();
        }
        if (!economyBridge.deposit(player, payout)) {
            return "Sell succeeded but payout failed; contact admin.";
        }
        return "Sold " + sellAmount + "x " + def.displayName() + " for " + payout;
    }

    public String buy(Player player, String itemKey, int amount) throws Exception {
        ExchangeItemDefinition def = exchangeConfig.definitions().get(itemKey);
        if (def == null || !def.enabled()) {
            return "Item is not available in Exchange.";
        }
        long stock = exchangeStockDao.getQuantity(itemKey);
        if (stock < amount) {
            return "Insufficient stock. Available: " + stock;
        }
        double cost = pricingService.exchangeBuyCost(def, amount);
        if (!economyBridge.has(player, cost)) {
            return "Insufficient funds.";
        }
        if (!economyBridge.withdraw(player, cost)) {
            return "Unable to charge account.";
        }

        try (Connection connection = databaseManager.getConnection()) {
            connection.setAutoCommit(false);
            exchangeStockDao.adjustStock(connection, itemKey, -amount);
            transactionService.record(connection, new TransactionRecord(0, "EXCHANGE_BUY", player.getUniqueId(), null,
                    itemKey, null, amount, def.buyPrice(), cost, "Stock-backed purchase", Instant.now()));
            connection.commit();
        }

        ItemStack stack = new ItemStack(def.material(), amount);
        player.getInventory().addItem(stack).values().forEach(leftover -> player.getWorld().dropItemNaturally(player.getLocation(), leftover));
        return "Purchased " + amount + "x " + def.displayName() + " for " + cost;
    }
}
