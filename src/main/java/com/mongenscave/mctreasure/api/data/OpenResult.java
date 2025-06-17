package com.mongenscave.mctreasure.api.data;

import org.jetbrains.annotations.Nullable;

public record OpenResult(boolean canOpen, @Nullable String message) {}
