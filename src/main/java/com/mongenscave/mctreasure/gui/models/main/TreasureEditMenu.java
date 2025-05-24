package com.mongenscave.mctreasure.gui.models.main;

import com.mongenscave.mctreasure.data.MenuController;
import com.mongenscave.mctreasure.gui.Menu;
import com.mongenscave.mctreasure.gui.models.TreasureItemsMenu;
import com.mongenscave.mctreasure.identifiers.ParticleTypes;
import com.mongenscave.mctreasure.identifiers.keys.ItemKeys;
import com.mongenscave.mctreasure.identifiers.keys.MenuKeys;
import com.mongenscave.mctreasure.identifiers.keys.MessageKeys;
import com.mongenscave.mctreasure.manager.TreasureManager;
import com.mongenscave.mctreasure.model.TreasureChest;
import com.mongenscave.mctreasure.sessions.ChatSessions;
import com.mongenscave.mctreasure.sessions.LocationSession;
import com.mongenscave.mctreasure.utils.PlaceholderUtils;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class TreasureEditMenu extends Menu {
    private final TreasureChest chest;
    private final Map<Integer, ItemKeys> slotToItemKeyMap = new HashMap<>();

    public TreasureEditMenu(@NotNull MenuController menuController, @NotNull TreasureChest chest) {
        super(menuController);
        this.chest = chest;
    }

    @Override
    public void handleMenu(final @NotNull InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        ItemStack clickedItem = event.getCurrentItem();

        if (clickedItem == null || clickedItem.getType() == Material.AIR) return;

        event.setCancelled(true);

        ItemKeys clickedItemKey = getItemKeyFromClickedItem(clickedItem, event.getSlot());
        if (clickedItemKey == null) return;

        switch (clickedItemKey) {
            case EDIT_NAME:
                player.closeInventory();
                ChatSessions.setName(player, chest, new TreasureEditMenu(menuController, chest));
                break;

            case EDIT_PERMISSION:
                player.closeInventory();
                ChatSessions.setPermission(player, chest, new TreasureEditMenu(menuController, chest));
                break;

            case EDIT_COOLDOWN:
                player.closeInventory();
                ChatSessions.setCooldown(player, chest, new TreasureEditMenu(menuController, chest));
                break;

            case EDIT_SIZE:
                player.closeInventory();
                ChatSessions.setSize(player, chest, new TreasureEditMenu(menuController, chest));
                break;

            case EDIT_LOCATION:
                player.closeInventory();
                LocationSession.startSettingLocation(player, chest);
                player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 0.5f, 1.0f);
                break;

            case EDIT_PUSHBACK:
                if (event.getClick() == ClickType.LEFT) {
                    player.closeInventory();
                    ChatSessions.setPushbackStrength(player, chest, new TreasureEditMenu(menuController, chest));
                } else if (event.getClick() == ClickType.DROP) {
                    chest.setPushbackEnabled(!chest.isPushbackEnabled());
                    player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 0.5f, 1.0f);
                    open();
                }
                break;

            case EDIT_HOLOGRAM:
                if (event.getClick() == ClickType.LEFT) {
                    new TreasureHologramMenu(MenuController.getMenuUtils(player), chest).open();
                    player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 0.5f, 1.0f);
                } else if (event.getClick() == ClickType.DROP) {
                    chest.setHologramEnabled(!chest.isHologramEnabled());
                    if (chest.isHologramEnabled()) chest.setupHologram();
                    else chest.removeHologram();
                    player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 0.5f, 1.0f);
                    open();
                }
                break;

            case EDIT_ITEMS:
                new TreasureItemsMenu(MenuController.getMenuUtils(player), chest).open();
                player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 0.5f, 1.0f);
                break;

            case EDIT_PARTICLE:
                if (event.getClick() == ClickType.LEFT) {
                    ParticleTypes currentType = chest.getParticleType();

                    if (currentType == null) chest.setParticleType(ParticleTypes.HEART);
                    else chest.setParticleType(currentType.next());

                    chest.setParticleDisplay(TreasureManager.getInstance().getParticleFromConfig(chest.getParticleType()));

                    if (chest.isParticleEnabled()) chest.setupParticleEffect();

                    player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 0.5f, 1.0f);
                    open();
                } else if (event.getClick() == ClickType.RIGHT) {
                    ParticleTypes currentType = chest.getParticleType();

                    if (currentType == null) chest.setParticleType(ParticleTypes.TORNADO);
                    else chest.setParticleType(currentType.previous());

                    chest.setParticleDisplay(TreasureManager.getInstance().getParticleFromConfig(chest.getParticleType()));
                    if (chest.isParticleEnabled()) chest.setupParticleEffect();

                    player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 0.5f, 1.0f);
                    open();
                } else if (event.getClick() == ClickType.DROP) {
                    chest.setParticleEnabled(!chest.isParticleEnabled());
                    if (chest.isParticleEnabled()) {
                        if (chest.getParticleType() == null) chest.setParticleType(ParticleTypes.HEART);
                        chest.setParticleDisplay(TreasureManager.getInstance().getParticleFromConfig(chest.getParticleType()));
                        chest.setupParticleEffect();
                    } else chest.removeParticleEffect();
                    player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 0.5f, 1.0f);
                    open();
                }
                break;

            case SAVE_ITEM:
                TreasureManager.getInstance().saveTreasures();
                player.sendMessage(MessageKeys.SUCCESS_SAVE.getMessage());
                player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 0.5f, 1.0f);
                new TreasureOverviewMenu(MenuController.getMenuUtils(player)).open();
                break;
        }
    }

    @Nullable
    private ItemKeys getItemKeyFromClickedItem(@NotNull ItemStack clickedItem, int slot) {
        ItemKeys fromSlot = slotToItemKeyMap.get(slot);
        if (fromSlot != null) return fromSlot;

        if (clickedItem.hasItemMeta() && clickedItem.getItemMeta() != null) {
            try {
                NamespacedKey key = new NamespacedKey("mctreasure", "item_key");
                String itemKeyName = clickedItem.getItemMeta().getPersistentDataContainer()
                        .get(key, PersistentDataType.STRING);

                if (itemKeyName != null) {
                    return ItemKeys.valueOf(itemKeyName);
                }
            } catch (Exception ignored) {}
        }

        for (ItemKeys itemKey : ItemKeys.values()) {
            ItemStack templateItem = itemKey.getItem();
            if (templateItem != null && isSameItem(clickedItem, templateItem)) {
                return itemKey;
            }
        }

        return null;
    }

    private boolean isSameItem(@NotNull ItemStack item1, @NotNull ItemStack item2) {
        if (item1.getType() != item2.getType()) return false;

        if (!item1.hasItemMeta() && !item2.hasItemMeta()) return true;
        if (item1.hasItemMeta() != item2.hasItemMeta()) return false;

        String name1 = item1.getItemMeta().hasDisplayName() ? item1.getItemMeta().getDisplayName() : "";
        String name2 = item2.getItemMeta().hasDisplayName() ? item2.getItemMeta().getDisplayName() : "";

        return name1.equals(name2);
    }

    private void setMenuItem(int slot, @NotNull ItemKeys itemKey) {
        ItemStack item = PlaceholderUtils.applyPlaceholders(itemKey.getItem(), chest);
        item.editMeta(meta -> {
            NamespacedKey key = new NamespacedKey("mctreasure", "item_key");
            meta.getPersistentDataContainer().set(key, PersistentDataType.STRING, itemKey.name());
        });

        inventory.setItem(slot, item);
        slotToItemKeyMap.put(slot, itemKey);
    }

    @Override
    public void setMenuItems() {
        setMenuItem(10, ItemKeys.EDIT_NAME);
        setMenuItem(12, ItemKeys.EDIT_PERMISSION);
        setMenuItem(14, ItemKeys.EDIT_COOLDOWN);
        setMenuItem(16, ItemKeys.EDIT_SIZE);
        setMenuItem(29, ItemKeys.EDIT_LOCATION);
        setMenuItem(31, ItemKeys.EDIT_PUSHBACK);
        setMenuItem(33, ItemKeys.EDIT_HOLOGRAM);
        setMenuItem(35, ItemKeys.EDIT_PARTICLE);
        setMenuItem(22, ItemKeys.EDIT_ITEMS);
        setMenuItem(49, ItemKeys.SAVE_ITEM);
    }

    @Override
    public String getMenuName() {
        return MenuKeys.MENU_EDIT_TITLE.getString().replace("{name}", chest.getName());
    }

    @Override
    public int getSlots() {
        return MenuKeys.MENU_EDIT_SIZE.getInt();
    }

    @Override
    public int getMenuTick() {
        return 20;
    }
}