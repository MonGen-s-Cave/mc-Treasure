package com.mongenscave.mctreasure.api.data;

import org.jetbrains.annotations.Nullable;

public record CooldownResult(boolean canOpen, @Nullable String formattedTime, long remainingMillis) { }
