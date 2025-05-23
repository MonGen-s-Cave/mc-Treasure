package net.netherpulse.nptreasure.utils;

import lombok.experimental.UtilityClass;
import net.netherpulse.nptreasure.NpTreasure;
import net.netherpulse.nptreasure.commands.CommandTreasure;
import net.netherpulse.nptreasure.identifiers.keys.ConfigKeys;
import revxrsal.commands.bukkit.BukkitCommandHandler;
import revxrsal.commands.orphan.Orphans;

import java.util.Locale;

@UtilityClass
@SuppressWarnings("deprecation")
public class RegisterUtils {
    private static final NpTreasure plugin = NpTreasure.getInstance();

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
