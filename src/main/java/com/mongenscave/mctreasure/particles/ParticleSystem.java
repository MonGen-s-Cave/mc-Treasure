package com.mongenscave.mctreasure.particles;

import com.github.Anon8281.universalScheduler.scheduling.tasks.MyScheduledTask;
import com.mongenscave.mctreasure.McTreasure;
import com.mongenscave.mctreasure.data.ParticleEffectConfiguration;
import com.mongenscave.mctreasure.identifiers.ParticleTypes;
import com.mongenscave.mctreasure.particles.models.ParticleEffect;
import com.mongenscave.mctreasure.particles.models.impl.Heart;
import com.mongenscave.mctreasure.particles.models.impl.Helix;
import com.mongenscave.mctreasure.particles.models.impl.Phoenix;
import com.mongenscave.mctreasure.particles.models.impl.Pulsar;
import com.mongenscave.mctreasure.particles.models.impl.Sphere;
import com.mongenscave.mctreasure.particles.models.impl.Spiral;
import com.mongenscave.mctreasure.particles.models.impl.Tornado;
import com.mongenscave.mctreasure.utils.LoggerUtils;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class ParticleSystem {
    private static ParticleSystem instance;
    private final McTreasure plugin = McTreasure.getInstance();

    @Getter private final ConcurrentHashMap<ParticleTypes, ParticleEffect> registeredEffects;
    private final ConcurrentHashMap<UUID, ParticleEffect> activeEffects;
    private boolean isRunning;
    private MyScheduledTask taskRunner;

    public ParticleSystem() {
        this.registeredEffects = new ConcurrentHashMap<>();
        this.activeEffects = new ConcurrentHashMap<>();
        this.isRunning = false;

        registerDefaultEffects();
        start();
    }

    public static ParticleSystem getInstance() {
        if (instance == null) instance = new ParticleSystem();
        return instance;
    }

    public void reload() {
        stopAllEffects();
        registeredEffects.clear();
        registerDefaultEffects();
        start();
    }

    public void shutdown() {
        stopAllEffects();
        isRunning = false;
        instance = null;
    }

    public void registerEffect(@NotNull ParticleTypes type, ParticleEffect effect) {
        registeredEffects.put(type, effect);
    }

    public ParticleEffect getEffect(@NotNull ParticleTypes types) {
        return registeredEffects.get(types);
    }

    public UUID startEffect(ParticleTypes types, ParticleEffectConfiguration config) {
        ParticleEffect effect = getEffect(types);

        if (effect == null) {
            LoggerUtils.warn("Tried to start unknown particle effect: " + types.name());
            return null;
        }

        if (config == null) {
            LoggerUtils.warn("Particle effect configuration cannot be null");
            return null;
        }

        ParticleEffect clonedEffect = effect.clone();
        clonedEffect.setConfig(config);

        UUID effectUUID = UUID.randomUUID();
        activeEffects.put(effectUUID, clonedEffect);

        if (!isRunning) start();

        return effectUUID;
    }

    public void stopEffect(UUID effectId) {
        if (effectId == null) return;
        activeEffects.remove(effectId);

        if (activeEffects.isEmpty() && isRunning) stop();
    }

    public void stopAllEffects() {
        activeEffects.clear();
        stop();
    }

    private void start() {
        if (isRunning) return;

        taskRunner = plugin.getScheduler().runTaskTimer(this::tick, 0L, 1L);
        isRunning = true;
    }

    private void stop() {
        if (!isRunning) return;

        if (taskRunner != null) {
            taskRunner.cancel();
            taskRunner = null;
        }

        isRunning = false;
    }

    private void tick() {
        List<UUID> toRemove = Collections.synchronizedList(new ArrayList<>());

        for (ConcurrentHashMap.Entry<UUID, ParticleEffect> entry : activeEffects.entrySet()) {
            UUID effectId = entry.getKey();
            ParticleEffect effect = entry.getValue();

            if (effect == null) {
                toRemove.add(effectId);
                continue;
            }

            try {
                if (effect.isComplete()) {
                    toRemove.add(effectId);
                    continue;
                }

                effect.update();
                effect.display();
            } catch (Exception exception) {
                LoggerUtils.error("Error updating particle effect: " + exception.getMessage());
                toRemove.add(effectId);
            }
        }

        for (UUID uuid : toRemove) {
            activeEffects.remove(uuid);
        }

        if (activeEffects.isEmpty() && isRunning) stop();
    }

    private void registerDefaultEffects() {
        registerEffect(ParticleTypes.HEART, new Heart());
        registerEffect(ParticleTypes.HELIX, new Helix());
        registerEffect(ParticleTypes.PULSAR, new Pulsar());
        registerEffect(ParticleTypes.TORNADO, new Tornado());
        registerEffect(ParticleTypes.SPHERE, new Sphere());
        registerEffect(ParticleTypes.SPIRAL, new Spiral());
        registerEffect(ParticleTypes.PHOENIX, new Phoenix());

        LoggerUtils.info("Registered " + registeredEffects.size() + " particle effects.");
    }
}