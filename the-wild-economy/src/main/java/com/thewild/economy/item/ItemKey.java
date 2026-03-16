package com.thewild.economy.item;

import org.bukkit.inventory.ItemStack;

public final class ItemKey {
    private ItemKey() {}

    public static String fromItem(ItemStack stack) {
        return stack.getType().name();
    }
}
