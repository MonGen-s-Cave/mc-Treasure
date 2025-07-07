package com.mongenscave.mctreasure.managers;

import com.mongenscave.mctreasure.McTreasure;
import com.mongenscave.mctreasure.managers.hologram.HologramProvider;
import com.mongenscave.mctreasure.managers.hologram.impl.DecentHologramProvider;
import com.mongenscave.mctreasure.managers.hologram.impl.FancyHologramProvider;
import com.mongenscave.mctreasure.utils.LoggerUtils;
import com.mongenscave.mctreasure.utils.RegisterUtils;
import eu.decentsoftware.holograms.api.DHAPI;
import de.oliver.fancyholograms.api.FancyHologramsPlugin;
import lombok.Getter;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class HologramManager {
    private static HologramManager instance;
    @Getter private HologramProvider hologramProvider;
    private static final McTreasure plugin = McTreasure.getInstance();

    private HologramManager() {
        initializeHologramProvider();
    }

    public static HologramManager getInstance() {
        if (instance == null) instance = new HologramManager();
        return instance;
    }

    public void createHologram(@NotNull String id, @NotNull Location location, List<String> lines) {
        if (hologramProvider != null) hologramProvider.createHologram(id, location, lines);
    }

    public void removeHologram(@NotNull String id) {
        if (hologramProvider != null) hologramProvider.removeHologram(id);
    }

    public void updateHologram(@NotNull String id, List<String> lines) {
        if (hologramProvider != null) hologramProvider.updateHologram(id, lines);
    }

    public boolean hologramExists(@NotNull String id) {
        if (hologramProvider != null) return hologramProvider.hologramExists(id);
        return false;
    }

    public void shutdown() {
        if (hologramProvider != null) {
            hologramProvider.shutdown();
            hologramProvider = null;
        }
    }

    public void reload() {
        shutdown();
        cleanupAllHolograms();
        initializeHologramProvider();

        LoggerUtils.info("HologramManager reloaded successfully!");
    }

    public static void resetInstance() {
        if (instance != null) {
            instance.shutdown();
            instance = null;
        }
    }

    private void cleanupAllHolograms() {
        LoggerUtils.info("Cleaning up all MCTreasure holograms from all providers...");

        if (RegisterUtils.isPluginEnabled("DecentHolograms")) {
            try {
                for (var treasure : TreasureManager.getInstance().getAllTreasures()) {
                    String hologramId = "treasure-" + treasure.getId();
                    if (DHAPI.getHologram(hologramId) != null) DHAPI.removeHologram(hologramId);
                }
            } catch (Exception exception) {
                LoggerUtils.error("Error cleaning up DecentHolograms: " + exception.getMessage());
            }
        }

        if (RegisterUtils.isPluginEnabled("FancyHolograms")) {
            try {
                var hologramManager = FancyHologramsPlugin.get().getHologramManager();
                for (var treasure : TreasureManager.getInstance().getAllTreasures()) {
                    String hologramId = "treasure-" + treasure.getId();
                    var hologram = hologramManager.getHologram(hologramId);
                    hologram.ifPresent(hologramManager::removeHologram);
                }
            } catch (Exception exception) {
                LoggerUtils.error("Error cleaning up FancyHolograms: " + exception.getMessage());
            }
        }
    }

    private void initializeHologramProvider() {
        String hologramType = plugin.getConfig().getString("hook.hologram-type", "DecentHolograms");

        try {
            if ("fancyholograms".equalsIgnoreCase(hologramType)) {
                if (RegisterUtils.isPluginEnabled("FancyHolograms")) {
                    this.hologramProvider = new FancyHologramProvider();
                    LoggerUtils.info("Using FancyHolograms!");
                    return;
                } else LoggerUtils.info("FancyHolograms not found, FALLBACK -> DecentHolograms!");
            }

            initializeDecentHolograms();
        } catch (Exception exception) {
            LoggerUtils.error("Error initializing hologram provider: " + exception.getMessage());
        }
    }

    private void initializeDecentHolograms() {
        if (RegisterUtils.isPluginEnabled("DecentHolograms")) {
            this.hologramProvider = new DecentHologramProvider();
            LoggerUtils.info("Using DecentHolograms!");
        } else {
            LoggerUtils.warn("No hologram plugin found! Please install DecentHolograms or FancyHolograms!");
            this.hologramProvider = null;
        }
    }
}