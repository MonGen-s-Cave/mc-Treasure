package com.mongenscave.mctreasure.gui.models;

import com.mongenscave.mctreasure.McTreasure;
import com.mongenscave.mctreasure.data.MenuController;
import com.mongenscave.mctreasure.gui.Menu;
import com.mongenscave.mctreasure.identifiers.keys.MessageKeys;
import com.mongenscave.mctreasure.model.TreasureChest;
import com.mongenscave.mctreasure.processor.MessageProcessor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class TreasureInventoryMenu extends Menu {
    private final TreasureChest chest;
    private final List<ItemStack> availableItems;
    private final ThreadLocalRandom random = ThreadLocalRandom.current();
    private boolean itemsPlaced = false;

    public TreasureInventoryMenu(@NotNull MenuController menuController, @NotNull TreasureChest chest) {
        super(menuController);
        this.chest = chest;
        this.availableItems = Collections.synchronizedList(new ArrayList<>(chest.getItems()));
        Collections.shuffle(this.availableItems);
    }

    @Override
    public void handleMenu(final @NotNull InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        ItemStack clickedItem = event.getCurrentItem();

        if (clickedItem == null || clickedItem.getType() == Material.AIR) return;

        event.setCancelled(true);

        if (event.getClick().isLeftClick() || event.getClick().isRightClick()) {
            if (hasAvailableSlot(player)) {
                player.getInventory().addItem(clickedItem.clone());
                player.playSound(player.getLocation(), Sound.ENTITY_ITEM_PICKUP, 0.5f, 1.2f);
                inventory.setItem(event.getSlot(), new ItemStack(Material.AIR));

                player.sendMessage(MessageKeys.ITEM_OBTAINED.getMessage());
            } else {
                player.sendMessage(MessageKeys.INVENTORY_FULL.getMessage());
                player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 0.5f, 1.0f);
            }
        }
    }

    @Override
    public void setMenuItems() {
        if (!itemsPlaced) {
            placeItems();
            itemsPlaced = true;
        }
    }

    private void placeItems() {
        inventory.clear();

        if (availableItems.isEmpty()) {
            ItemStack noItemsItem = new ItemStack(Material.PAPER);
            inventory.setItem(getSlots() / 2, noItemsItem);
            return;
        }

        int itemsToShow = Math.min(availableItems.size(), getSlots());
        List<Integer> availableSlots = getRandomSlots(itemsToShow);

        for (int i = 0; i < itemsToShow; i++) {
            inventory.setItem(availableSlots.get(i), availableItems.get(i).clone());
        }
    }

    @NotNull
    private List<Integer> getRandomSlots(int count) {
        List<Integer> slots = Collections.synchronizedList(new ArrayList<>());
        List<Integer> availableSlots = Collections.synchronizedList(new ArrayList<>());

        for (int i = 0; i < getSlots(); i++) {
            availableSlots.add(i);
        }

        Collections.shuffle(availableSlots, random);

        for (int i = 0; i < count; i++) {
            slots.add(availableSlots.get(i));
        }

        return slots;
    }

    private boolean hasAvailableSlot(@NotNull Player player) {
        for (ItemStack item : player.getInventory().getStorageContents()) {
            if (item == null || item.getType() == Material.AIR) return true;
        }

        return false;
    }

    @Override
    public void close() {
        super.close();

        Player player = menuController.owner();
        player.playSound(player.getLocation(), Sound.BLOCK_CHEST_CLOSE, 0.5f, 1.0f);
    }

    @Override
    public String getMenuName() {
        return MessageProcessor.process(chest.getName());
    }

    @Override
    public int getSlots() {
        return chest.getSize();
    }

    @Override
    public int getMenuTick() {
        return 20;
    }
}