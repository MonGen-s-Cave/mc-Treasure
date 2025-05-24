package com.mongenscave.mctreasure.particles.models;

import com.mongenscave.mctreasure.data.ParticleEffectConfiguration;
import com.mongenscave.mctreasure.particles.AbstractParticleEffect;
import org.bukkit.Location;
import org.bukkit.Particle;

public class Heart extends AbstractParticleEffect {
    private static final double MAX_SIZE = 1.0;
    private static final double GROWTH_RATE = 0.05;
    private double size = 0;

    @Override
    public void update() {
        super.update();

        size += GROWTH_RATE * config.getExpansionRate();

        if (size >= MAX_SIZE) size = 0;
    }

    @Override
    public void display() {
        Location center = config.getLocation();
        if (center == null) return;

        double radius = config.getRadius() * size;
        double scale = radius / MAX_SIZE;

        for (double t = 0; t <= Math.PI * 2; t += Math.PI / 16) {
            double x = scale * 16 * Math.pow(Math.sin(t), 3);
            double y = scale * (13 * Math.cos(t) - 5 * Math.cos(2 * t) - 2 * Math.cos(3 * t) - Math.cos(4 * t));

            Location particleLoc = createLocation(x, y, 0);

            if (particleLoc != null) {
                if (config.getParticleType() == Particle.DUST) spawnDustParticle(particleLoc, config.getParticleColor(), config.getParticleSize());
                else spawnParticle(particleLoc, config.getParticleType(), config.getParticleSpeed());
            }
        }
    }

    @Override
    public Heart clone() {
        Heart clone = new Heart();

        ParticleEffectConfiguration newConfig = new ParticleEffectConfiguration();
        newConfig.copyFrom(this.config);
        clone.setConfig(newConfig);
        return clone;
    }
}
