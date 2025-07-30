package com.mongenscave.mctreasure.managers.cooldown;

import com.mongenscave.mctreasure.api.data.CooldownResult;
import com.mongenscave.mctreasure.identifiers.keys.ConfigKeys;
import com.mongenscave.mctreasure.managers.cooldown.impl.JsonCooldownProvider;
import com.mongenscave.mctreasure.managers.cooldown.impl.MemoryCooldownProvider;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class CooldownManager {
    private static CooldownManager instance;
    private CooldownProvider provider;

    private CooldownManager() {}

    public static CooldownManager getInstance() {
        if (instance == null) instance = new CooldownManager();
        return instance;
    }

    public void initialize() {
        if (provider != null) provider.shutdown();

        if (ConfigKeys.COOLDOWN_STORAGE.getString().equalsIgnoreCase("JSON")) provider = new JsonCooldownProvider();
        else provider = new MemoryCooldownProvider();
    }

    public @NotNull CompletableFuture<CooldownResult> checkCooldownAsync(@NotNull String treasureId, @NotNull UUID playerId, long cooldownMillis) {
        return provider.checkCooldownAsync(treasureId, playerId, cooldownMillis);
    }

    public @NotNull CompletableFuture<Void> recordOpenAsync(@NotNull String treasureId, @NotNull UUID playerId) {
        if (provider instanceof JsonCooldownProvider jsonProvider) {
            long cooldownMillis = getCooldownForTreasure(treasureId);
            return jsonProvider.recordOpenWithCooldownAsync(treasureId, playerId, cooldownMillis);
        } else return provider.recordOpenAsync(treasureId, playerId);
    }

    public @NotNull CompletableFuture<Void> removeTreasureAsync(@NotNull String treasureId) {
        return provider.removeTreasureAsync(treasureId);
    }

    public @NotNull CompletableFuture<Void> initializeTreasureAsync(@NotNull String treasureId) {
        return provider.initializeTreasureAsync(treasureId);
    }

    public @NotNull CompletableFuture<Void> clearAllAsync() {
        return provider.clearAllAsync();
    }

    public @NotNull CooldownResult checkCooldown(@NotNull String treasureId, @NotNull UUID playerId, long cooldownMillis) {
        return checkCooldownAsync(treasureId, playerId, cooldownMillis).join();
    }

    public void recordOpen(@NotNull String treasureId, @NotNull UUID playerId) {
        recordOpenAsync(treasureId, playerId).join();
    }

    public void removeTreasure(@NotNull String treasureId) {
        removeTreasureAsync(treasureId).join();
    }

    public void initializeTreasure(@NotNull String treasureId) {
        initializeTreasureAsync(treasureId).join();
    }

    public void shutdown() {
        if (provider != null) provider.shutdown();
    }

    private long getCooldownForTreasure(@NotNull String treasureId) {
        var treasureManager = com.mongenscave.mctreasure.managers.TreasureManager.getInstance();
        if (treasureManager != null) {
            var treasure = treasureManager.getTreasure(treasureId);
            if (treasure != null) return treasure.getCooldownMillis();
        }

        return 300000L;
    }
}