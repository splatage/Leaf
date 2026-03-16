package com.thewild.economy;

import com.thewild.economy.command.AdminEconomyCommand;
import com.thewild.economy.command.ExchangeSubcommand;
import com.thewild.economy.command.MarketSubcommand;
import com.thewild.economy.command.ShopCommand;
import com.thewild.economy.config.EconomyConfig;
import com.thewild.economy.config.ExchangeConfig;
import com.thewild.economy.config.MarketplaceConfig;
import com.thewild.economy.economy.EconomyBridge;
import com.thewild.economy.economy.VaultEconomyBridge;
import com.thewild.economy.persistence.DatabaseManager;
import com.thewild.economy.persistence.MigrationManager;
import com.thewild.economy.persistence.dao.ExchangeStockDao;
import com.thewild.economy.persistence.dao.ItemDefinitionDao;
import com.thewild.economy.persistence.dao.MarketplaceListingDao;
import com.thewild.economy.persistence.dao.TransactionDao;
import com.thewild.economy.service.ExchangeService;
import com.thewild.economy.service.ListingExpiryService;
import com.thewild.economy.service.MarketplaceService;
import com.thewild.economy.service.PricingService;
import com.thewild.economy.service.StockStateService;
import com.thewild.economy.service.TransactionService;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

public class TheWildEconomyPlugin extends JavaPlugin {
    private ExchangeService exchangeService;
    private MarketplaceService marketplaceService;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        try {
            bootstrap();
        } catch (Exception exception) {
            getLogger().severe("Failed to enable plugin: " + exception.getMessage());
            getServer().getPluginManager().disablePlugin(this);
        }
    }

    public void reloadPluginConfig() throws Exception {
        reloadConfig();
        bootstrap();
    }

    private void bootstrap() throws Exception {
        EconomyConfig economyConfig = EconomyConfig.from(getConfig());
        ExchangeConfig exchangeConfig = ExchangeConfig.from(getConfig());
        MarketplaceConfig marketplaceConfig = MarketplaceConfig.from(getConfig());

        DatabaseManager databaseManager = new DatabaseManager(economyConfig, getLogger());
        databaseManager.testConnection();
        new MigrationManager(databaseManager).migrate();

        EconomyBridge economyBridge = resolveEconomyBridge();

        ExchangeStockDao exchangeStockDao = new ExchangeStockDao(databaseManager);
        MarketplaceListingDao marketplaceListingDao = new MarketplaceListingDao(databaseManager);
        TransactionService transactionService = new TransactionService(new TransactionDao(databaseManager));

        new ItemDefinitionDao(databaseManager).upsertAll(exchangeConfig.definitions().values());

        exchangeService = new ExchangeService(exchangeConfig, exchangeStockDao, new PricingService(), transactionService, databaseManager, economyBridge);
        marketplaceService = new MarketplaceService(marketplaceConfig, marketplaceListingDao, transactionService, databaseManager, economyBridge);

        getCommand("shop").setExecutor(new ShopCommand(
                new ExchangeSubcommand(exchangeService, new StockStateService()),
                new MarketSubcommand(marketplaceService)));
        getCommand("shopadmin").setExecutor(new AdminEconomyCommand(this, exchangeService));

        new ListingExpiryService(this, marketplaceListingDao).start();
    }

    private EconomyBridge resolveEconomyBridge() {
        RegisteredServiceProvider<Economy> registration = getServer().getServicesManager().getRegistration(Economy.class);
        if (registration == null) {
            throw new IllegalStateException("Vault economy provider not found");
        }
        return new VaultEconomyBridge(registration.getProvider());
    }
}
