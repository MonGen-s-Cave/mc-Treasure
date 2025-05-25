package com.mongenscave.mctreasure.managers;

import com.mongenscave.mctreasure.data.CooldownResult;
import com.mongenscave.mctreasure.utils.TimeUtils;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class CooldownManager {
    private static CooldownManager instance;

    private final ConcurrentHashMap<String, ConcurrentHashMap<UUID, Long>> cooldownData = new ConcurrentHashMap<>();

    private CooldownManager() {}

    public static CooldownManager getInstance() {
        if (instance == null) {
            instance = new CooldownManager();
        }
        return instance;
    }

    public @NotNull CooldownResult checkCooldown(@NotNull String treasureId, @NotNull UUID playerId, long cooldownMillis) {
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
    }

    public void recordOpen(@NotNull String treasureId, @NotNull UUID playerId) {
        cooldownData.computeIfAbsent(treasureId, k -> new ConcurrentHashMap<>())
                .put(playerId, System.currentTimeMillis());
    }

    public void removeTreasure(@NotNull String treasureId) {
        cooldownData.remove(treasureId);
    }

    public void initializeTreasure(@NotNull String treasureId) {
        cooldownData.computeIfAbsent(treasureId, k -> new ConcurrentHashMap<>());
    }

    public long getShortestRemainingCooldown(@NotNull String treasureId, long cooldownMillis) {
        if (cooldownMillis <= 0) return 0;

        ConcurrentHashMap<UUID, Long> treasureCooldowns = cooldownData.get(treasureId);
        if (treasureCooldowns == null || treasureCooldowns.isEmpty()) return 0;

        long currentTime = System.currentTimeMillis();
        long shortestRemaining = Long.MAX_VALUE;
        boolean hasActiveCooldown = false;

        for (Long lastOpened : treasureCooldowns.values()) {
            if (lastOpened != null && lastOpened > 0) {
                long remainingTime = (lastOpened + cooldownMillis) - currentTime;
                if (remainingTime > 0) {
                    hasActiveCooldown = true;
                    shortestRemaining = Math.min(shortestRemaining, remainingTime);
                }
            }
        }

        return hasActiveCooldown ? shortestRemaining : 0;
    }

    public void clearAll() {
        cooldownData.clear();
    }

    public @NotNull ConcurrentHashMap<String, ConcurrentHashMap<UUID, Long>> getAllCooldownData() {
        return cooldownData;
    }
}
