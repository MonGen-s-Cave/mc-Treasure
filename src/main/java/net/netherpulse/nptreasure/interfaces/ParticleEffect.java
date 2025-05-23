package net.netherpulse.nptreasure.interfaces;


import net.netherpulse.nptreasure.data.ParticleEffectConfiguration;

public interface ParticleEffect {
    void update();

    void display();

    ParticleEffectConfiguration getConfig();

    void setConfig(ParticleEffectConfiguration config);

    boolean isComplete();

    ParticleEffect clone();
}
