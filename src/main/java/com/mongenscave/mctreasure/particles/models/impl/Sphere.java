package com.mongenscave.mctreasure.particles.models.impl;

import com.mongenscave.mctreasure.data.ParticleEffectConfiguration;
import com.mongenscave.mctreasure.particles.AbstractParticleEffect;
import org.bukkit.Location;
import org.bukkit.Particle;

public class Sphere extends AbstractParticleEffect {
    private double rotationX = 0;
    private double rotationY = 0;

    @Override
    public void update() {
        super.update();

        rotationX += config.getRotationSpeed() * 0.05;
        rotationY += config.getRotationSpeed() * 0.03;

        if (rotationX >= 360) rotationX = 0;
        if (rotationY >= 360) rotationY = 0;
    }

    @Override
    public void display() {
        Location center = config.getLocation();
        if (center == null) return;

        double radius = config.getRadius();
        int density = Math.max(3, config.getDensity() / 2);

        for (int i = 0; i < density; i++) {
            double phi = Math.acos(-1 + (2.0 * i) / density);

            for (int j = 0; j < density; j++) {
                double theta = 2 * Math.PI * j / density;

                double x = radius * Math.sin(phi) * Math.cos(theta);
                double y = radius * Math.sin(phi) * Math.sin(theta);
                double z = radius * Math.cos(phi);

                double newX = x * Math.cos(rotationX) - z * Math.sin(rotationX);
                double newZ = x * Math.sin(rotationX) + z * Math.cos(rotationX);
                double newY = y * Math.cos(rotationY) - newZ * Math.sin(rotationY);
                newZ = y * Math.sin(rotationY) + newZ * Math.cos(rotationY);

                Location particleLoc = createLocation(newX, newY, newZ);

                if (particleLoc != null) {
                    if (config.getParticleType() == Particle.DUST) spawnDustParticle(particleLoc, config.getParticleColor(), config.getParticleSize());
                    else spawnParticle(particleLoc, config.getParticleType(), config.getParticleSpeed());
                }
            }
        }
    }

    @Override
    public Sphere clone() {
        Sphere clone = new Sphere();

        ParticleEffectConfiguration newConfig = new ParticleEffectConfiguration();
        newConfig.copyFrom(this.config);
        clone.setConfig(newConfig);
        return clone;
    }
}
