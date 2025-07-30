package com.mongenscave.mctreasure.managers.cooldown.impl;

import com.mongenscave.mctreasure.api.data.CooldownResult;
import com.mongenscave.mctreasure.managers.cooldown.CooldownProvider;
import com.mongenscave.mctreasure.utils.TimeUtils;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

public class MemoryCooldownProvider implements CooldownProvider {

    private final ConcurrentHashMap<String, ConcurrentHashMap<UUID, Long>> cooldownData = new ConcurrentHashMap<>();

    @Override
    public @NotNull CompletableFuture<CooldownResult> checkCooldownAsync(@NotNull String treasureId, @NotNull UUID playerId, long cooldownMillis) {
        return CompletableFuture.supplyAsync(() -> {
            if (cooldownMillis <= 0) return new CooldownResult(true, null, 0);

            ConcurrentHashMap<UUID, Long> treasureCooldowns = cooldownData.get(treasureId);
            if (treasureCooldowns == null) return new CooldownResult(true, null, 0);

            Long lastOpened = treasureCooldowns.get(playerId);
            if (lastOpened == null || lastOpened == 0) return new CooldownResult(true, null, 0);

            long currentTime = System.currentTimeMillis();
            long remainingTime = (lastOpened + cooldownMillis) - currentTime;

            if (remainingTime <= 0) return new CooldownResult(true, null, 0);

            String formattedTime = TimeUtils.formatTime(remainingTime);
            return new CooldownResult(false, formattedTime, remainingTime);
        });
    }

    @Override
    public @NotNull CompletableFuture<Void> recordOpenAsync(@NotNull String treasureId, @NotNull UUID playerId) {
        return CompletableFuture.runAsync(() -> {
            cooldownData.computeIfAbsent(treasureId, k -> new ConcurrentHashMap<>())
                    .put(playerId, System.currentTimeMillis());
        });
    }

    @Override
    public @NotNull CompletableFuture<Void> removeTreasureAsync(@NotNull String treasureId) {
        return CompletableFuture.runAsync(() -> cooldownData.remove(treasureId));
    }

    @Override
    public @NotNull CompletableFuture<Void> initializeTreasureAsync(@NotNull String treasureId) {
        return CompletableFuture.runAsync(() ->
                cooldownData.computeIfAbsent(treasureId, k -> new ConcurrentHashMap<>()));
    }

    @Override
    public @NotNull CompletableFuture<Void> clearAllAsync() {
        return CompletableFuture.runAsync(cooldownData::clear);
    }

    @Override
    public void shutdown() {
        cooldownData.clear();
    }
}