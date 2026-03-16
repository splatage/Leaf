package com.thewild.economy.command;

import com.thewild.economy.service.ExchangeService;
import com.thewild.economy.service.StockStateService;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ExchangeSubcommand {
    private final ExchangeService exchangeService;
    private final StockStateService stockStateService;

    public ExchangeSubcommand(ExchangeService exchangeService, StockStateService stockStateService) {
        this.exchangeService = exchangeService;
        this.stockStateService = stockStateService;
    }

    public boolean execute(CommandSender sender, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("Players only.");
            return true;
        }
        try {
            if (args.length == 0 || "browse".equalsIgnoreCase(args[0])) {
                sender.sendMessage("§6Exchange (stock-backed)");
                for (var entry : exchangeService.definitions().entrySet()) {
                    long stock = exchangeService.stock(entry.getKey());
                    var def = entry.getValue();
                    sender.sendMessage("§e" + def.displayName() + " §7stock=" + stock + " §7buy=" + def.buyPrice() + " §7sell=" + def.sellPrice() +
                            " §7state=" + stockStateService.stateFor(def, stock));
                }
                return true;
            }
            if ("sell".equalsIgnoreCase(args[0])) {
                int amount = args.length >= 2 ? Integer.parseInt(args[1]) : player.getInventory().getItemInMainHand().getAmount();
                sender.sendMessage(exchangeService.sellFromHand(player, amount));
                return true;
            }
            if ("buy".equalsIgnoreCase(args[0]) && args.length >= 3) {
                sender.sendMessage(exchangeService.buy(player, args[1].toUpperCase(), Integer.parseInt(args[2])));
                return true;
            }
        } catch (Exception exception) {
            sender.sendMessage("Exchange error: " + exception.getMessage());
        }
        sender.sendMessage("Usage: /shop exchange [browse|sell <amount>|buy <item> <amount>]");
        return true;
    }
}
