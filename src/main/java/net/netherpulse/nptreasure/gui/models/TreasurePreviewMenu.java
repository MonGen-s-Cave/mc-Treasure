package net.netherpulse.nptreasure.gui.models;

import net.netherpulse.nptreasure.data.MenuController;
import net.netherpulse.nptreasure.gui.Menu;
import net.netherpulse.nptreasure.gui.models.main.TreasureEditMenu;
import net.netherpulse.nptreasure.identifiers.keys.MenuKeys;
import net.netherpulse.nptreasure.model.TreasureChest;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class TreasurePreviewMenu extends Menu {
    private final TreasureChest chest;
    private final List<ItemStack> items;

    public TreasurePreviewMenu(@NotNull MenuController menuController, @NotNull TreasureChest chest) {
        super(menuController);
        this.chest = chest;
        this.items = chest.getItems();
    }

    @Override
    public void handleMenu(final @NotNull InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        ItemStack clickedItem = event.getCurrentItem();

        if (clickedItem == null || clickedItem.getType() == Material.AIR) return;

        event.setCancelled(true);

        if (event.getSlot() == getSlots() - 1) {
            player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 0.5f, 1.0f);
            new TreasureEditMenu(MenuController.getMenuUtils(player), chest).open();
        }
    }

    @Override
    public void setMenuItems() {
        for (int i = 0; i < items.size() && i < getSlots() - 1; i++) {
            inventory.setItem(i, items.get(i));
        }

        ItemStack backButton = new ItemStack(Material.ARROW);
        inventory.setItem(getSlots() - 1, backButton);
    }

    @Override
    public String getMenuName() {
        return MenuKeys.MENU_PREVIEW_TITLE.getString();
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
