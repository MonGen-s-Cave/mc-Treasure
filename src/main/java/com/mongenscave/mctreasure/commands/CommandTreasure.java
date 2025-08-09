package com.mongenscave.mctreasure.commands;

import com.mongenscave.mctreasure.McTreasure;
import com.mongenscave.mctreasure.data.MenuController;
import com.mongenscave.mctreasure.gui.models.main.TreasureOverviewMenu;
import com.mongenscave.mctreasure.identifiers.keys.ConfigKeys;
import com.mongenscave.mctreasure.identifiers.keys.MessageKeys;
import com.mongenscave.mctreasure.managers.HologramManager;
import com.mongenscave.mctreasure.managers.TreasureManager;
import com.mongenscave.mctreasure.model.TreasureChest;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;
import revxrsal.commands.annotation.Subcommand;
import revxrsal.commands.bukkit.annotation.CommandPermission;
import revxrsal.commands.orphan.OrphanCommand;

public class CommandTreasure implements OrphanCommand, Listener {
    private static final McTreasure plugin = McTreasure.getInstance();

    @Subcommand("reload")
    @CommandPermission("mctreasure.reload")
    public void reload(@NotNull CommandSender sender) {
        TreasureManager.getInstance().saveTreasures();

        for (TreasureChest treasure : TreasureManager.getInstance().getAllTreasures()) {
            treasure.removeHologram();
            treasure.removeParticleEffect();
        }

        HologramManager.resetInstance();

        plugin.getParticleSystem().reload();
        plugin.getConfiguration().reload();
        plugin.getGuis().reload();
        plugin.getLanguage().reload();
        plugin.getTreasures().reload();

        HologramManager.getInstance().reload();
        TreasureManager.getInstance().loadTreasures();
        TreasureManager.getInstance().setupAllHolograms();

        sender.sendMessage(MessageKeys.RELOAD.getMessage());
    }

    @Subcommand("give tracker")
    @CommandPermission("mctreasure.tracker")
    public void give(@NotNull CommandSender sender, @NotNull Player target) {
        target.getInventory().addItem(ConfigKeys.TRACKER_ITEM.getItem());
        target.sendMessage(MessageKeys.TRACKER_GET.getMessage());
        sender.sendMessage(MessageKeys.TRACKER_GAVE.getMessage().replace("{player}", target.getName()));
    }

    @Subcommand("setup")
    @CommandPermission("mctreasure.setup")
    public void setup(@NotNull Player player) {
        MenuController menuController = MenuController.getMenuUtils(player);
        new TreasureOverviewMenu(menuController).open();
    }
}