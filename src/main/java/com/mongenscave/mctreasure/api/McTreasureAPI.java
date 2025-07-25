package com.mongenscave.mctreasure.api;

import com.mongenscave.mctreasure.api.model.ITreasureChest;
import com.mongenscave.mctreasure.managers.TreasureManager;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Main API class for McTreasure plugin
 */
public class McTreasureAPI {

    /**
     * Get a treasure chest by its ID
     * @param id The treasure chest ID
     * @return The treasure chest or null if not found
     */
    @Nullable
    public static ITreasureChest getTreasure(@NotNull String id) {
        return TreasureManager.getInstance().getTreasure(id);
    }

    /**
     * Get all treasure chests
     * @return List of all treasure chests
     */
    @NotNull
    public static List<ITreasureChest> getAllTreasures() {
        return TreasureManager.getInstance().getAllTreasures()
                .stream()
                .map(treasure -> (ITreasureChest) treasure)
                .toList();
    }

    /**
     * Create a new treasure chest
     * @param id The treasure chest ID
     * @return The created treasure chest
     */
    @NotNull
    public static ITreasureChest createTreasure(@NotNull String id) {
        return TreasureManager.getInstance().createTreasure(id);
    }

    /**
     * Delete a treasure chest
     * @param id The treasure chest ID
     * @return true if deleted successfully
     */
    public static boolean deleteTreasure(@NotNull String id) {
        return TreasureManager.getInstance().deleteTreasure(id);
    }

    /**
     * Get treasure chest at specific location
     * @param location The location to check
     * @return The treasure chest at location or null
     */
    @Nullable
    public static ITreasureChest getTreasureAtLocation(@NotNull Location location) {
        return TreasureManager.getInstance().getChestAtLocation(location);
    }

    /**
     * Check if player can open a treasure
     * @param player The player
     * @param treasure The treasure chest
     * @return true if player can open
     */
    public static boolean canPlayerOpen(@NotNull Player player, @NotNull ITreasureChest treasure) {
        return treasure.canPlayerOpen(player).canOpen();
    }
}