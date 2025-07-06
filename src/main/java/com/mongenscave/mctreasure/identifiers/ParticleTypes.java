package com.mongenscave.mctreasure.identifiers;


public enum ParticleTypes {
    HEART,
    HELIX,
    PULSAR,
    SPHERE,
    TORNADO,
    PHOENIX,
    SPIRAL;

    public ParticleTypes next() {
        ParticleTypes[] values = ParticleTypes.values();
        int nextIndex = (this.ordinal() + 1) % values.length;
        return values[nextIndex];
    }

    public ParticleTypes previous() {
        ParticleTypes[] values = ParticleTypes.values();
        int prevIndex = (this.ordinal() - 1 + values.length) % values.length;
        return values[prevIndex];
    }
}
