package com.example.mod.utils.player;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;

public class InventoryUtils {
    public static boolean isFull(Inventory inventory) {
        for (int i = 0; i < inventory.size(); i++) {
            if (inventory.getStack(i).isEmpty()) {
                return false;
            }
        }

        return true;
    }

    public static boolean isFull(PlayerInventory inventory) {
        return inventory.main.stream().noneMatch(ItemStack::isEmpty);
    }
}
