package com.mongenscave.mctreasure.utils;

import com.mongenscave.mctreasure.McTreasure;
import lombok.experimental.UtilityClass;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

@UtilityClass
public class ItemUtils {

    private static final NamespacedKey MCTREASURE_KEY = new NamespacedKey(McTreasure.getInstance(), "mcTreasure");

    /**
     * Csak a saját tracking adatunkat távolítja el, minden mást érintetlenül hagy
     */
    public @NotNull ItemStack removeTrackingData(@NotNull ItemStack item) {
        if (item == null || !hasTrackingData(item)) {
            return item.clone(); // Ha nincs tracking data, egyszerűen klónozzuk
        }

        ItemStack cleanItem = item.clone();

        cleanItem.editMeta(meta -> {
            if (meta != null) {
                PersistentDataContainer pdc = meta.getPersistentDataContainer();
                // Csak a saját kulcsunkat távolítjuk el
                pdc.remove(MCTREASURE_KEY);
            }
        });

        return cleanItem;
    }

    /**
     * Ellenőrzi, hogy az item tartalmazza-e a saját tracking adatunkat
     */
    public boolean hasTrackingData(@NotNull ItemStack item) {
        if (item == null) return false;

        ItemMeta meta = item.getItemMeta();
        if (meta == null) return false;

        PersistentDataContainer pdc = meta.getPersistentDataContainer();
        return pdc.has(MCTREASURE_KEY, PersistentDataType.STRING);
    }

    /**
     * Hozzáadja a tracking adatot egy itemhez
     */
    public @NotNull ItemStack addTrackingData(@NotNull ItemStack item, @NotNull String configPath) {
        ItemStack trackedItem = item.clone();

        trackedItem.editMeta(meta -> {
            if (meta != null) {
                PersistentDataContainer pdc = meta.getPersistentDataContainer();
                pdc.set(MCTREASURE_KEY, PersistentDataType.STRING, configPath);
            }
        });

        return trackedItem;
    }

    /**
     * Biztonságos item klónozás - megőrzi az összes metadatát
     */
    public @NotNull ItemStack safeClone(@NotNull ItemStack item) {
        if (item == null) {
            throw new IllegalArgumentException("Item cannot be null");
        }

        return item.clone();
    }
}