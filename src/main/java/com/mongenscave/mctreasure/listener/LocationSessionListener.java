package com.mongenscave.mctreasure.listener;

import com.mongenscave.mctreasure.data.MenuController;
import com.mongenscave.mctreasure.gui.models.main.TreasureEditMenu;
import com.mongenscave.mctreasure.identifiers.keys.MessageKeys;
import com.mongenscave.mctreasure.managers.TreasureManager;
import com.mongenscave.mctreasure.model.TreasureChest;
import com.mongenscave.mctreasure.sessions.LocationSession;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.jetbrains.annotations.NotNull;

public class LocationSessionListener implements Listener {
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onBlockBreak(final @NotNull BlockBreakEvent event) {
        Player player = event.getPlayer();
        Block block = event.getBlock();

        if (LocationSession.isSettingLocation(player)) {
            event.setCancelled(true);
            TreasureChest chest = LocationSession.getChestForSettingLocation(player);
            if (chest != null) {
                Location location = block.getLocation();

                chest.setLocation(location);
                chest.setupHologram();
                TreasureManager.getInstance().saveTreasures();
                player.sendMessage(MessageKeys.SESSION_LOCATION_INPUT.getMessage());
                player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 0.5f, 1.0f);
                LocationSession.exitSettingLocation(player);
                new TreasureEditMenu(MenuController.getMenuUtils(player), chest).open();
            }
        }
    }
}
