package com.mongenscave.mctreasure.manager;

import com.artillexstudios.axapi.config.Config;
import com.artillexstudios.axapi.libs.boostedyaml.boostedyaml.block.implementation.Section;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.mongenscave.mctreasure.McTreasure;
import com.mongenscave.mctreasure.identifiers.ParticleTypes;
import com.mongenscave.mctreasure.item.ItemFactory;
import com.mongenscave.mctreasure.model.TreasureChest;
import com.mongenscave.mctreasure.utils.LocationUtils;
import com.mongenscave.mctreasure.utils.LoggerUtils;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@SuppressWarnings("all")
public class TreasureManager {
    private static final McTreasure plugin = McTreasure.getInstance();
    private static TreasureManager instance;
    private final ConcurrentHashMap<String, TreasureChest> treasureChests;
    private final Gson gson;
    @Getter
    private final ConcurrentHashMap<String, ConcurrentHashMap<UUID, Long>> allPlayerCooldowns = new ConcurrentHashMap<>();
    private final File cooldownsFile;

    private TreasureManager() {
        this.treasureChests = new ConcurrentHashMap<>();
        this.gson = new GsonBuilder().setPrettyPrinting().create();
        this.cooldownsFile = new File(plugin.getDataFolder(), "cooldowns.json");

        loadTreasures();
        loadCooldowns();
    }

    public static TreasureManager getInstance() {
        if (instance == null) instance = new TreasureManager();
        return instance;
    }

    public TreasureChest createTreasure(@NotNull String id) {
        TreasureChest chest = TreasureChest.builder()
                .id(id)
                .name("&6&lTreasure Chest")
                .pushbackEnabled(true)
                .pushbackStrength(1.0)
                .hologramEnabled(true)
                .hologramLines(Collections.synchronizedList(new ArrayList<>(List.of("&6&lTreasure Chest", "&7Open me!"))))
                .cooldownMillis(0)
                .permission("")
                .size(27)
                .items(Collections.synchronizedList(new ArrayList<>()))
                .particleEnabled(false)
                .particleType(ParticleTypes.HELIX)
                .particleDisplay(getParticleFromConfig(ParticleTypes.HELIX))
                .build();

        treasureChests.put(id, chest);
        return chest;
    }

    public @Nullable TreasureChest getTreasure(@NotNull String id) {
        return treasureChests.get(id);
    }

    public @NotNull List<TreasureChest> getAllTreasures() {
        return new ArrayList<>(treasureChests.values());
    }

    public boolean deleteTreasure(@NotNull String id) {
        TreasureChest chest = treasureChests.remove(id);

        if (chest != null) {
            chest.removeHologram();
            chest.removeParticleEffect();
            saveTreasures();
            return true;
        }

        return false;
    }

    public Particle getParticleFromConfig(@NotNull ParticleTypes type) {
        Config config = plugin.getConfiguration();
        String particleName = config.getString("effect-particles." + type.name().toLowerCase(), "FLAME");

        try {
            return Particle.valueOf(particleName.toUpperCase());
        } catch (IllegalArgumentException exception) {
            LoggerUtils.warn("Invalid particle type in config: " + particleName + ", using FLAME as default");
            return Particle.FLAME;
        }
    }

    public void loadTreasures() {
        Config treasuresConfig = plugin.getTreasures();

        treasureChests.values().forEach(chest -> {
            chest.removeHologram();
            chest.removeParticleEffect();
        });

        treasureChests.clear();

        Section treasuresSection = treasuresConfig.getSection("treasures");
        if (treasuresSection == null) return;

        for (String id : treasuresSection.getRoutesAsStrings(false)) {
            try {
                Section chestSection = treasuresConfig.getSection("treasures." + id);
                if (chestSection == null) continue;

                String name = chestSection.getString("name", "&6&lTreasure Chest");
                String locationStr = chestSection.getString("location");
                Location location = locationStr != null ? LocationUtils.deserialize(locationStr) : null;

                boolean pushbackEnabled = chestSection.getBoolean("pushback.enabled", true);
                double pushbackStrength = chestSection.getDouble("pushback.strength", 1.0);

                boolean hologramEnabled = chestSection.getBoolean("hologram.enabled", true);
                List<String> hologramLines = chestSection.getStringList("hologram.lines");

                if (hologramLines.isEmpty())
                    hologramLines = Collections.synchronizedList(new ArrayList<>(List.of("&6&lTreasure Chest", "&7Open me!")));

                long cooldownMillis = chestSection.getLong("cooldown", 3600000L);
                String permission = chestSection.getString("permission", "");
                int size = chestSection.getInt("size", 27);

                boolean particleEnabled = chestSection.getBoolean("particle.enabled", false);
                String particleTypeName = chestSection.getString("particle.type", "HELIX");
                ParticleTypes particleType;
                try {
                    particleType = ParticleTypes.valueOf(particleTypeName.toUpperCase());
                } catch (IllegalArgumentException exception) {
                    particleType = ParticleTypes.HELIX;
                }

                List<ItemStack> items = Collections.synchronizedList(new ArrayList<>());
                Section itemsSection = chestSection.getSection("items");

                if (itemsSection != null) {
                    for (String itemKey : itemsSection.getRoutesAsStrings(false)) {
                        Section itemSection = itemsSection.getSection(itemKey);

                        if (itemSection != null) {
                            ItemFactory.buildItem(itemSection, "treasures." + id + ".items." + itemKey)
                                    .ifPresent(items::add);
                        }
                    }
                }

                TreasureChest chest = TreasureChest.builder()
                        .id(id)
                        .name(name)
                        .location(location)
                        .pushbackEnabled(pushbackEnabled)
                        .pushbackStrength(pushbackStrength)
                        .hologramEnabled(hologramEnabled)
                        .hologramLines(hologramLines)
                        .cooldownMillis(cooldownMillis)
                        .permission(permission)
                        .size(size)
                        .items(items)
                        .particleEnabled(particleEnabled)
                        .particleDisplay(getParticleFromConfig(particleType))
                        .particleType(particleType)
                        .build();

                treasureChests.put(id, chest);

                if (location != null) {
                    if (hologramEnabled) chest.setupHologram();
                    if (particleEnabled) chest.setupParticleEffect();
                }
            } catch (Exception exception) {
                LoggerUtils.error("Failed to load treasure chest with ID: " + id);
                exception.printStackTrace();
            }
        }

        LoggerUtils.info("Loaded " + treasureChests.size() + " treasure chests.");
    }

    public void saveTreasures() {
        try {
            Config treasuresConfig = plugin.getTreasures();
            treasuresConfig.set("treasures", null);

            for (ConcurrentHashMap.Entry<String, TreasureChest> entry : treasureChests.entrySet()) {
                String id = entry.getKey();
                TreasureChest chest = entry.getValue();

                treasuresConfig.set("treasures." + id + ".name", chest.getName());

                if (chest.getLocation() != null)
                    treasuresConfig.set("treasures." + id + ".location", LocationUtils.serialize(chest.getLocation()));

                treasuresConfig.set("treasures." + id + ".pushback.enabled", chest.isPushbackEnabled());
                treasuresConfig.set("treasures." + id + ".pushback.strength", chest.getPushbackStrength());

                treasuresConfig.set("treasures." + id + ".hologram.enabled", chest.isHologramEnabled());
                treasuresConfig.set("treasures." + id + ".hologram.lines", chest.getHologramLines());

                treasuresConfig.set("treasures." + id + ".cooldown", chest.getCooldownMillis());
                treasuresConfig.set("treasures." + id + ".permission", chest.getPermission());
                treasuresConfig.set("treasures." + id + ".size", chest.getSize());

                treasuresConfig.set("treasures." + id + ".particle.enabled", chest.isParticleEnabled());
                treasuresConfig.set("treasures." + id + ".particle.type", chest.getParticleType().name());

                treasuresConfig.set("treasures." + id + ".items", null);

                List<ItemStack> items = chest.getItems();

                if (items != null && !items.isEmpty()) {
                    treasuresConfig.set("treasures." + id + ".items", new HashMap<>());

                    int validItemCount = 0;
                    for (int i = 0; i < items.size(); i++) {
                        ItemStack item = items.get(i);

                        if (item != null && item.getType() != Material.AIR) {
                            validItemCount++;
                            String itemPath = "treasures." + id + ".items." + i;
                            ItemFactory.serializeItem(item, treasuresConfig, itemPath);
                        }
                    }
                }
            }

            treasuresConfig.save();
            LoggerUtils.info("Saved all treasure chests to configuration.");
        } catch (Exception exception) {
            LoggerUtils.error("Error saving treasures: " + exception.getMessage());
        }
    }

    public void applyPushback(@NotNull Player player, @NotNull TreasureChest chest) {
        if (!chest.isPushbackEnabled() || chest.getLocation() == null) return;

        Location chestLoc = chest.getLocation();
        Location playerLoc = player.getLocation();

        Vector direction = playerLoc.toVector().subtract(chestLoc.toVector()).normalize();
        direction.multiply(chest.getPushbackStrength());
        direction.setY(0.3);

        player.setVelocity(direction);
    }

    public void loadCooldowns() {
        if (!cooldownsFile.exists()) {
            saveCooldowns();
            return;
        }

        try (FileReader reader = new FileReader(cooldownsFile)) {
            Type type = new TypeToken<ConcurrentHashMap<String, ConcurrentHashMap<String, Long>>>() {
            }.getType();

            ConcurrentHashMap<String, ConcurrentHashMap<String, Long>> stringCooldowns = gson.fromJson(reader, type);

            if (stringCooldowns != null) {
                for (ConcurrentHashMap.Entry<String, ConcurrentHashMap<String, Long>> entry : stringCooldowns.entrySet()) {
                    String treasureId = entry.getKey();
                    ConcurrentHashMap<String, Long> playerTimestamps = entry.getValue();
                    ConcurrentHashMap<UUID, Long> uuidMap = new ConcurrentHashMap<>();

                    for (ConcurrentHashMap.Entry<String, Long> playerEntry : playerTimestamps.entrySet()) {
                        try {
                            UUID uuid = UUID.fromString(playerEntry.getKey());
                            uuidMap.put(uuid, playerEntry.getValue());
                        } catch (IllegalArgumentException exception) {
                            LoggerUtils.warn("Invalid UUID in cooldowns.json: " + playerEntry.getKey());
                        }
                    }

                    allPlayerCooldowns.put(treasureId, uuidMap);

                    TreasureChest chest = treasureChests.get(treasureId);

                    if (chest != null) {
                        chest.getPlayerCooldowns().clear();
                        chest.getPlayerCooldowns().putAll(uuidMap);
                    }
                }
            }

            LoggerUtils.info("Loaded cooldowns for " + allPlayerCooldowns.size() + " treasure chests.");
        } catch (IOException exception) {
            LoggerUtils.error("Failed to load cooldowns.json");
        }
    }

    public void saveCooldowns() {
        try {
            if (!cooldownsFile.exists()) {
                cooldownsFile.getParentFile().mkdirs();
                cooldownsFile.createNewFile();
            }

            ConcurrentHashMap<String, Map<String, Long>> cooldownsToSave = new ConcurrentHashMap<>();

            for (ConcurrentHashMap.Entry<String, TreasureChest> entry : treasureChests.entrySet()) {
                String id = entry.getKey();
                TreasureChest chest = entry.getValue();

                Map<String, Long> playerCooldowns = chest.getPlayerCooldowns().entrySet().stream()
                        .collect(Collectors.toMap(e -> e.getKey().toString(), Map.Entry::getValue));

                cooldownsToSave.put(id, playerCooldowns);
            }

            try (FileWriter writer = new FileWriter(cooldownsFile)) {
                gson.toJson(cooldownsToSave, writer);
            }
        } catch (IOException exception) {
            LoggerUtils.error("Failed to save cooldowns.json");
        }
    }

    public @Nullable TreasureChest getChestAtLocation(@NotNull Location location) {
        for (TreasureChest chest : treasureChests.values()) {
            if (chest.getLocation() != null && chest.getLocation().equals(location)) return chest;
        }
        return null;
    }
}