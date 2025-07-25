package com.mongenscave.mctreasure.gui.models.main;

import com.mongenscave.mctreasure.data.MenuController;
import com.mongenscave.mctreasure.gui.Menu;
import com.mongenscave.mctreasure.identifiers.keys.ItemKeys;
import com.mongenscave.mctreasure.identifiers.keys.MenuKeys;
import com.mongenscave.mctreasure.identifiers.keys.MessageKeys;
import com.mongenscave.mctreasure.managers.TreasureManager;
import com.mongenscave.mctreasure.model.TreasureChest;
import com.mongenscave.mctreasure.processor.MessageProcessor;
import com.mongenscave.mctreasure.sessions.ChatSessions;
import com.mongenscave.mctreasure.utils.PlaceholderUtils;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@SuppressWarnings("deprecation")
public class TreasureHologramMenu extends Menu {
    private final TreasureChest chest;
    @Getter private final List<String> hologramLines;
    private double hologramHeight;
    private final ConcurrentHashMap<Integer, ItemKeys> slotToItemKeyMap = new ConcurrentHashMap<>();
    private static final NamespacedKey ITEM_KEY = new NamespacedKey("mctreasure", "item_key");

    public TreasureHologramMenu(@NotNull MenuController menuController, @NotNull TreasureChest chest) {
        super(menuController);
        this.chest = chest;
        this.hologramLines = Collections.synchronizedList(new ArrayList<>(chest.getHologramLines()));
        this.hologramHeight = chest.getHologramHeight();
    }

    @Override
    public void handleMenu(final @NotNull InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        ItemStack clickedItem = event.getCurrentItem();
        int slot = event.getSlot();

        if (clickedItem == null || clickedItem.getType() == Material.AIR) return;
        event.setCancelled(true);

        if (slot < hologramLines.size()) {
            switch (event.getClick()) {
                case LEFT -> {
                    player.closeInventory();
                    ChatSessions.editHologramLine(player, slot, this);
                }

                case DROP -> {
                    hologramLines.remove(slot);
                    updateHologramInRealTime();
                    player.playSound(player.getLocation(), Sound.ENTITY_ITEM_BREAK, 0.5f, 1.0f);
                    open();
                }
            }
        }

        ItemKeys clickedItemKey = slotToItemKeyMap.get(event.getSlot());
        if (clickedItemKey == null) return;

        switch (clickedItemKey) {
            case HOLOGRAM_SAVE -> {
                saveHologramChanges();
                player.sendMessage(MessageKeys.SUCCESS_SAVE.getMessage());
                player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 0.5f, 1.0f);
                new TreasureEditMenu(MenuController.getMenuUtils(player), chest).open();
            }

            case HOLOGRAM_CREATE_LINE -> {
                hologramLines.add("%blank%");
                player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 0.5f, 1.0f);
                updateHologramInRealTime();

                open();
            }

            case HOLOGRAM_HEIGHT_ITEM -> {
                switch (event.getClick()) {
                    case LEFT -> hologramHeight += 0.1;
                    case RIGHT -> hologramHeight -= 0.1;
                }

                updateHologramInRealTime();
                open();
            }
        }
    }

    public void updateHologramInRealTime() {
        chest.setHologramLines(new ArrayList<>(hologramLines));
        chest.setHologramHeight(hologramHeight);

        if (chest.getLocation() != null) chest.setupHologram();
    }

    public void saveHologramChanges() {
        chest.setHologramLines(hologramLines);
        chest.setHologramHeight(hologramHeight);
        chest.setupHologram();
        TreasureManager treasureManager = TreasureManager.getInstance();

        if (treasureManager == null) return;

        treasureManager.saveTreasures();
    }

    @Override
    public void setMenuItems() {
        inventory.clear();

        for (int i = 0; i < hologramLines.size() && i < 52; i++) {
            String line = hologramLines.get(i);
            ItemStack lineItem;

            if ("%blank%".equals(line)) lineItem = ItemKeys.HOLOGRAM_BLANK_ITEM.getItem();
            else {
                lineItem = ItemKeys.HOLOGRAM_LINE_ITEM.getItem();
                ItemMeta meta = lineItem.getItemMeta();

                if (meta != null) {
                    meta.setDisplayName(MessageProcessor.process(meta.getDisplayName().replace("{content}", line)));
                    lineItem.setItemMeta(meta);
                }
            }

            inventory.setItem(i, lineItem);
        }

        setMenuItemWithPlaceholders();
        setMenuItem(ItemKeys.HOLOGRAM_CREATE_LINE);
        setMenuItem(ItemKeys.HOLOGRAM_SAVE);
    }

    @Override
    public String getMenuName() {
        return MenuKeys.MENU_EDIT_HOLOGRAM_TITLE.getString();
    }

    @Override
    public int getSlots() {
        return MenuKeys.MENU_EDIT_HOLOGRAM_SIZE.getInt();
    }

    @Override
    public int getMenuTick() {
        return 20;
    }

    private void setMenuItem(@NotNull ItemKeys itemKey) {
        ItemStack item = PlaceholderUtils.applyPlaceholders(itemKey.getItem(), chest);
        int slot = itemKey.getSlot();

        item.editMeta(meta -> meta.getPersistentDataContainer().set(ITEM_KEY, PersistentDataType.STRING, itemKey.name()));

        inventory.setItem(slot, item);
        slotToItemKeyMap.put(slot, itemKey);
    }

    private void setMenuItemWithPlaceholders() {
        ItemStack item = PlaceholderUtils.applyPlaceholders(ItemKeys.HOLOGRAM_HEIGHT_ITEM.getItem(), chest);
        int slot = ItemKeys.HOLOGRAM_HEIGHT_ITEM.getSlot();

        ItemMeta meta = item.getItemMeta();

        if (meta != null) {
            if (meta.hasDisplayName()) {
                String displayName = meta.getDisplayName();
                displayName = displayName.replace("{height-status}", String.format("%.1f", hologramHeight));
                meta.setDisplayName(displayName);
            }

            if (meta.hasLore()) {
                List<String> lore = meta.getLore();
                if (lore != null) {
                    for (int i = 0; i < lore.size(); i++) {
                        String line = lore.get(i);
                        if (line.contains("{height-status}")) {
                            line = line.replace("{height-status}", String.format("%.1f", hologramHeight));
                            lore.set(i, line);
                        }
                    }
                    meta.setLore(lore);
                }
            }
            item.setItemMeta(meta);
        }

        item.editMeta(itemMeta -> itemMeta.getPersistentDataContainer().set(ITEM_KEY, PersistentDataType.STRING, ItemKeys.HOLOGRAM_HEIGHT_ITEM.name()));

        inventory.setItem(slot, item);
        slotToItemKeyMap.put(slot, ItemKeys.HOLOGRAM_HEIGHT_ITEM);
    }
}