package com.mongenscave.mctreasure.listener;

import com.mongenscave.mctreasure.gui.Menu;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.InventoryHolder;
import org.jetbrains.annotations.NotNull;

public class MenuListener implements Listener {
    @EventHandler
    public void onInventoryClick(final @NotNull InventoryClickEvent event) {
        InventoryHolder holder = event.getInventory().getHolder();
        if (holder instanceof Menu menu) menu.handleMenu(event);
    }

    @EventHandler
    public void onInventoryClose(final @NotNull InventoryCloseEvent event) {
        InventoryHolder holder = event.getInventory().getHolder();
        if (holder instanceof Menu menu) menu.close();
    }
}
