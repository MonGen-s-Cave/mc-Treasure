package net.netherpulse.nptreasure.model;

import eu.decentsoftware.holograms.api.DHAPI;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import net.netherpulse.nptreasure.data.OpenResult;
import net.netherpulse.nptreasure.data.ParticleEffectConfiguration;
import net.netherpulse.nptreasure.identifiers.ParticleTypes;
import net.netherpulse.nptreasure.manager.TreasureManager;
import net.netherpulse.nptreasure.particles.ParticleSystem;
import net.netherpulse.nptreasure.processor.MessageProcessor;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Data
@Builder
public class TreasureChest {
    @Getter private final String id;
    private transient final ConcurrentHashMap<UUID, Long> playerCooldowns = new ConcurrentHashMap<>();
    @Setter
    @Getter private String name;
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

    public void setupHologram() {
        if (!hologramEnabled || location == null || hologramLines == null || hologramLines.isEmpty()) {
            removeHologram();
            return;
        }

        Location holoLoc = location.clone().add(0.5, 1.5 + hologramHeight, 0.5);

        if (hologramId != null) DHAPI.removeHologram(hologramId);

        List<String> processedLines = Collections.synchronizedList(new ArrayList<>());

        for (String line : hologramLines) {
            if (line.equals("%blank%")) processedLines.add("");
            else processedLines.add(line);
        }

        hologramId = "treasure-" + id;
        DHAPI.createHologram(hologramId, holoLoc, processedLines);
    }

    public void removeHologram() {
        if (hologramId != null) {
            DHAPI.removeHologram(hologramId);
            hologramId = null;
        }
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

    public void refreshParticleEffect() {
        removeParticleEffect();

        if (particleEnabled) setupParticleEffect();
    }

    public @NotNull OpenResult canPlayerOpen(@NotNull Player player) {
        if (permission != null && !permission.isEmpty() && !player.hasPermission(permission))
            return new OpenResult(false, MessageProcessor.process("&cYou don't have permission to open this chest!"));

        if (cooldownMillis > 0) {
            long lastOpened = playerCooldowns.getOrDefault(player.getUniqueId(), 0L);
            long currentTime = System.currentTimeMillis();

            if (lastOpened > 0) {
                long remainingTime = (lastOpened + cooldownMillis) - currentTime;

                if (remainingTime > 0) {
                    long remainingSeconds = remainingTime / 1000;
                    return new OpenResult(false, MessageProcessor.process("&cYou must wait &e" + remainingSeconds + " &cseconds before opening this chest again!"));
                }
            }
        }

        return new OpenResult(true, null);
    }

    public void recordPlayerOpen(@NotNull Player player) {
        if (cooldownMillis > 0) {
            playerCooldowns.put(player.getUniqueId(), System.currentTimeMillis());
            TreasureManager.getInstance().saveCooldowns();
        }
    }

    @NotNull
    private ParticleEffectConfiguration createParticleConfig() {
        ParticleEffectConfiguration config = ParticleEffectConfiguration.at(location.clone().add(0.5, 0.5, 0.5));
        config.setParticleType(particleDisplay != null ? particleDisplay : Particle.FLAME);
        config.setDuration(0);
        return config;
    }
}