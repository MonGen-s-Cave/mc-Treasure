package com.mongenscave.mctreasure.api.data;

import org.jetbrains.annotations.NotNull;

public record CooldownData(long lastOpenedTimestamp, long cooldownMillis) {

    @NotNull
    @Override
    public String toString() {
        return "CooldownData{timestamp=" + lastOpenedTimestamp + ", cooldown=" + cooldownMillis + "}";
    }
}
