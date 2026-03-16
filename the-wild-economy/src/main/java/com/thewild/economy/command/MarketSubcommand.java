package com.thewild.economy.command;

import com.thewild.economy.service.MarketplaceService;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class MarketSubcommand {
    private final MarketplaceService marketplaceService;

    public MarketSubcommand(MarketplaceService marketplaceService) {
        this.marketplaceService = marketplaceService;
    }

    public boolean execute(CommandSender sender, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("Players only.");
            return true;
        }
        try {
            if (args.length == 0 || "browse".equalsIgnoreCase(args[0])) {
                sender.sendMessage("§bMarketplace (player listings)");
                for (var listing : marketplaceService.browse(args.length >= 2 ? args[1] : null)) {
                    sender.sendMessage("§f#" + listing.listingId() + " §7" + listing.itemSummaryKey() + " x" + listing.quantity() +
                            " §7price=" + listing.price() + " §7seller=" + listing.sellerName());
                }
                return true;
            }
            if ("list".equalsIgnoreCase(args[0]) && args.length >= 2) {
                sender.sendMessage(marketplaceService.createListing(player, Double.parseDouble(args[1])));
                return true;
            }
            if ("buy".equalsIgnoreCase(args[0]) && args.length >= 2) {
                sender.sendMessage(marketplaceService.buy(player, Long.parseLong(args[1])));
                return true;
            }
            if ("withdraw".equalsIgnoreCase(args[0]) && args.length >= 2) {
                sender.sendMessage(marketplaceService.withdraw(player, Long.parseLong(args[1])));
                return true;
            }
        } catch (Exception exception) {
            sender.sendMessage("Marketplace error: " + exception.getMessage());
        }
        sender.sendMessage("Usage: /shop market [browse|list <price>|buy <id>|withdraw <id>]");
        return true;
    }
}
