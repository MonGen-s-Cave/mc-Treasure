package com.mongenscave.mctreasure.model;

import com.github.Anon8281.universalScheduler.scheduling.tasks.MyScheduledTask;
import com.mongenscave.mctreasure.McTreasure;
import com.mongenscave.mctreasure.api.model.ITreasureChest;
import com.mongenscave.mctreasure.api.data.CooldownResult;
import com.mongenscave.mctreasure.api.data.OpenResult;
import com.mongenscave.mctreasure.data.ParticleEffectConfiguration;
import com.mongenscave.mctreasure.identifiers.ParticleTypes;
import com.mongenscave.mctreasure.identifiers.keys.MessageKeys;
import com.mongenscave.mctreasure.identifiers.keys.PlaceholderKeys;
import com.mongenscave.mctreasure.managers.CooldownManager;
import com.mongenscave.mctreasure.managers.TreasureManager;
import com.mongenscave.mctreasure.particles.ParticleSystem;
import com.mongenscave.mctreasure.utils.TimeUtils;
import eu.decentsoftware.holograms.api.DHAPI;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Data
@Builder
public class TreasureChest implements ITreasureChest {
    @Getter private final String id;
    @Setter @Getter private String name;
    @Setter @Getter private Location location;
    @Setter @Getter private boolean pushbackEnabled;
    @Setter @Getter private double pushbackStrength;
    @Setter @Getter private boolean hologramEnabled;
    @Setter @Getter private List<String> hologramLines;
    @Setter @Getter private double hologramHeight;
    @Setter @Getter private long cooldownMillis;
    @Setter @Getter private String permission;
    @Setter @Getter private int size;
    @Setter @Getter private List<ItemStack> items;
    @Getter @Setter private ParticleTypes particleType;
    @Getter @Setter private Particle particleDisplay;
    @Getter @Setter private boolean particleEnabled;

    private transient UUID particleEffectId;
    private transient String hologramId;
    private transient MyScheduledTask hologramUpdateTask;

    public void setupHologram() {
        if (!hologramEnabled || location == null || hologramLines == null || hologramLines.isEmpty()) {
            removeHologram();
            return;
        }

        Location holoLoc = location.clone().add(0.5, 1.5 + hologramHeight, 0.5);

        if (hologramId != null) DHAPI.removeHologram(hologramId);

        List<String> processedLines = processHologramLines();

        hologramId = "treasure-" + id;
        DHAPI.createHologram(hologramId, holoLoc, processedLines);

        if (hasTimeLeftPlaceholder()) startHologramUpdateTask();
    }

    public void removeHologram() {
        if (hologramId != null) {
            DHAPI.removeHologram(hologramId);
            hologramId = null;
        }

        stopHologramUpdateTask();
    }

    public void setupParticleEffect() {
        if (!particleEnabled || location == null) {
            removeParticleEffect();
            return;
        }

        if (particleEffectId != null) removeParticleEffect();

        this.particleDisplay = TreasureManager.getInstance().getParticleFromConfig(particleType);

        ParticleEffectConfiguration config = createParticleConfig();
        particleEffectId = ParticleSystem.getInstance().startEffect(particleType, config);
    }

    public void removeParticleEffect() {
        if (particleEffectId != null) {
            ParticleSystem.getInstance().stopEffect(particleEffectId);
            particleEffectId = null;
        }
    }

    public @NotNull OpenResult canPlayerOpen(@NotNull Player player) {
        CooldownResult result = CooldownManager.getInstance()
                .checkCooldown(id, player.getUniqueId(), cooldownMillis);

        if (result.canOpen()) return new OpenResult(true, null);
        else {
            String message = null;

            if (result.formattedTime() != null) message = MessageKeys.COOLDOWN.getMessage().replace("{time}", result.formattedTime());
            return new OpenResult(false, message);
        }
    }

    public void recordPlayerOpen(@NotNull Player player) {
        CooldownManager.getInstance().recordOpen(id, player.getUniqueId());
    }

    private boolean hasTimeLeftPlaceholder() {
        String placeholderPattern = "%mctreasure_time_left_" + id + "%";
        return hologramLines.stream().anyMatch(line -> line.contains(placeholderPattern));
    }

    private void startHologramUpdateTask() {
        stopHologramUpdateTask();

        if (hasTimeLeftPlaceholder()) {
            hologramUpdateTask = McTreasure.getInstance().getScheduler().runTaskTimerAsynchronously(() -> {
                if (hologramEnabled && hologramId != null) setupHologram();
            }, 20L, 20L);
        }
    }

    private void stopHologramUpdateTask() {
        if (hologramUpdateTask != null && !hologramUpdateTask.isCancelled()) {
            hologramUpdateTask.cancel();
            hologramUpdateTask = null;
        }
    }

    @NotNull
    private ParticleEffectConfiguration createParticleConfig() {
        ParticleEffectConfiguration config = ParticleEffectConfiguration.at(location.clone().add(0.5, 0.5, 0.5));

        config.setParticleType(particleDisplay != null ? particleDisplay : Particle.FLAME);
        config.setDuration(0);
        return config;
    }

    @NotNull
    private List<String> processHologramLines() {
        List<String> processedLines = Collections.synchronizedList(new ArrayList<>());

        for (String line : hologramLines) {
            if (line.equals("%blank%")) processedLines.add("");
            else processedLines.add(line);
        }

        return processedLines;
    }

    public String getTimeLeftDisplay(@NotNull Player player) {
        if (cooldownMillis <= 0) return PlaceholderKeys.HOLOGRAM_READY.getString();

        CooldownResult result = CooldownManager.getInstance()
                .checkCooldown(id, player.getUniqueId(), cooldownMillis);

        if (result.canOpen()) return PlaceholderKeys.HOLOGRAM_READY.getString();

        String timeLeftFormat = PlaceholderKeys.HOLOGRAM_TIME_LEFT.getString();
        return String.format(timeLeftFormat, result.formattedTime());
    }
}