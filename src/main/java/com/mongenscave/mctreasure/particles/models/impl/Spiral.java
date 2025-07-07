package com.mongenscave.mctreasure.particles.models.impl;

import com.mongenscave.mctreasure.data.ParticleEffectConfiguration;
import com.mongenscave.mctreasure.particles.AbstractParticleEffect;
import org.bukkit.Location;
import org.bukkit.Particle;

public class Spiral extends AbstractParticleEffect {
    private double angle = 0;
    private static final int NUMBER_OF_ARMS = 3;
    private static final double SPIRAL_TIGHTNESS = 0.5;

    @Override
    public void update() {
        super.update();
        angle += config.getRotationSpeed() * 0.02;
        if (angle >= 360) angle = 0;
    }

    @Override
    public void display() {
        Location center = config.getLocation();
        if (center == null) return;

        double maxRadius = config.getRadius();
        int density = config.getDensity();

        for (int armIndex = 0; armIndex < NUMBER_OF_ARMS; armIndex++) {
            double armAngle = angle + (armIndex * 2 * Math.PI / NUMBER_OF_ARMS);

            for (int i = 0; i < density; i++) {
                double distance = (double) i / density;
                double spiralRadius = distance * maxRadius;
                double spiralAngle = armAngle + (distance * SPIRAL_TIGHTNESS * 4 * Math.PI);

                double x = spiralRadius * Math.cos(spiralAngle);
                double z = spiralRadius * Math.sin(spiralAngle);
                double y = Math.sin(distance * Math.PI * 2) * 0.5;

                Location particleLoc = createLocation(x, y, z);

                if (particleLoc != null) {
                    if (config.getParticleType() == Particle.DUST) spawnDustParticle(particleLoc, config.getParticleColor(), config.getParticleSize());
                    else spawnParticle(particleLoc, config.getParticleType(), config.getParticleSpeed());
                }
            }
        }

        for (int i = 0; i < 5; i++) {
            double centerAngle = angle * 2 + (i * 2 * Math.PI / 5);
            double centerRadius = maxRadius * 0.1;
            double x = centerRadius * Math.cos(centerAngle);
            double z = centerRadius * Math.sin(centerAngle);

            Location centerLoc = createLocation(x, 0, z);
            if (centerLoc != null) {
                if (config.getParticleType() == Particle.DUST) spawnDustParticle(centerLoc, config.getParticleColor(), config.getParticleSize());
                else spawnParticle(centerLoc, config.getParticleType(), config.getParticleSpeed());
            }
        }
    }

    @Override
    public Spiral clone() {
        Spiral clone = new Spiral();

        ParticleEffectConfiguration newConfig = new ParticleEffectConfiguration();
        newConfig.copyFrom(this.config);
        clone.setConfig(newConfig);
        return clone;
    }
}