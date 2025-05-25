package com.mongenscave.mctreasure.data;

import org.jetbrains.annotations.Nullable;

public record CooldownResult(boolean canOpen, @Nullable String formattedTime, long remainingMillis) { }
