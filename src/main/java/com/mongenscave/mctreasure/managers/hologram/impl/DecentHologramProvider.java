package com.mongenscave.mctreasure.managers.hologram.impl;

import com.mongenscave.mctreasure.managers.hologram.HologramProvider;
import com.mongenscave.mctreasure.utils.LoggerUtils;
import eu.decentsoftware.holograms.api.DHAPI;
import eu.decentsoftware.holograms.api.holograms.Hologram;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class DecentHologramProvider implements HologramProvider {
    private final Set<String> managedHolograms;

    public DecentHologramProvider() {
        this.managedHolograms = ConcurrentHashMap.newKeySet();
    }

    @Override
    public void createHologram(@NotNull String id, @NotNull Location location, List<String> lines) {
        if (hologramExists(id)) removeHologram(id);

        try {
            DHAPI.createHologram(id, location, lines);
            managedHolograms.add(id);
        } catch (Exception exception) {
            LoggerUtils.error("Failed to create DecentHologram '" + id + "': " + exception.getMessage());
        }
    }

    @Override
    public void removeHologram(@NotNull String id) {
        if (!hologramExists(id)) return;

        try {
            DHAPI.removeHologram(id);
            managedHolograms.remove(id);
        } catch (Exception exception) {
            LoggerUtils.error("Failed to remove DecentHologram '" + id + "': " + exception.getMessage());
        }
    }

    @Override
    public void updateHologram(@NotNull String id, List<String> lines) {
        if (!hologramExists(id)) {
            LoggerUtils.warn("Attempted to update non-existent DecentHologram: " + id);
            return;
        }

        try {
            Hologram hologram = DHAPI.getHologram(id);

            if (hologram != null) {
                hologram.realignLines();
                DHAPI.setHologramLines(hologram, lines);
            }
        } catch (Exception exception) {
            LoggerUtils.error("Failed to update DecentHologram '" + id + "': " + exception.getMessage());
        }
    }

    @Override
    public boolean hologramExists(@NotNull String id) {
        return DHAPI.getHologram(id) != null;
    }

    @Override
    public void shutdown() {
        LoggerUtils.info("Shutting down DecentHologramProvider, removing " + managedHolograms.size() + " holograms...");

        Set<String> hologramsToRemove = Collections.synchronizedSet(new HashSet<>(managedHolograms));

        for (String id : hologramsToRemove) {
            removeHologram(id);
        }

        managedHolograms.clear();
        LoggerUtils.info("DecentHologramProvider shutdown complete.");
    }
}