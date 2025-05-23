package net.netherpulse.nptreasure.sessions;

import net.netherpulse.nptreasure.identifiers.keys.MessageKeys;
import net.netherpulse.nptreasure.model.TreasureChest;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class LocationSession {
    private static final ConcurrentHashMap<UUID, TreasureChest> locationSettingSessions = new ConcurrentHashMap<>();

    public static void startSettingLocation(@NotNull Player player, @NotNull TreasureChest chest) {
        player.sendMessage(MessageKeys.SESSION_LOCATION_START.getMessage());
        locationSettingSessions.put(player.getUniqueId(), chest);
    }

    public static void exitSettingLocation(@NotNull Player player) {
        locationSettingSessions.remove(player.getUniqueId());
    }

    public static boolean isSettingLocation(@NotNull Player player) {
        return locationSettingSessions.containsKey(player.getUniqueId());
    }

    public static TreasureChest getChestForSettingLocation(@NotNull Player player) {
        return locationSettingSessions.get(player.getUniqueId());
    }
}
