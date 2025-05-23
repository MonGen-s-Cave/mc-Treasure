package net.netherpulse.nptreasure.utils;

import lombok.experimental.UtilityClass;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@UtilityClass
public class LocationUtils {
    @NotNull
    public String serialize(@NotNull Location location) {
        return location.getWorld().getName() + "," +
                location.getBlockX() + "," +
                location.getBlockY() + "," +
                location.getBlockZ();
    }

    @Nullable
    public Location deserialize(@NotNull String serializedLocation) {
        String[] parts = serializedLocation.split(",");
        if (parts.length != 4) return null;

        try {
            return new Location(
                    Bukkit.getWorld(parts[0]),
                    Integer.parseInt(parts[1]),
                    Integer.parseInt(parts[2]),
                    Integer.parseInt(parts[3])
            );
        } catch (Exception exception) {
            LoggerUtils.error("Could not deserialize location: " + serializedLocation);
            return null;
        }
    }
}
