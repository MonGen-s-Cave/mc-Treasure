package com.mongenscave.mctreasure.particles;

import com.mongenscave.mctreasure.data.ParticleEffectConfiguration;
import com.mongenscave.mctreasure.particles.models.ParticleEffect;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;

public abstract class AbstractParticleEffect implements ParticleEffect, Cloneable {
    @Getter
    @Setter
    protected ParticleEffectConfiguration config;
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

    protected void spawnParticle(Location location, Particle particle, double extra) {
        if (location == null || location.getWorld() == null || particle == null) return;

        if (config.isVisibleToAll()) {
            location.getWorld().spawnParticle(particle, location, 1, 0, 0, 0, extra);
        } else if (config.getPlayer() != null && config.getPlayer().isOnline()) {
            Player player = config.getPlayer();
            player.spawnParticle(particle, location, 1, 0, 0, 0, extra);
        }
    }

    protected void spawnDustParticle(Location location, Color color, float size) {
        if (location == null || location.getWorld() == null) return;

        Particle.DustOptions dustOptions = new Particle.DustOptions(color, size);

        if (config.isVisibleToAll()) {
            location.getWorld().spawnParticle(Particle.DUST, location, 1, 0, 0, 0, 0, dustOptions);
        } else if (config.getPlayer() != null && config.getPlayer().isOnline()) {
            Player player = config.getPlayer();
            player.spawnParticle(Particle.DUST, location, 1, 0, 0, 0, 0, dustOptions);
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