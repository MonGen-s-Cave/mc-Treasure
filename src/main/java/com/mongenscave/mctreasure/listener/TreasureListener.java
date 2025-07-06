package com.mongenscave.mctreasure.listener;

import com.mongenscave.mctreasure.api.TreasureOpenEvent;
import com.mongenscave.mctreasure.data.MenuController;
import com.mongenscave.mctreasure.api.data.OpenResult;
import com.mongenscave.mctreasure.gui.models.TreasureInventoryMenu;
import com.mongenscave.mctreasure.identifiers.keys.MessageKeys;
import com.mongenscave.mctreasure.managers.TreasureManager;
import com.mongenscave.mctreasure.model.TreasureChest;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.jetbrains.annotations.NotNull;

public class TreasureListener implements Listener {
    private final TreasureManager treasureManager = TreasureManager.getInstance();

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onPlayerInteract(final @NotNull PlayerInteractEvent event) {
        Player player = event.getPlayer();
        Block block = event.getClickedBlock();

        if (block == null) return;
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return;

        Location location = block.getLocation();
        TreasureChest chest = treasureManager.getChestAtLocation(location);

        if (chest == null) return;

        event.setCancelled(true);

        if (!chest.getPermission().isEmpty() && !player.hasPermission(chest.getPermission())) {
            player.sendMessage(MessageKeys.NO_PERMISSION.getMessage());
            player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 0.5f, 1.0f);

            if (chest.isPushbackEnabled()) treasureManager.applyPushback(player, chest);
            return;
        }

        OpenResult result = chest.canPlayerOpen(player);

        if (!result.canOpen()) {
            if (result.message() != null) player.sendMessage(result.message());
            player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 0.5f, 1.0f);

            if (chest.isPushbackEnabled()) treasureManager.applyPushback(player, chest);
            return;
        }

        TreasureOpenEvent openEvent = new TreasureOpenEvent(player, chest);
        Bukkit.getPluginManager().callEvent(openEvent);

        if (openEvent.isCancelled()) return;

        TreasureInventoryMenu menu = new TreasureInventoryMenu(MenuController.getMenuUtils(player), chest);
        menu.open();
        player.playSound(player.getLocation(), Sound.BLOCK_CHEST_OPEN, 0.5f, 1.0f);
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onBlockBreak(final @NotNull BlockBreakEvent event) {
        Player player = event.getPlayer();
        Block block = event.getBlock();

        if (block.getType() != Material.CHEST && block.getType() != Material.BARREL) return;

        Location location = block.getLocation();
        TreasureChest chest = treasureManager.getChestAtLocation(location);

        if (chest == null) return;

        event.setCancelled(true);
        player.sendMessage(MessageKeys.NO_PERMISSION.getMessage());
    }
}