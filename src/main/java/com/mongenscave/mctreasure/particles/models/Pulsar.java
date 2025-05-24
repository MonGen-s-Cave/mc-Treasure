package com.mongenscave.mctreasure.particles.models;

import com.mongenscave.mctreasure.data.ParticleEffectConfiguration;
import com.mongenscave.mctreasure.particles.AbstractParticleEffect;
import org.bukkit.Location;
import org.bukkit.Particle;

public class Pulsar extends AbstractParticleEffect {
    private double pulseRadius = 0;
    private boolean expanding = true;

    @Override
    public void update() {
        super.update();

        if (expanding) {
            pulseRadius += config.getExpansionRate() * 0.1;
            if (pulseRadius >= config.getRadius()) expanding = false;
        } else {
            pulseRadius -= config.getExpansionRate() * 0.1;
            if (pulseRadius <= 0) expanding = true;
        }
    }

    @Override
    public void display() {
        Location center = config.getLocation();
        if (center == null) return;

        int density = config.getDensity();

        for (int i = 0; i < density; i++) {
            double angle = i * 2 * Math.PI / density;
            double x = pulseRadius * Math.cos(angle);
            double z = pulseRadius * Math.sin(angle);

            Location particleLoc = createLocation(x, 0, z);

            if (particleLoc != null) {
                if (config.getParticleType() == Particle.DUST)
                    spawnDustParticle(particleLoc, config.getParticleColor(), config.getParticleSize());
                else spawnParticle(particleLoc, config.getParticleType(), config.getParticleSpeed());
            }

            double y = pulseRadius * Math.sin(angle) * 0.5;
            Location particleLoc2 = createLocation(x, y, z);

            if (particleLoc2 != null) {
                if (config.getParticleType() == Particle.DUST)
                    spawnDustParticle(particleLoc2, config.getParticleColor(), config.getParticleSize());
                else spawnParticle(particleLoc2, config.getParticleType(), config.getParticleSpeed());
            }
        }
    }

    @Override
    public Pulsar clone() {
        Pulsar clone = new Pulsar();

        ParticleEffectConfiguration newConfig = new ParticleEffectConfiguration();
        newConfig.copyFrom(this.config);
        clone.setConfig(newConfig);
        return clone;
    }
}
