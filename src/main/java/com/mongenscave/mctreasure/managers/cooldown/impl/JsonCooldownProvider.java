package com.mongenscave.mctreasure.managers.cooldown.impl;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.mongenscave.mctreasure.McTreasure;
import com.mongenscave.mctreasure.api.data.CooldownData;
import com.mongenscave.mctreasure.api.data.CooldownResult;
import com.mongenscave.mctreasure.managers.TreasureManager;
import com.mongenscave.mctreasure.managers.cooldown.CooldownAdapter;
import com.mongenscave.mctreasure.managers.cooldown.CooldownProvider;
import com.mongenscave.mctreasure.utils.LoggerUtils;
import com.mongenscave.mctreasure.utils.TimeUtils;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.lang.reflect.Type;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class JsonCooldownProvider implements CooldownProvider {
    private static final String COOLDOWN_FILE = "cooldowns.json";
    private static final McTreasure plugin = McTreasure.getInstance();

    private final Gson gson;
    private final File cooldownFile;
    private final ReadWriteLock lock = new ReentrantReadWriteLock();
    private final ScheduledExecutorService executor = Executors.newScheduledThreadPool(2);

    private final ConcurrentHashMap<String, ConcurrentHashMap<UUID, CooldownData>> cooldownData = new ConcurrentHashMap<>();

    public JsonCooldownProvider() {
        this.gson = new GsonBuilder()
                .setPrettyPrinting()
                .registerTypeAdapter(CooldownData.class, new CooldownAdapter())
                .create();
        this.cooldownFile = new File(plugin.getDataFolder(), COOLDOWN_FILE);

        loadData();
        LoggerUtils.info("JsonCooldownProvider initialized. Loaded cooldowns for " + cooldownData.size() + " treasures.");
    }

    private void loadData() {
        lock.writeLock().lock();
        try {
            cooldownData.clear();

            if (!cooldownFile.exists()) {
                LoggerUtils.info("Cooldown file does not exist at: " + cooldownFile.getAbsolutePath());
                return;
            }

            try (FileReader reader = new FileReader(cooldownFile)) {
                Type type = new TypeToken<ConcurrentHashMap<String, ConcurrentHashMap<UUID, CooldownData>>>(){}.getType();
                ConcurrentHashMap<String, ConcurrentHashMap<UUID, CooldownData>> loaded = gson.fromJson(reader, type);

                if (loaded != null && !loaded.isEmpty()) {
                    cooldownData.putAll(loaded);
                    LoggerUtils.info("Successfully loaded cooldown data: " + loaded.size() + " treasures");
                } else LoggerUtils.warn("Loaded data is null or empty from JSON file!");
            } catch (Exception exception) {
                LoggerUtils.error("Failed to load cooldown data: " + exception.getMessage());
            }
        } finally {
            lock.writeLock().unlock();
        }
    }

    private void saveDataImmediately() {
        lock.readLock().lock();
        try {
            if (!plugin.getDataFolder().exists()) plugin.getDataFolder().mkdirs();

            LoggerUtils.info("Saving cooldown data immediately to: " + cooldownFile.getAbsolutePath());

            try (FileWriter writer = new FileWriter(cooldownFile)) {
                gson.toJson(cooldownData, writer);
                writer.flush();

            } catch (Exception exception) {
                LoggerUtils.error("Failed to save cooldown data: " + exception.getMessage());
            }
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public @NotNull CompletableFuture<CooldownResult> checkCooldownAsync(@NotNull String treasureId, @NotNull UUID playerId, long cooldownMillis) {
        return CompletableFuture.supplyAsync(() -> {
            if (cooldownMillis <= 0) return new CooldownResult(true, null, 0);

            lock.readLock().lock();
            try {
                ConcurrentHashMap<UUID, CooldownData> treasureCooldowns = cooldownData.get(treasureId);
                if (treasureCooldowns == null) return new CooldownResult(true, null, 0);

                CooldownData data = treasureCooldowns.get(playerId);
                if (data == null || data.lastOpenedTimestamp() == 0) return new CooldownResult(true, null, 0);

                long currentTime = System.currentTimeMillis();
                long remainingTime = (data.lastOpenedTimestamp() + data.cooldownMillis()) - currentTime;

                if (remainingTime <= 0) return new CooldownResult(true, null, 0);

                String formattedTime = TimeUtils.formatTime(remainingTime);
                return new CooldownResult(false, formattedTime, remainingTime);
            } finally {
                lock.readLock().unlock();
            }
        }, executor);
    }

    @Override
    public @NotNull CompletableFuture<Void> recordOpenAsync(@NotNull String treasureId, @NotNull UUID playerId) {
        return recordOpenWithCooldownAsync(treasureId, playerId, getCooldownForTreasure(treasureId));
    }

    public @NotNull CompletableFuture<Void> recordOpenWithCooldownAsync(@NotNull String treasureId, @NotNull UUID playerId, long cooldownMillis) {
        return CompletableFuture.runAsync(() -> {
            lock.writeLock().lock();
            try {
                long currentTime = System.currentTimeMillis();
                CooldownData data = new CooldownData(currentTime, cooldownMillis);

                cooldownData.computeIfAbsent(treasureId, k -> new ConcurrentHashMap<>()).put(playerId, data);

                saveDataImmediately();

            } finally {
                lock.writeLock().unlock();
            }
        }, executor);
    }

    @Override
    public @NotNull CompletableFuture<Void> removeTreasureAsync(@NotNull String treasureId) {
        return CompletableFuture.runAsync(() -> {
            lock.writeLock().lock();
            try {
                if (cooldownData.remove(treasureId) != null) {
                    LoggerUtils.info("Removed cooldown data for treasure: " + treasureId);
                    saveDataImmediately();
                }
            } finally {
                lock.writeLock().unlock();
            }
        }, executor);
    }

    @Override
    public @NotNull CompletableFuture<Void> initializeTreasureAsync(@NotNull String treasureId) {
        return CompletableFuture.runAsync(() -> {
            lock.writeLock().lock();
            try {
                if (!cooldownData.containsKey(treasureId)) {
                    cooldownData.put(treasureId, new ConcurrentHashMap<>());
                    LoggerUtils.info("Initialized empty cooldown data for treasure: " + treasureId);
                    saveDataImmediately();
                }
            } finally {
                lock.writeLock().unlock();
            }
        }, executor);
    }

    @Override
    public @NotNull CompletableFuture<Void> clearAllAsync() {
        return CompletableFuture.runAsync(() -> {
            lock.writeLock().lock();
            try {
                cooldownData.clear();
                LoggerUtils.info("Cleared all cooldown data.");
                saveDataImmediately();
            } finally {
                lock.writeLock().unlock();
            }
        }, executor);
    }

    @Override
    public void shutdown() {
        LoggerUtils.info("Shutting down JsonCooldownProvider...");

        LoggerUtils.info("Final save before shutdown...");
        saveDataImmediately();

        executor.shutdown();
        try {
            if (!executor.awaitTermination(10, java.util.concurrent.TimeUnit.SECONDS)) {
                LoggerUtils.warn("Executor did not terminate gracefully, forcing shutdown...");
                executor.shutdownNow();
            }
        } catch (InterruptedException exception) {
            LoggerUtils.warn("Interrupted while waiting for executor termination.");
            executor.shutdownNow();
            Thread.currentThread().interrupt();
        }

        LoggerUtils.info("JsonCooldownProvider shutdown complete.");
    }

    private long getCooldownForTreasure(@NotNull String treasureId) {
        var treasureManager = TreasureManager.getInstance();

        if (treasureManager != null) {
            var treasure = treasureManager.getTreasure(treasureId);
            if (treasure != null) return treasure.getCooldownMillis();
        }
        return 300000L;
    }
}