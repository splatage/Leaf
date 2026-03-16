package com.thewild.economy.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class ShopCommand implements CommandExecutor {
    private final ExchangeSubcommand exchangeSubcommand;
    private final MarketSubcommand marketSubcommand;

    public ShopCommand(ExchangeSubcommand exchangeSubcommand, MarketSubcommand marketSubcommand) {
        this.exchangeSubcommand = exchangeSubcommand;
        this.marketSubcommand = marketSubcommand;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            sender.sendMessage("Usage: /shop <exchange|market>");
            return true;
        }
        String[] tail = new String[args.length - 1];
        System.arraycopy(args, 1, tail, 0, tail.length);
        if ("exchange".equalsIgnoreCase(args[0])) {
            return exchangeSubcommand.execute(sender, tail);
        }
        if ("market".equalsIgnoreCase(args[0])) {
            return marketSubcommand.execute(sender, tail);
        }
        sender.sendMessage("Usage: /shop <exchange|market>");
        return true;
    }
}
