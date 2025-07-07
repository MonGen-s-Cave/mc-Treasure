package com.mongenscave.mctreasure.managers.hologram;

import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface HologramProvider {
    void createHologram(@NotNull String id, @NotNull Location location, List<String> lines);

    void removeHologram(@NotNull String id);

    void updateHologram(@NotNull String id, List<String> lines);

    boolean hologramExists(@NotNull String id);

    void shutdown();
}
