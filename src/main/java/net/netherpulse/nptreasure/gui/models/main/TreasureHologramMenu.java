package net.netherpulse.nptreasure.gui.models.main;

import lombok.Getter;
import net.netherpulse.nptreasure.data.MenuController;
import net.netherpulse.nptreasure.gui.Menu;
import net.netherpulse.nptreasure.identifiers.keys.ItemKeys;
import net.netherpulse.nptreasure.identifiers.keys.MenuKeys;
import net.netherpulse.nptreasure.identifiers.keys.MessageKeys;
import net.netherpulse.nptreasure.manager.TreasureManager;
import net.netherpulse.nptreasure.model.TreasureChest;
import net.netherpulse.nptreasure.processor.MessageProcessor;
import net.netherpulse.nptreasure.sessions.ChatSessions;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@SuppressWarnings("deprecation")
public class TreasureHologramMenu extends Menu {
    private final TreasureChest chest;
    @Getter
    private final List<String> hologramLines;
    private double hologramHeight;

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

        if (slot == 53) {
            saveHologramChanges();
            player.sendMessage(MessageKeys.SUCCESS_SAVE.getMessage());
            player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 0.5f, 1.0f);
            new TreasureEditMenu(MenuController.getMenuUtils(player), chest).open();
            return;
        }

        if (slot == 52) {
            hologramLines.add("%blank%");
            player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 0.5f, 1.0f);
            updateHologramInRealTime();

            open();
            return;
        }

        if (slot == 51) {
            if (event.getClick() == ClickType.LEFT) {
                hologramHeight += 0.1;
                player.sendMessage(MessageProcessor.process("&aHologram height increased to: &e" + String.format("%.1f", hologramHeight)));
            } else if (event.getClick() == ClickType.RIGHT) {
                hologramHeight -= 0.1;
                player.sendMessage(MessageProcessor.process("&aHologram height decreased to: &e" + String.format("%.1f", hologramHeight)));
            }

            chest.setHologramHeight(hologramHeight);
            updateHologramInRealTime();

            player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 0.5f, 1.0f);
            return;
        }

        if (slot < hologramLines.size()) {
            if (event.getClick() == ClickType.LEFT) {
                player.closeInventory();
                ChatSessions.editHologramLine(player, slot, this);
            } else if (event.getClick() == ClickType.DROP) {
                hologramLines.remove(slot);

                updateHologramInRealTime();

                player.playSound(player.getLocation(), Sound.ENTITY_ITEM_BREAK, 0.5f, 1.0f);
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
        TreasureManager.getInstance().saveTreasures();
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

        ItemStack heightItem = ItemKeys.HOLOGRAM_HEIGHT_ITEM.getItem();
        ItemMeta heightMeta = heightItem.getItemMeta();
        if (heightMeta != null) {
            List<String> lore = heightMeta.getLore();

            if (lore != null) {
                for (int i = 0; i < lore.size(); i++) {
                    String line = lore.get(i);
                    if (line.contains("{height-status}")) {
                        line = line.replace("{height-status}", String.format("%.1f", hologramHeight));
                        lore.set(i, line);
                    }
                }
                heightMeta.setLore(lore);
            }

            heightItem.setItemMeta(heightMeta);
        }

        inventory.setItem(51, heightItem);
        inventory.setItem(52, ItemKeys.HOLOGRAM_CREATE_LINE.getItem());
        inventory.setItem(53, ItemKeys.HOLOGRAM_SAVE.getItem());
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
}