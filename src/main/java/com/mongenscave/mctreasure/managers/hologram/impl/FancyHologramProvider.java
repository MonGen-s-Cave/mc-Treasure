package com.mongenscave.mctreasure.managers.hologram.impl;

import com.mongenscave.mctreasure.McTreasure;
import com.mongenscave.mctreasure.managers.hologram.HologramProvider;
import com.mongenscave.mctreasure.utils.LoggerUtils;
import de.oliver.fancyholograms.api.FancyHologramsPlugin;
import de.oliver.fancyholograms.api.HologramManager;
import de.oliver.fancyholograms.api.data.TextHologramData;
import de.oliver.fancyholograms.api.hologram.Hologram;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.entity.Display;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class FancyHologramProvider implements HologramProvider {
    private static final McTreasure plugin = McTreasure.getInstance();
    private final HologramManager hologramManager;
    private final Set<String> managedHolograms;

    private final Display.Billboard billboard;
    private final Color backgroundColor;
    private final boolean shadow;

    public FancyHologramProvider() {
        this.hologramManager = FancyHologramsPlugin.get().getHologramManager();
        this.managedHolograms = ConcurrentHashMap.newKeySet();

        this.billboard = parseBillboard(plugin.getConfiguration().getString("hook.fancy-holograms.billboard"));
        this.backgroundColor = parseTextColor(plugin.getConfiguration().getString("hook.fancy-holograms.background"));
        this.shadow = plugin.getConfiguration().getBoolean("hook.fancy-holograms.shadow");
    }

    @Override
    public void createHologram(@NotNull String id, @NotNull Location location, List<String> lines) {
        if (hologramExists(id)) {
            removeHologram(id);
        }

        try {
            TextHologramData hologramData = new TextHologramData(id, location);

            hologramData.setBillboard(billboard);

            if (backgroundColor != null) {
                hologramData.setBackground(backgroundColor);
            }

            hologramData.setText(lines);
            hologramData.setPersistent(false);
            hologramData.setTextShadow(shadow);

            Hologram hologram = hologramManager.create(hologramData);
            hologramManager.addHologram(hologram);

            managedHolograms.add(id);
        } catch (Exception exception) {
            LoggerUtils.error("Failed to create FancyHologram '" + id + "': " + exception.getMessage());
        }
    }

    @Override
    public void removeHologram(@NotNull String id) {
        if (!hologramExists(id)) return;

        try {
            Optional<Hologram> hologram = hologramManager.getHologram(id);

            if (hologram.isPresent()) {
                hologramManager.removeHologram(hologram.get());
                managedHolograms.remove(id);
            }
        } catch (Exception exception) {
            LoggerUtils.error("Failed to remove FancyHologram '" + id + "': " + exception.getMessage());
        }
    }

    @Override
    public void updateHologram(@NotNull String id, List<String> lines) {
        if (!hologramExists(id)) {
            LoggerUtils.warn("Attempted to update non-existent FancyHologram: " + id);
            return;
        }

        try {
            Optional<Hologram> hologram = hologramManager.getHologram(id);

            if (hologram.isPresent() && hologram.get().getData() instanceof TextHologramData data) {
                data.setText(lines);
                hologram.get().forceUpdate();
            }
        } catch (Exception exception) {
            LoggerUtils.error("Failed to update FancyHologram '" + id + "': " + exception.getMessage());
        }
    }

    @Override
    public boolean hologramExists(@NotNull String id) {
        return hologramManager.getHologram(id).isPresent();
    }

    @Override
    public void shutdown() {
        LoggerUtils.info("Shutting down FancyHologramProvider, removing " + managedHolograms.size() + " holograms...");

        Set<String> hologramsToRemove = Collections.synchronizedSet(new HashSet<>(managedHolograms));

        for (String id : hologramsToRemove) {
            removeHologram(id);
        }

        managedHolograms.clear();
        LoggerUtils.info("FancyHologramProvider shutdown complete.");
    }

    private Display.Billboard parseBillboard(@NotNull String text) {
        if (text.isEmpty()) return Display.Billboard.FIXED;

        try {
            return Display.Billboard.valueOf(text.toUpperCase());
        } catch (IllegalArgumentException exception) {
            LoggerUtils.warn("Invalid billboard type: " + text + ", using FIXED as default");
            return Display.Billboard.FIXED;
        }
    }

    @Nullable
    private Color parseTextColor(@NotNull String text) {
        if (text.isEmpty()) return null;
        if ("TRANSPARENT".equalsIgnoreCase(text)) return Color.fromARGB(0);

        try {
            String[] rgb = text.split(";");

            if (rgb.length == 3) {
                int r = Integer.parseInt(rgb[0].trim());
                int g = Integer.parseInt(rgb[1].trim());
                int b = Integer.parseInt(rgb[2].trim());

                if (r >= 0 && r <= 255 && g >= 0 && g <= 255 && b >= 0 && b <= 255) return Color.fromRGB(r, g, b);
            }
        } catch (NumberFormatException exception) {
            LoggerUtils.warn("Invalid color format: " + text);
        }

        return null;
    }
}