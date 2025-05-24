package com.mongenscave.mctreasure.utils;

import com.mongenscave.mctreasure.McTreasure;
import com.mongenscave.mctreasure.commands.CommandTreasure;
import com.mongenscave.mctreasure.identifiers.keys.ConfigKeys;
import lombok.experimental.UtilityClass;
import revxrsal.commands.bukkit.BukkitCommandHandler;
import revxrsal.commands.orphan.Orphans;

import java.util.Locale;

@UtilityClass
@SuppressWarnings("deprecation")
public class RegisterUtils {
    private static final McTreasure plugin = McTreasure.getInstance();

    public void registerCommands() {
        LoggerUtils.info("### Registering commands... ###");

        BukkitCommandHandler handler = BukkitCommandHandler.create(plugin);

        //handler.getTranslator().add(new ErrorHandler());
        handler.setLocale(new Locale("en", "US"));
        handler.register(Orphans.path(ConfigKeys.ALIASES.getList().toArray(String[]::new)).handler(new CommandTreasure()));
        handler.registerBrigadier();

        LoggerUtils.info("### Successfully registered exception handlers... ###");
    }
}
