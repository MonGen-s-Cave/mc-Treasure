package net.netherpulse.nptreasure.listener;

import net.netherpulse.nptreasure.data.MenuController;
import net.netherpulse.nptreasure.gui.models.main.TreasureEditMenu;
import net.netherpulse.nptreasure.manager.TreasureManager;
import net.netherpulse.nptreasure.model.TreasureChest;
import net.netherpulse.nptreasure.processor.MessageProcessor;
import net.netherpulse.nptreasure.sessions.LocationSession;
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
                player.sendMessage(MessageProcessor.process("&aLocation set to the block's position!"));
                player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 0.5f, 1.0f);
                LocationSession.exitSettingLocation(player);
                new TreasureEditMenu(MenuController.getMenuUtils(player), chest).open();
            }
        }
    }
}
