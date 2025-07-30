package com.mongenscave.mctreasure.managers.cooldown;

import com.mongenscave.mctreasure.api.data.CooldownResult;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface CooldownProvider {
    @NotNull CompletableFuture<CooldownResult> checkCooldownAsync(@NotNull String treasureId, @NotNull UUID playerId, long cooldownMillis);

    @NotNull CompletableFuture<Void> recordOpenAsync(@NotNull String treasureId, @NotNull UUID playerId);

    @NotNull CompletableFuture<Void> removeTreasureAsync(@NotNull String treasureId);

    @NotNull CompletableFuture<Void> initializeTreasureAsync(@NotNull String treasureId);

    @NotNull CompletableFuture<Void> clearAllAsync();

    default void shutdown() {}
}
