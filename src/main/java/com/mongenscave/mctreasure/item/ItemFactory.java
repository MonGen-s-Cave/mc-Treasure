package com.mongenscave.mctreasure.item;

import com.mongenscave.mctreasure.McTreasure;
import com.mongenscave.mctreasure.config.Config;
import com.mongenscave.mctreasure.processor.MessageProcessor;
import com.mongenscave.mctreasure.utils.LoggerUtils;
import dev.dejvokep.boostedyaml.block.implementation.Section;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@SuppressWarnings("deprecation")
public interface ItemFactory {
    @Contract("_ -> new")
    static @NotNull ItemFactory create(@NotNull Material material) {
        return new ItemBuilder(material);
    }

    @Contract("_, _ -> new")
    static @NotNull ItemFactory create(@NotNull Material material, int count) {
        return new ItemBuilder(material, count);
    }

    @Contract("_, _, _ -> new")
    static @NotNull ItemFactory create(@NotNull Material material, int count, short damage) {
        return new ItemBuilder(material, count, damage);
    }

    @Contract("_ -> new")
    static @NotNull ItemFactory create(ItemStack item) {
        return new ItemBuilder(item);
    }

    static Optional<ItemStack> buildItem(@NotNull Section section, @NotNull String configPath) {
        try {
            String materialName = section.getString("material");
            if (materialName == null || materialName.isEmpty()) return Optional.empty();

            Material material;
            try {
                material = Material.valueOf(materialName.toUpperCase());
            } catch (IllegalArgumentException exception) {
                return Optional.empty();
            }

            int amount = section.getInt("amount", 1);
            amount = Math.max(1, Math.min(amount, 64));

            String rawName = section.getString("name", "");
            String processedName = rawName.isEmpty() ? "" : MessageProcessor.process(rawName);

            List<String> lore = section.getStringList("lore").stream()
                    .map(MessageProcessor::process)
                    .toList();

            ItemStack item = ItemFactory.create(material, amount)
                    .setName(processedName)
                    .addLore(lore.toArray(new String[0]))
                    .finish();

            List<String> enchantmentStrings = section.getStringList("enchantments");
            for (String enchantmentString : enchantmentStrings) {
                String[] parts = enchantmentString.split(":");
                if (parts.length == 2) {
                    try {
                        Enchantment enchantment = Enchantment.getByKey(NamespacedKey.minecraft(parts[0].toLowerCase()));
                        if (enchantment != null) {
                            int level = Integer.parseInt(parts[1]);
                            item.addUnsafeEnchantment(enchantment, level);
                        }
                    } catch (Exception ignored) {
                    }
                }
            }

            boolean unbreakable = section.getBoolean("unbreakable", false);
            if (unbreakable) item.editMeta(meta -> meta.setUnbreakable(true));

            item.editMeta(meta -> {
                PersistentDataContainer pdc = meta.getPersistentDataContainer();
                NamespacedKey key = new NamespacedKey(McTreasure.getInstance(), "mcTreasure");
                pdc.set(key, PersistentDataType.STRING, configPath);
            });

            return Optional.of(item);
        } catch (Exception exception) {
            return Optional.empty();
        }
    }

    static Optional<ItemStack> createItemFromString(@NotNull String path, @NotNull Config config) {
        Section section = config.getSection(path);
        return section != null ? buildItem(section, path) : Optional.empty();
    }

    static void serializeItem(@NotNull ItemStack item, @NotNull Config config, @NotNull String configPath) {
        try {
            if (item.getType() == Material.AIR) {
                LoggerUtils.warn("Trying to serialize null or AIR item at path: " + configPath);
                return;
            }

            LoggerUtils.info("Serializing item at path: " + configPath + ", type: " + item.getType().name());

            config.set(configPath, null);
            config.set(configPath + ".material", item.getType().name());
            config.set(configPath + ".amount", item.getAmount());

            ItemMeta meta = item.getItemMeta();
            if (meta != null) {
                if (meta.hasDisplayName()) config.set(configPath + ".name", meta.getDisplayName());
                else config.set(configPath + ".name", "");

                if (meta.hasLore() && meta.getLore() != null) config.set(configPath + ".lore", meta.getLore());
                else config.set(configPath + ".lore", new ArrayList<String>());

                config.set(configPath + ".unbreakable", meta.isUnbreakable());

                List<String> enchantments = new ArrayList<>();
                for (Map.Entry<Enchantment, Integer> entry : item.getEnchantments().entrySet()) {
                    Enchantment enchantment = entry.getKey();
                    NamespacedKey key = enchantment.getKey();
                    enchantments.add(key.getKey() + ":" + entry.getValue());
                }
                config.set(configPath + ".enchantments", enchantments);

                if (!meta.getItemFlags().isEmpty()) {
                    List<String> flags = Collections.synchronizedList(new ArrayList<>());

                    for (ItemFlag flag : meta.getItemFlags()) {
                        flags.add(flag.name());
                    }

                    config.set(configPath + ".item_flags", flags);
                } else config.set(configPath + ".item_flags", new ArrayList<String>());
            }

        } catch (Exception exception) {
            LoggerUtils.error("Failed to serialize item at path " + configPath + ": " + exception.getMessage());
        }
    }

    static String getPathFromItem(@Nullable ItemStack item) {
        if (item == null || !item.hasItemMeta()) return null;
        PersistentDataContainer container = item.getItemMeta().getPersistentDataContainer();
        NamespacedKey key = new NamespacedKey(McTreasure.getInstance(), "mcTreasure");
        return container.get(key, PersistentDataType.STRING);
    }

    ItemFactory setType(@NotNull Material material);

    ItemFactory setCount(int newCount);

    ItemFactory setName(@NotNull String name);

    ItemBuilder setLore(@NotNull List<String> lore);

    void addEnchantment(@NotNull Enchantment enchantment, int level);

    default ItemFactory addEnchantments(@NotNull ConcurrentHashMap<Enchantment, Integer> enchantments) {
        enchantments.forEach(this::addEnchantment);
        return this;
    }

    ItemBuilder addLore(@NotNull String... lores);

    ItemFactory setUnbreakable();

    default void addFlag(@NotNull ItemFlag... flags) {
        Arrays.stream(flags).forEach(this::addFlag);
    }

    ItemFactory removeLore(int line);

    ItemStack finish();

    boolean isFinished();
}
