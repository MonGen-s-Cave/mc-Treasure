package com.mongenscave.mctreasure.particles;

import com.mongenscave.mctreasure.data.ParticleEffectConfiguration;
import com.mongenscave.mctreasure.particles.models.ParticleEffect;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public abstract class AbstractParticleEffect implements ParticleEffect, Cloneable {
    @Getter @Setter protected ParticleEffectConfiguration config;
    protected int ticks = 0;
    protected boolean complete = false;

    public AbstractParticleEffect() {
        this.config = new ParticleEffectConfiguration();
    }

    @Override
    public boolean isComplete() {
        if (config.getDuration() > 0 && ticks >= config.getDuration()) return true;
        return complete;
    }

    @Override
    public void update() {
        ticks++;
    }

    protected void spawnParticle(Location location) {
        if (location == null || location.getWorld() == null || config.getParticleType() == null) return;

        if (config.isVisibleToAll()) {
            location.getWorld().spawnParticle(config.getParticleType(), location, 1, 0, 0, 0, 0);
        } else if (config.getPlayer() != null && config.getPlayer().isOnline()) {
            Player player = config.getPlayer();
            player.spawnParticle(config.getParticleType(), location, 1, 0, 0, 0, 0);
        }
    }

    protected void spawnParticle(Location location, int count) {
        if (location == null || location.getWorld() == null || config.getParticleType() == null) return;

        if (config.isVisibleToAll()) {
            location.getWorld().spawnParticle(config.getParticleType(), location, count, 0, 0, 0, 0);
        } else if (config.getPlayer() != null && config.getPlayer().isOnline()) {
            Player player = config.getPlayer();
            player.spawnParticle(config.getParticleType(), location, count, 0, 0, 0, 0);
        }
    }

    protected void spawnParticle(Location location, double offsetX, double offsetY, double offsetZ) {
        if (location == null || location.getWorld() == null || config.getParticleType() == null) return;

        if (config.isVisibleToAll()) {
            location.getWorld().spawnParticle(config.getParticleType(), location, 1, offsetX, offsetY, offsetZ, 0);
        } else if (config.getPlayer() != null && config.getPlayer().isOnline()) {
            Player player = config.getPlayer();
            player.spawnParticle(config.getParticleType(), location, 1, offsetX, offsetY, offsetZ, 0);
        }
    }

    protected Location createLocation(double x, double y, double z) {
        Location origin = config.getLocation();
        if (origin == null) return null;

        return new Location(
                origin.getWorld(),
                origin.getX() + x,
                origin.getY() + y,
                origin.getZ() + z
        );
    }

    @Override
    public abstract AbstractParticleEffect clone();
}