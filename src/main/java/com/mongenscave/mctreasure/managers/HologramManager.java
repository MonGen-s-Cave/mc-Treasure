package com.mongenscave.mctreasure.managers;

import com.mongenscave.mctreasure.McTreasure;
import com.mongenscave.mctreasure.managers.hologram.HologramProvider;
import com.mongenscave.mctreasure.managers.hologram.impl.DecentHologramProvider;
import com.mongenscave.mctreasure.managers.hologram.impl.FancyHologramProvider;
import com.mongenscave.mctreasure.utils.LoggerUtils;
import com.mongenscave.mctreasure.utils.RegisterUtils;
import lombok.Getter;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class HologramManager {
    private static HologramManager instance;
    @Getter private HologramProvider hologramProvider;
    private static final McTreasure plugin =  McTreasure.getInstance();

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
        if (hologramProvider != null) hologramProvider.shutdown();
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
            LoggerUtils.error(exception.getMessage());
        }
    }

    private void initializeDecentHolograms() {
        if (RegisterUtils.isPluginEnabled("DecentHolograms")) {
            this.hologramProvider = new DecentHologramProvider();
            LoggerUtils.info("Using DecentHolograms!");
        } else LoggerUtils.info("No hologram plugin found! Please install DecentHolograms or FancyHolograms!");
    }
}
