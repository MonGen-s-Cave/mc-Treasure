package com.mongenscave.mctreasure.managers;

import com.github.Anon8281.universalScheduler.scheduling.tasks.MyScheduledTask;
import com.mongenscave.mctreasure.McTreasure;
import com.mongenscave.mctreasure.identifiers.keys.ConfigKeys;
import com.mongenscave.mctreasure.model.TreasureChest;
import com.mongenscave.mctreasure.processor.MessageProcessor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@SuppressWarnings("deprecation")
public class TreasureTracker {
    private static final McTreasure plugin = McTreasure.getInstance();
    private static TreasureTracker instance;
    private final ConcurrentHashMap<UUID, MyScheduledTask> activeTrackers = new  ConcurrentHashMap<>();

    public static TreasureTracker getInstance() {
        if (instance == null) instance = new TreasureTracker();
        return instance;
    }

    public void startTracking(@NotNull Player player) {
        if (!ConfigKeys.TRACKER_ENABLED.getBoolean()) return;

        UUID playerId = player.getUniqueId();
        stopTracking(player);

        MyScheduledTask tracker = plugin.getScheduler().runTaskTimer(() -> {
            if (!player.isOnline()) {
                stopTracking(player);
                return;
            }

            ItemStack mainHand = player.getInventory().getItemInMainHand();
            ItemStack offHand = player.getInventory().getItemInOffHand();
            ItemStack trackerItem = ConfigKeys.TRACKER_ITEM.getItem();

            if (trackerItem == null) {
                stopTracking(player);
                return;
            }

            boolean hasTrackerInHand = isSameItem(mainHand, trackerItem) || isSameItem(offHand, trackerItem);

            if (!hasTrackerInHand) {
                stopTracking(player);
                return;
            }

            TreasureChest nearestTreasure = findNearestTreasure(player);

            if (nearestTreasure != null) {
                String message = createTrackerMessage(player, nearestTreasure);
                player.sendActionBar(message);
            }
        }, 0L, 10L);

        activeTrackers.put(playerId, tracker);
    }

    public void stopTracking(@NotNull Player player) {
        UUID playerId = player.getUniqueId();
        MyScheduledTask tracker = activeTrackers.remove(playerId);

        if (tracker != null && !tracker.isCancelled()) tracker.cancel();
    }

    public void stopAllTracking() {
        for (MyScheduledTask tracker : activeTrackers.values()) {
            if (!tracker.isCancelled()) tracker.cancel();
        }

        activeTrackers.clear();
    }

    public boolean isTracking(@NotNull Player player) {
        return activeTrackers.containsKey(player.getUniqueId());
    }

    public void handleItemChange(@NotNull Player player) {
        if (!ConfigKeys.TRACKER_ENABLED.getBoolean()) return;

        ItemStack mainHand = player.getInventory().getItemInMainHand();
        ItemStack offHand = player.getInventory().getItemInOffHand();
        ItemStack trackerItem = ConfigKeys.TRACKER_ITEM.getItem();

        if (trackerItem == null) return;

        boolean hasTrackerInHand = isSameItem(mainHand, trackerItem) || isSameItem(offHand, trackerItem);

        if (hasTrackerInHand && !isTracking(player)) startTracking(player);
        else if (!hasTrackerInHand && isTracking(player)) stopTracking(player);
    }

    private boolean isSameItem(@Nullable ItemStack item1, @NotNull ItemStack trackerItem) {
        if (item1 == null || item1.getType() == Material.AIR) return false;

        return item1.equals(trackerItem);
    }

    private @Nullable TreasureChest findNearestTreasure(@NotNull Player player) {
        Location playerLocation = player.getLocation();
        TreasureChest nearestChest = null;
        double nearestDistance = Double.MAX_VALUE;

        for (TreasureChest chest : TreasureManager.getInstance().getAllTreasures()) {
            if (chest.getLocation() == null) continue;
            if (!chest.getLocation().getWorld().equals(playerLocation.getWorld())) continue;

            double distance = playerLocation.distance(chest.getLocation());
            if (distance < nearestDistance) {
                nearestDistance = distance;
                nearestChest = chest;
            }
        }

        return nearestChest;
    }

    private @NotNull String createTrackerMessage(@NotNull Player player, @NotNull TreasureChest treasure) {
        Location playerLoc = player.getLocation();
        Location treasureLoc = treasure.getLocation();

        if (treasureLoc == null) return "";

        double distance = Math.round(playerLoc.distance(treasureLoc) * 10.0) / 10.0;
        String direction = getDirection(playerLoc, treasureLoc);

        String message = ConfigKeys.TRACKER_MESSAGE.getString();
        message = message.replace("{distance}", String.valueOf(distance));
        message = message.replace("{direction}", direction);

        return MessageProcessor.process(message);
    }

    private @NotNull String getDirection(@NotNull Location from, @NotNull Location to) {
        Vector playerDirection = from.getDirection().normalize();
        Vector toTreasure = to.toVector().subtract(from.toVector()).normalize();

        playerDirection.setY(0);
        toTreasure.setY(0);

        double dot = playerDirection.dot(toTreasure);
        double cross = playerDirection.getX() * toTreasure.getZ() - playerDirection.getZ() * toTreasure.getX();

        if (dot > 0.7) return ConfigKeys.TRACKER_DIRECTIONS_FORWARD.getString();
        else if (dot < -0.7) return ConfigKeys.TRACKER_DIRECTIONS_BACKWARD.getString();
        else if (cross > 0) return ConfigKeys.TRACKER_DIRECTIONS_RIGHT.getString();
        else return ConfigKeys.TRACKER_DIRECTIONS_LEFT.getString();
    }
}
