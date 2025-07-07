package com.mongenscave.mctreasure.utils;

import com.mongenscave.mctreasure.McTreasure;
import com.mongenscave.mctreasure.commands.CommandTreasure;
import com.mongenscave.mctreasure.handler.ErrorHandler;
import com.mongenscave.mctreasure.identifiers.keys.ConfigKeys;
import lombok.experimental.UtilityClass;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import revxrsal.commands.bukkit.BukkitLamp;
import revxrsal.commands.orphan.Orphans;

@UtilityClass
public class RegisterUtils {
    private static final McTreasure plugin = McTreasure.getInstance();

    public void registerCommands() {
        var lamp = BukkitLamp.builder(plugin)
                .exceptionHandler(new ErrorHandler())
                .build();

        lamp.register(Orphans.path(ConfigKeys.ALIASES.getList().toArray(String[]::new)).handler(new CommandTreasure()));
    }

    public boolean isPluginEnabled(@NotNull String name) {
        Plugin plugin = Bukkit.getPluginManager().getPlugin(name);
        return plugin != null && plugin.isEnabled();
    }
}
