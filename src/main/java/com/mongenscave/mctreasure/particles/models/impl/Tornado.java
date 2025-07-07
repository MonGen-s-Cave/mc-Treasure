package com.mongenscave.mctreasure.particles.models.impl;

import com.mongenscave.mctreasure.data.ParticleEffectConfiguration;
import com.mongenscave.mctreasure.particles.AbstractParticleEffect;
import org.bukkit.Location;
import org.bukkit.Particle;

public class Tornado extends AbstractParticleEffect {
    private double angle = 0;
    private double height = 0;
    private boolean ascending = true;

    @Override
    public void update() {
        super.update();

        angle += config.getRotationSpeed() * 0.2;
        if (angle >= 360) angle = 0;

        if (ascending) {
            height += 0.1;
            if (height >= config.getHeight()) ascending = false;
        } else {
            height -= 0.1;
            if (height <= 0) ascending = true;
        }
    }

    @Override
    public void display() {
        Location center = config.getLocation();
        if (center == null) return;

        double maxHeight = config.getHeight();
        int density = config.getDensity();

        for (double y = 0; y <= maxHeight; y += maxHeight / density) {
            double radiusAtHeight = config.getRadius() * (1 - y / maxHeight);

            for (int i = 0; i < 8; i++) {
                double circleAngle = angle + (i * Math.PI / 4);
                double x = radiusAtHeight * Math.cos(circleAngle);
                double z = radiusAtHeight * Math.sin(circleAngle);

                Location particleLoc = createLocation(x, y + height % maxHeight, z);

                if (particleLoc != null) {
                    if (config.getParticleType() == Particle.DUST) spawnDustParticle(particleLoc, config.getParticleColor(), config.getParticleSize());
                    else spawnParticle(particleLoc, config.getParticleType(), config.getParticleSpeed());
                }
            }
        }
    }

    @Override
    public Tornado clone() {
        Tornado clone = new Tornado();

        ParticleEffectConfiguration newConfig = new ParticleEffectConfiguration();
        newConfig.copyFrom(this.config);
        clone.setConfig(newConfig);
        return clone;
    }
}
