package com.mongenscave.mctreasure.gui.models;

import com.mongenscave.mctreasure.data.MenuController;
import com.mongenscave.mctreasure.gui.Menu;
import com.mongenscave.mctreasure.gui.models.main.TreasureEditMenu;
import com.mongenscave.mctreasure.identifiers.keys.ItemKeys;
import com.mongenscave.mctreasure.identifiers.keys.MenuKeys;
import com.mongenscave.mctreasure.identifiers.keys.MessageKeys;
import com.mongenscave.mctreasure.managers.TreasureManager;
import com.mongenscave.mctreasure.model.TreasureChest;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TreasureItemsMenu extends Menu {
    private final TreasureChest chest;
    private List<ItemStack> items;
    private boolean isInitialized = false;

    public TreasureItemsMenu(@NotNull MenuController menuController, @NotNull TreasureChest chest) {
        super(menuController);
        this.chest = chest;
        this.items = chest.getItems();
    }

    @Override
    public void handleMenu(final @NotNull InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();

        if (event.getSlot() == getSlots() - 1) {
            event.setCancelled(true);
            updateItemsFromInventory();
            chest.setItems(items);

            TreasureManager treasureManager = TreasureManager.getInstance();

            if (treasureManager == null) return;

            treasureManager.saveTreasures();

            player.sendMessage(MessageKeys.SUCCESS_SAVE.getMessage());
            player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 0.5f, 1.0f);
            new TreasureEditMenu(MenuController.getMenuUtils(player), chest).open();
            return;
        }

        if (event.getSlot() >= getSlots() - 1) event.setCancelled(true);
    }

    @Override
    public void setMenuItems() {
        ItemStack saveItem = ItemKeys.ADD_ITEM_SAVE.getItem();
        inventory.setItem(getSlots() - 1, saveItem);

        if (!isInitialized) {
            for (int i = 0; i < items.size() && i < getSlots() - 1; i++) {
                ItemStack item = items.get(i);
                // Csak akkor klónozunk, ha tényleg kell - de itt is megtartjuk minden adatot
                if (item != null && item.getType() != Material.AIR) {
                    inventory.setItem(i, item);
                }
            }

            isInitialized = true;
        }
    }

    private void updateItemsFromInventory() {
        if (inventory == null) return;

        List<ItemStack> updatedItems = Collections.synchronizedList(new ArrayList<>());

        for (int i = 0; i < getSlots() - 1; i++) {
            ItemStack currentItem = inventory.getItem(i);

            if (currentItem != null && currentItem.getType() != Material.AIR) {
                updatedItems.add(currentItem);
            }
        }

        this.items = updatedItems;
    }

    @Override
    public String getMenuName() {
        return MenuKeys.MENU_ADD_ITEM_TITLE.getString();
    }

    @Override
    public int getSlots() {
        return chest.getSize() > 0 ? chest.getSize() : 54;
    }

    @Override
    public int getMenuTick() {
        return 5;
    }
}