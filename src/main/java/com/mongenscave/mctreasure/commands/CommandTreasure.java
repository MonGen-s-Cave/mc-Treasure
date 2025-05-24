package com.mongenscave.mctreasure.commands;

import com.mongenscave.mctreasure.data.MenuController;
import com.mongenscave.mctreasure.gui.models.main.TreasureOverviewMenu;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import revxrsal.commands.annotation.Subcommand;
import revxrsal.commands.orphan.OrphanCommand;

public class CommandTreasure implements OrphanCommand {
    @Subcommand("setup")
    public void setup(@NotNull Player player) {
        MenuController menuController = MenuController.getMenuUtils(player);

        new TreasureOverviewMenu(menuController).open();
    }
}
