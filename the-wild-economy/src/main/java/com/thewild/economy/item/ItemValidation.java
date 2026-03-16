package com.thewild.economy.item;

import com.thewild.economy.model.ExchangeItemDefinition;
import org.bukkit.inventory.ItemStack;

public final class ItemValidation {
    private ItemValidation() {}

    public static boolean isStrictExchangeMatch(ItemStack stack, ExchangeItemDefinition definition, boolean strictMetadata) {
        if (stack.getType() != definition.material()) {
            return false;
        }
        if (!strictMetadata) {
            return true;
        }
        if (!stack.hasItemMeta()) {
            return true;
        }
        var meta = stack.getItemMeta();
        return !meta.hasDisplayName() && !meta.hasEnchants() && !meta.hasLore();
    }
}
