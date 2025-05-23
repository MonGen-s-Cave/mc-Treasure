package net.netherpulse.nptreasure.data;

import org.jetbrains.annotations.Nullable;

public record OpenResult(boolean canOpen, @Nullable String message) {
}
