package com.mongenscave.mctreasure.managers.hologram.impl;

import com.mongenscave.mctreasure.managers.hologram.HologramProvider;
import eu.decentsoftware.holograms.api.DHAPI;
import eu.decentsoftware.holograms.api.holograms.Hologram;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class DecentHologramProvider implements HologramProvider {
    @Override
    public void createHologram(@NotNull String id, @NotNull Location location, List<String> lines) {
        if (hologramExists(id)) removeHologram(id);

        DHAPI.createHologram(id, location, lines);
    }

    @Override
    public void removeHologram(@NotNull String id) {
        if (hologramExists(id)) DHAPI.removeHologram(id);
    }

    @Override
    public void updateHologram(@NotNull String id, List<String> lines) {
        if (hologramExists(id)) {
            Hologram hologram = DHAPI.getHologram(id);

            if (hologram != null) {
                hologram.realignLines();
                DHAPI.setHologramLines(hologram, lines);
            }
        }
    }

    @Override
    public boolean hologramExists(@NotNull String id) {
        return DHAPI.getHologram(id) != null;
    }

    @Override
    public void shutdown() {
        // automatic
    }
}
