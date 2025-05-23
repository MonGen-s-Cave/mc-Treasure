package net.netherpulse.nptreasure.data;

import lombok.Data;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Data
public class ParticleEffectConfiguration {
    private Location location;
    private Player player;
    private boolean visibleToAll = true;
    private int duration = 0;

    private Particle particleType = Particle.FLAME;
    private int particleCount = 1;
    private double particleSpeed = 0;
    private Color particleColor = Color.RED;
    private float particleSize = 1.0f;

    private double radius = 1.0;
    private double height = 2.0;
    private double rotationSpeed = 1.0;
    private double expansionRate = 0.1;
    private int density = 10;

    @NotNull
    public static ParticleEffectConfiguration at(@Nullable Location location) {
        ParticleEffectConfiguration config = new ParticleEffectConfiguration();
        config.setLocation(location);
        return config;
    }

    public ParticleEffectConfiguration visibleTo(Player player) {
        this.player = player;
        this.visibleToAll = false;
        return this;
    }

    public ParticleEffectConfiguration visibleToAll() {
        this.visibleToAll = true;
        this.player = null;
        return this;
    }

    public ParticleEffectConfiguration particle(Particle type) {
        this.particleType = type;
        return this;
    }

    public ParticleEffectConfiguration color(Color color) {
        this.particleColor = color;
        return this;
    }

    public ParticleEffectConfiguration size(float size) {
        this.particleSize = size;
        return this;
    }

    public void copyFrom(ParticleEffectConfiguration other) {
        if (other == null) return;

        this.location = other.location == null ? null : other.location.clone();
        this.player = other.player;
        this.visibleToAll = other.visibleToAll;
        this.duration = other.duration;

        this.particleType = other.particleType;
        this.particleCount = other.particleCount;
        this.particleSpeed = other.particleSpeed;
        this.particleColor = other.particleColor;
        this.particleSize = other.particleSize;

        this.radius = other.radius;
        this.height = other.height;
        this.rotationSpeed = other.rotationSpeed;
        this.expansionRate = other.expansionRate;
        this.density = other.density;
    }
}
