package com.mongenscave.mctreasure.listener;

import com.mongenscave.mctreasure.McTreasure;
import com.mongenscave.mctreasure.data.MenuController;
import com.mongenscave.mctreasure.data.OpenResult;
import com.mongenscave.mctreasure.gui.models.TreasureInventoryMenu;
import com.mongenscave.mctreasure.manager.TreasureManager;
import com.mongenscave.mctreasure.model.TreasureChest;
import com.mongenscave.mctreasure.processor.MessageProcessor;
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

import java.util.Objects;

public class TreasureListener implements Listener {
    private final TreasureManager treasureManager = TreasureManager.getInstance();
    private final McTreasure plugin = McTreasure.getInstance();

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

        OpenResult result = chest.canPlayerOpen(player);
        if (!result.canOpen()) {
            player.sendMessage(Objects.requireNonNull(result.message()));
            player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 0.5f, 1.0f);

            if (chest.isPushbackEnabled()) treasureManager.applyPushback(player, chest);
            return;
        }

        chest.recordPlayerOpen(player);

        new TreasureInventoryMenu(MenuController.getMenuUtils(player), chest).open();
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

        if (!player.hasPermission("ctreasure.admin.break")) {
            event.setCancelled(true);
            player.sendMessage(MessageProcessor.process(plugin.getLanguage().getString("messages.no-permission-break", "&cYou don't have permission to break treasure chests!")));
            return;
        }

        player.sendMessage(MessageProcessor.process(plugin.getLanguage().getString("messages.confirm-break", "&eBreak the treasure chest using &6/ctreasure delete " + chest.getId() + " &einstead!")));
        event.setCancelled(true);
    }
}
