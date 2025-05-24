package com.mongenscave.mctreasure.particles.models;

import com.mongenscave.mctreasure.data.ParticleEffectConfiguration;
import com.mongenscave.mctreasure.particles.AbstractParticleEffect;
import org.bukkit.Location;
import org.bukkit.Particle;

public class Helix extends AbstractParticleEffect {
    private double angle = 0;

    @Override
    public void update() {
        super.update();
        angle += config.getRotationSpeed() * 0.1;
        if (angle >= 360) angle = 0;
    }

    @Override
    public void display() {
        Location center = config.getLocation();
        if (center == null) return;

        double radius = config.getRadius();
        double height = config.getHeight();
        int density = config.getDensity();

        for (double y = 0; y <= height; y += height / density) {
            double x1 = radius * Math.cos(angle + (y * 2 * Math.PI / height));
            double z1 = radius * Math.sin(angle + (y * 2 * Math.PI / height));

            double x2 = radius * Math.cos(angle + Math.PI + (y * 2 * Math.PI / height));
            double z2 = radius * Math.sin(angle + Math.PI + (y * 2 * Math.PI / height));

            Location particleLoc1 = createLocation(x1, y, z1);
            Location particleLoc2 = createLocation(x2, y, z2);

            if (particleLoc1 != null) {
                if (config.getParticleType() == Particle.DUST)
                    spawnDustParticle(particleLoc1, config.getParticleColor(), config.getParticleSize());
                else spawnParticle(particleLoc1, config.getParticleType(), config.getParticleSpeed());
            }

            if (particleLoc2 != null) {
                if (config.getParticleType() == Particle.DUST)
                    spawnDustParticle(particleLoc2, config.getParticleColor(), config.getParticleSize());
                else spawnParticle(particleLoc2, config.getParticleType(), config.getParticleSpeed());
            }
        }
    }

    @Override
    public Helix clone() {
        Helix clone = new Helix();

        ParticleEffectConfiguration newConfig = new ParticleEffectConfiguration();
        newConfig.copyFrom(this.config);
        clone.setConfig(newConfig);
        return clone;
    }
}
