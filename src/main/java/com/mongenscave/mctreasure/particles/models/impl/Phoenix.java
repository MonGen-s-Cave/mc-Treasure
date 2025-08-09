package com.mongenscave.mctreasure.particles.models.impl;

import com.mongenscave.mctreasure.data.ParticleEffectConfiguration;
import com.mongenscave.mctreasure.particles.AbstractParticleEffect;
import org.bukkit.Location;
import org.bukkit.Particle;

public class Phoenix extends AbstractParticleEffect {
    private double time = 0;
    private static final double FLAP_SPEED = 0.3;
    private static final double MAX_FLAP = 0.8;

    @Override
    public void update() {
        super.update();
        time += config.getRotationSpeed() * 0.1;
        if (time >= 2 * Math.PI) time = 0;
    }

    @Override
    public void display() {
        Location center = config.getLocation();
        if (center == null) return;

        double wingspan = config.getRadius();
        double wingHeight = config.getHeight();
        int density = config.getDensity();

        double flapAmplitude = Math.sin(time * FLAP_SPEED) * MAX_FLAP;
        double wingAngle = Math.sin(time * 0.1) * Math.PI / 6;

        for (int wing = 0; wing < 2; wing++) {
            double wingMultiplier = wing == 0 ? 1 : -1;

            for (int i = 0; i <= density; i++) {
                double t = (double) i / density;

                double wingShape = Math.sin(t * 3) * (1 - t * t);

                double x = wingMultiplier * t * wingspan * Math.cos(wingAngle);
                double y = wingShape * wingHeight + flapAmplitude * Math.sin(t * Math.PI);
                double z = t * wingspan * Math.sin(wingAngle) + wingShape * 0.3;

                Location particleLoc = createLocation(x, y, z);

                if (particleLoc != null) spawnParticle(particleLoc);

                if (t > 0.3) {
                    for (int feather = 0; feather < 3; feather++) {
                        double featherOffset = (feather - 1) * 0.2;
                        double featherX = x + wingMultiplier * featherOffset * Math.cos(wingAngle + Math.PI/2);
                        double featherZ = z + featherOffset * Math.sin(wingAngle + Math.PI/2);

                        Location featherLoc = createLocation(featherX, y, featherZ);

                        if (featherLoc != null) spawnParticle(featherLoc);
                    }
                }
            }
        }

        Location bodyLoc = createLocation(0, wingHeight * 0.3, 0);
        if (bodyLoc != null) spawnParticle(bodyLoc);
    }

    @Override
    public Phoenix clone() {
        Phoenix clone = new Phoenix();

        ParticleEffectConfiguration newConfig = new ParticleEffectConfiguration();
        newConfig.copyFrom(this.config);
        clone.setConfig(newConfig);
        return clone;
    }
}