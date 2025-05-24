package com.mongenscave.mctreasure.identifiers;

import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public enum ParticleTypes {
    HEART("Heart"),
    HELIX("Helix"),
    PULSAR("Pulsar"),
    SPHERE("Sphere"),
    TORNADO("Tornado");

    @Getter
    private final String displayName;

    ParticleTypes(@NotNull String displayName) {
        this.displayName = displayName;
    }

    @Nullable
    public static ParticleTypes getByName(@NotNull String name) {
        for (ParticleTypes type : values()) {
            if (type.name().equalsIgnoreCase(name) || type.getDisplayName().equalsIgnoreCase(name)) return type;
        }

        return null;
    }

    public static List<String> getDisplayNames() {
        return Arrays.stream(values())
                .map(ParticleTypes::getDisplayName)
                .collect(Collectors.toList());
    }

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
