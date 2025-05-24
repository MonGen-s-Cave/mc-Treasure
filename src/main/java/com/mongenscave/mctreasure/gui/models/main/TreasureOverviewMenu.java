package com.mongenscave.mctreasure.gui.models.main;

import com.mongenscave.mctreasure.data.MenuController;
import com.mongenscave.mctreasure.gui.Menu;
import com.mongenscave.mctreasure.gui.models.TreasurePreviewMenu;
import com.mongenscave.mctreasure.identifiers.keys.ItemKeys;
import com.mongenscave.mctreasure.identifiers.keys.MenuKeys;
import com.mongenscave.mctreasure.manager.TreasureManager;
import com.mongenscave.mctreasure.model.TreasureChest;
import com.mongenscave.mctreasure.processor.MessageProcessor;
import com.mongenscave.mctreasure.utils.PlaceholderUtils;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

@SuppressWarnings("deprecation")
public class TreasureOverviewMenu extends Menu {
    private final TreasureManager treasureManager;
    private final List<TreasureChest> treasureChests;

    public TreasureOverviewMenu(@NotNull MenuController menuController) {
        super(menuController);
        this.treasureManager = TreasureManager.getInstance();
        this.treasureChests = treasureManager.getAllTreasures();
    }

    @Override
    public void handleMenu(final @NotNull InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        ItemStack clickedItem = event.getCurrentItem();

        if (clickedItem == null || clickedItem.getType() == Material.AIR) return;

        event.setCancelled(true);

        if (event.getSlot() == 53) {
            if (event.getClick() == ClickType.LEFT) {
                String id = "treasure_" + UUID.randomUUID().toString().substring(0, 8);
                TreasureChest treasure = treasureManager.createTreasure(id);
                treasureManager.saveTreasures();

                new TreasureEditMenu(MenuController.getMenuUtils(player), treasure).open();
                player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1.0f, 2.0f);
            }
            return;
        }

        int index = event.getSlot();
        if (index >= 0 && index < treasureChests.size()) {
            TreasureChest treasure = treasureChests.get(index);

            if (event.getClick() == ClickType.LEFT) {
                new TreasureEditMenu(MenuController.getMenuUtils(player), treasure).open();
                player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 0.5f, 1.0f);
            } else if (event.getClick() == ClickType.RIGHT) {
                new TreasurePreviewMenu(MenuController.getMenuUtils(player), treasure).open();
                player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 0.5f, 1.0f);
            } else if (event.getClick() == ClickType.DROP) {
                player.closeInventory();
                treasureManager.deleteTreasure(treasure.getId());
                player.sendMessage(MessageProcessor.process("&cTreasure chest &e" + treasure.getName() + " &chas been deleted."));
                player.playSound(player.getLocation(), Sound.ENTITY_ITEM_BREAK, 0.5f, 0.5f);

                new TreasureOverviewMenu(MenuController.getMenuUtils(player)).open();
            }
        }
    }

    @Override
    public void setMenuItems() {
        AtomicInteger slot = new AtomicInteger(0);

        treasureChests.forEach(treasure -> {
            int currentSlot = slot.getAndIncrement();
            if (currentSlot < 53) inventory.setItem(currentSlot, createTreasureItem(treasure));
        });

        inventory.setItem(53, ItemKeys.OVERVIEW_CREATE_TREASURE.getItem());
    }

    @NotNull
    private ItemStack createTreasureItem(@NotNull TreasureChest treasure) {
        ItemStack baseItem = ItemKeys.OVERVIEW_TREASURE_ITEM.getItem();

        if (baseItem == null) {
            baseItem = new ItemStack(Material.BARREL);
            ItemMeta meta = baseItem.getItemMeta();
            meta.setDisplayName(MessageProcessor.process("&f" + treasure.getName()));
            baseItem.setItemMeta(meta);
            return baseItem;
        }

        ItemStack result = baseItem.clone();
        ItemMeta meta = result.getItemMeta();

        if (meta.hasDisplayName()) {
            String name = meta.getDisplayName().replace("{name}", treasure.getName());
            meta.setDisplayName(MessageProcessor.process(name));
        }

        if (meta.hasLore() && meta.getLore() != null) {
            List<String> lore = new ArrayList<>();
            List<String> originalLore = meta.getLore();

            for (String line : originalLore) {
                line = PlaceholderUtils.replacePlaceholders(line, treasure);
                lore.add(MessageProcessor.process(line));
            }

            meta.setLore(lore);
        }

        result.setItemMeta(meta);
        return result;
    }

    @Override
    public String getMenuName() {
        return MenuKeys.MENU_OVERVIEW_TITLE.getString();
    }

    @Override
    public int getSlots() {
        return MenuKeys.MENU_OVERVIEW_SIZE.getInt();
    }

    @Override
    public int getMenuTick() {
        return 20;
    }
}