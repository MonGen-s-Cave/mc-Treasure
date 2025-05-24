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
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import java.util.concurrent.ConcurrentHashMap;

public class TreasureEditMenu extends Menu {
    private final TreasureChest chest;
    private final ConcurrentHashMap<Integer, ItemKeys> slotToItemKeyMap = new ConcurrentHashMap<>();
    private static final NamespacedKey ITEM_KEY = new NamespacedKey("mctreasure", "item_key");

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

        ItemKeys clickedItemKey = slotToItemKeyMap.get(event.getSlot());
        if (clickedItemKey == null) return;

        switch (clickedItemKey) {
            case EDIT_NAME -> {
                player.closeInventory();
                ChatSessions.setName(player, chest, new TreasureEditMenu(menuController, chest));
            }

            case EDIT_PERMISSION -> {
                player.closeInventory();
                ChatSessions.setPermission(player, chest, new TreasureEditMenu(menuController, chest));
            }

            case EDIT_COOLDOWN -> {
                player.closeInventory();
                ChatSessions.setCooldown(player, chest, new TreasureEditMenu(menuController, chest));
            }

            case EDIT_SIZE -> {
                player.closeInventory();
                ChatSessions.setSize(player, chest, new TreasureEditMenu(menuController, chest));
            }

            case EDIT_LOCATION -> {
                player.closeInventory();
                LocationSession.startSettingLocation(player, chest);
                player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 0.5f, 1.0f);
            }

            case EDIT_PUSHBACK -> {
                switch (event.getClick()) {
                    case LEFT -> {
                        player.closeInventory();
                        ChatSessions.setPushbackStrength(player, chest, new TreasureEditMenu(menuController, chest));
                    }

                    case DROP -> {
                        chest.setPushbackEnabled(!chest.isPushbackEnabled());
                        player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 0.5f, 1.0f);
                        open();
                    }
                }
            }

            case EDIT_HOLOGRAM -> {
                switch (event.getClick()) {
                    case LEFT -> {
                        new TreasureHologramMenu(MenuController.getMenuUtils(player), chest).open();
                        player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 0.5f, 1.0f);
                    }

                    case DROP -> {
                        chest.setHologramEnabled(!chest.isHologramEnabled());
                        if (chest.isHologramEnabled()) chest.setupHologram();
                        else chest.removeHologram();
                        player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 0.5f, 1.0f);
                        open();
                    }
                }
            }

            case EDIT_ITEMS -> {
                new TreasureItemsMenu(MenuController.getMenuUtils(player), chest).open();
                player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 0.5f, 1.0f);
            }

            case EDIT_PARTICLE -> {
                switch (event.getClick()) {
                    case LEFT -> {
                        ParticleTypes currentType = chest.getParticleType();

                        if (currentType == null) chest.setParticleType(ParticleTypes.HEART);
                        else chest.setParticleType(currentType.next());

                        chest.setParticleDisplay(TreasureManager.getInstance().getParticleFromConfig(chest.getParticleType()));

                        if (chest.isParticleEnabled()) chest.setupParticleEffect();

                        player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 0.5f, 1.0f);
                        open();
                    }

                    case RIGHT -> {
                        ParticleTypes currentType = chest.getParticleType();

                        if (currentType == null) chest.setParticleType(ParticleTypes.TORNADO);
                        else chest.setParticleType(currentType.previous());

                        chest.setParticleDisplay(TreasureManager.getInstance().getParticleFromConfig(chest.getParticleType()));
                        if (chest.isParticleEnabled()) chest.setupParticleEffect();

                        player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 0.5f, 1.0f);
                        open();
                    }

                    case DROP -> {
                        chest.setParticleEnabled(!chest.isParticleEnabled());

                        if (chest.isParticleEnabled()) {
                            if (chest.getParticleType() == null) chest.setParticleType(ParticleTypes.HEART);
                            chest.setParticleDisplay(TreasureManager.getInstance().getParticleFromConfig(chest.getParticleType()));
                            chest.setupParticleEffect();
                        } else chest.removeParticleEffect();

                        player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 0.5f, 1.0f);
                        open();
                    }
                }
            }

            case SAVE_ITEM -> {
                TreasureManager.getInstance().saveTreasures();
                player.sendMessage(MessageKeys.SUCCESS_SAVE.getMessage());
                player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 0.5f, 1.0f);
                new TreasureOverviewMenu(MenuController.getMenuUtils(player)).open();
            }
        }
    }

    @Override
    public void setMenuItems() {
        setMenuItem(ItemKeys.EDIT_NAME);
        setMenuItem(ItemKeys.EDIT_PERMISSION);
        setMenuItem(ItemKeys.EDIT_COOLDOWN);
        setMenuItem(ItemKeys.EDIT_SIZE);
        setMenuItem(ItemKeys.EDIT_LOCATION);
        setMenuItem(ItemKeys.EDIT_PUSHBACK);
        setMenuItem(ItemKeys.EDIT_HOLOGRAM);
        setMenuItem(ItemKeys.EDIT_PARTICLE);
        setMenuItem(ItemKeys.EDIT_ITEMS);
        setMenuItem(ItemKeys.SAVE_ITEM);
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

    private void setMenuItem(@NotNull ItemKeys itemKey) {
        ItemStack item = PlaceholderUtils.applyPlaceholders(itemKey.getItem(), chest);
        int slot = itemKey.getSlot();

        item.editMeta(meta -> meta.getPersistentDataContainer().set(ITEM_KEY, PersistentDataType.STRING, itemKey.name()));

        inventory.setItem(slot, item);
        slotToItemKeyMap.put(slot, itemKey);
    }
}