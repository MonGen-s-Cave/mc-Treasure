package com.mongenscave.mctreasure.processor;

import com.artillexstudios.axapi.scheduler.ScheduledTask;
import com.mongenscave.mctreasure.McTreasure;
import com.mongenscave.mctreasure.gui.Menu;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;

public class MenuProcessor {
    private final Menu menu;
    private ScheduledTask task;

    public MenuProcessor(@NotNull Menu menu) {
        this.menu = menu;
    }

    public void start(int intervalTicks) {
        if (isRunning()) return;

        task = McTreasure.getInstance().getScheduler().runTimer(this::updateMenu, intervalTicks, intervalTicks);
    }

    public void stop() {
        if (task != null) {
            task.cancel();
            task = null;
        }
    }

    private void updateMenu() {
        Inventory inv = menu.getInventory();

        if (inv == null) {
            stop();
            return;
        }

        if (inv.getViewers().contains(menu.menuController.owner())) menu.updateMenuItems();
        else stop();
    }

    public boolean isRunning() {
        return task != null && !task.isCancelled();
    }
}