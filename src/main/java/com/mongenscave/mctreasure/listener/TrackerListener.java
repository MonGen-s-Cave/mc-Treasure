package com.mongenscave.mctreasure.listener;

import com.mongenscave.mctreasure.McTreasure;
import com.mongenscave.mctreasure.managers.TreasureTracker;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.jetbrains.annotations.NotNull;

public class TrackerListener implements Listener {
    private static final McTreasure plugin = McTreasure.getInstance();
    private static final TreasureTracker tracker = TreasureTracker.getInstance();

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerItemHeld(final @NotNull PlayerItemHeldEvent event) {
        Player player = event.getPlayer();

        plugin.getScheduler().runTaskLater(() -> {
            if (player.isOnline()) tracker.handleItemChange(player);
        }, 1L);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerSwapHandItems(final @NotNull PlayerSwapHandItemsEvent event) {
        if (event.isCancelled()) return;

        Player player = event.getPlayer();

        plugin.getScheduler().runTaskLater(() -> {
            if (player.isOnline()) tracker.handleItemChange(player);
        }, 1L);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onInventoryClick(final @NotNull InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;
        if (event.isCancelled()) return;

        if (event.getSlot() == player.getInventory().getHeldItemSlot()) {
            plugin.getScheduler().runTaskLater(() -> {
                if (player.isOnline()) tracker.handleItemChange(player);
            }, 1L);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onInventoryClose(final @NotNull InventoryCloseEvent event) {
        if (!(event.getPlayer() instanceof Player player)) return;

        plugin.getScheduler().runTaskLater(() -> {
            if (player.isOnline()) tracker.handleItemChange(player);
        }, 1L);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerDropItem(final @NotNull PlayerDropItemEvent event) {
        if (event.isCancelled()) return;

        Player player = event.getPlayer();

        plugin.getScheduler().runTaskLater(() -> {
            if (player.isOnline()) tracker.handleItemChange(player);
        }, 1L);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerJoin(final @NotNull PlayerJoinEvent event) {
        Player player = event.getPlayer();

        plugin.getScheduler().runTaskLater(() -> {
            if (player.isOnline()) tracker.handleItemChange(player);
        }, 1L);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerQuit(final @NotNull PlayerQuitEvent event) {
        Player player = event.getPlayer();
        tracker.stopTracking(player);
    }
}
