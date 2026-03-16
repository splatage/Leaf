package com.thewild.economy.economy;

import org.bukkit.OfflinePlayer;

public interface EconomyBridge {
    boolean has(OfflinePlayer player, double amount);

    boolean withdraw(OfflinePlayer player, double amount);

    boolean deposit(OfflinePlayer player, double amount);
}
