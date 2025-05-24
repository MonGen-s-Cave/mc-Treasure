package com.mongenscave.mctreasure.interfaces;

import com.mongenscave.mctreasure.data.ParticleEffectConfiguration;

public interface ParticleEffect {
    void update();

    void display();

    ParticleEffectConfiguration getConfig();

    void setConfig(ParticleEffectConfiguration config);

    boolean isComplete();

    ParticleEffect clone();
}
