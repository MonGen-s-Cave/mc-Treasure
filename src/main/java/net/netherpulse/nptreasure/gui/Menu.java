package net.netherpulse.nptreasure.gui;

import net.netherpulse.nptreasure.data.MenuController;
import net.netherpulse.nptreasure.processor.MenuProcessor;
import net.netherpulse.nptreasure.processor.MessageProcessor;
import org.bukkit.Bukkit;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("deprecation")
public abstract class Menu implements InventoryHolder {
    public MenuController menuController;
    protected Inventory inventory;

    public Menu(@NotNull MenuController menuController) {
        this.menuController = menuController;
    }

    public abstract void handleMenu(final @NotNull InventoryClickEvent event);

    public abstract void setMenuItems();

    public abstract String getMenuName();

    public abstract int getSlots();

    public abstract int getMenuTick();

    @NotNull
    @Override
    public Inventory getInventory() {
        return inventory;
    }

    public void open() {
        inventory = Bukkit.createInventory(this, getSlots(), MessageProcessor.process(getMenuName()));

        this.setMenuItems();

        menuController.owner().openInventory(inventory);
        MenuProcessor menuUpdater = new MenuProcessor(this);
        menuUpdater.start(getMenuTick());
    }

    public void close() {
        MenuProcessor menuUpdater = new MenuProcessor(this);
        menuUpdater.stop();
        inventory = null;
    }

    public void updateMenuItems() {
        if (inventory == null) return;

        setMenuItems();
        menuController.owner().updateInventory();
    }
}
