package com.mongenscave.mctreasure.api.model;

import com.mongenscave.mctreasure.api.data.OpenResult;
import com.mongenscave.mctreasure.identifiers.ParticleTypes;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Interface representing a treasure chest in the McTreasure plugin
 * This interface provides read-only access to treasure chest data for external plugins
 */
public interface ITreasureChest {

    /**
     * Get the unique identifier of this treasure chest
     * @return the treasure chest ID
     */
    @NotNull
    String getId();

    /**
     * Get the display name of this treasure chest
     * @return the treasure chest name (with color codes)
     */
    @NotNull
    String getName();

    /**
     * Get the location of this treasure chest
     * @return the location or null if not set
     */
    @Nullable
    Location getLocation();

    /**
     * Get the permission required to open this treasure chest
     * @return the permission string, empty if no permission required
     */
    @NotNull
    String getPermission();

    /**
     * Get the cooldown time in milliseconds
     * @return cooldown in milliseconds, 0 if no cooldown
     */
    long getCooldownMillis();

    /**
     * Check if a player can open this treasure chest
     * @param player the player to check
     * @return OpenResult containing whether player can open and optional message
     */
    @NotNull
    OpenResult canPlayerOpen(@NotNull Player player);

    /**
     * Get the inventory size of this treasure chest
     * @return the inventory size (9, 18, 27, 36, 45, or 54)
     */
    int getSize();

    /**
     * Get all items in this treasure chest
     * @return unmodifiable list of items
     */
    @NotNull
    List<ItemStack> getItems();

    /**
     * Check if the treasure chest has any items
     * @return true if has items, false otherwise
     */
    default boolean hasItems() {
        List<ItemStack> items = getItems();
        return !items.isEmpty();
    }

    /**
     * Get the number of items in this treasure chest
     * @return number of items
     */
    default int getItemCount() {
        List<ItemStack> items = getItems();
        return items.size();
    }

    /**
     * Check if pushback is enabled for this treasure chest
     * @return true if pushback is enabled
     */
    boolean isPushbackEnabled();

    /**
     * Get the pushback strength
     * @return pushback strength multiplier
     */
    double getPushbackStrength();

    /**
     * Check if hologram is enabled for this treasure chest
     * @return true if hologram is enabled
     */
    boolean isHologramEnabled();

    /**
     * Get the hologram lines
     * @return list of hologram lines (with color codes and placeholders)
     */
    @NotNull
    List<String> getHologramLines();

    /**
     * Check if particle effects are enabled for this treasure chest
     * @return true if particle effects are enabled
     */
    boolean isParticleEnabled();

    /**
     * Get the particle type for this treasure chest
     * @return the particle type enum
     */
    @NotNull
    ParticleTypes getParticleType();

    /**
     * Get the actual Bukkit particle for display
     * @return the Bukkit particle
     */
    @NotNull
    Particle getParticleDisplay();


    /**
     * Check if this treasure chest is properly configured
     * @return true if treasure chest has location and items
     */
    default boolean isConfigured() {
        return getLocation() != null && hasItems();
    }

    /**
     * Check if this treasure chest is at the specified location
     * @param location the location to check
     * @return true if treasure chest is at this location
     */
    default boolean isAtLocation(@NotNull Location location) {
        Location treasureLocation = getLocation();
        return treasureLocation != null && treasureLocation.equals(location);
    }

    /**
     * Check if player has permission to open this treasure chest
     * @param player the player to check
     * @return true if player has permission or no permission required
     */
    default boolean hasPermission(@NotNull Player player) {
        String permission = getPermission();
        return permission.isEmpty() || player.hasPermission(permission);
    }
}