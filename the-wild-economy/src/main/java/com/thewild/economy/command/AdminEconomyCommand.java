package com.thewild.economy.command;

import com.thewild.economy.TheWildEconomyPlugin;
import com.thewild.economy.service.ExchangeService;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class AdminEconomyCommand implements CommandExecutor {
    private final TheWildEconomyPlugin plugin;
    private final ExchangeService exchangeService;

    public AdminEconomyCommand(TheWildEconomyPlugin plugin, ExchangeService exchangeService) {
        this.plugin = plugin;
        this.exchangeService = exchangeService;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            sender.sendMessage("Usage: /shopadmin <reload|exchange>");
            return true;
        }
        try {
            if ("reload".equalsIgnoreCase(args[0])) {
                plugin.reloadPluginConfig();
                sender.sendMessage("Reloaded economy config.");
                return true;
            }
            if ("exchange".equalsIgnoreCase(args[0]) && args.length >= 3 && "stock".equalsIgnoreCase(args[1])) {
                sender.sendMessage("Stock for " + args[2].toUpperCase() + ": " + exchangeService.stock(args[2].toUpperCase()));
                return true;
            }
        } catch (Exception exception) {
            sender.sendMessage("Admin command failed: " + exception.getMessage());
        }
        sender.sendMessage("Usage: /shopadmin reload | /shopadmin exchange stock <item>");
        return true;
    }
}
