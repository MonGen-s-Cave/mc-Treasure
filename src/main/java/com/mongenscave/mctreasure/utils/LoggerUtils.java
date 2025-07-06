package com.mongenscave.mctreasure.utils;

import com.mongenscave.mctreasure.McTreasure;
import lombok.experimental.UtilityClass;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

@UtilityClass
public class LoggerUtils {
    private final Logger logger = LogManager.getLogger("McTreasure");

    public void info(@NotNull String msg, @NotNull Object... objs) {
        logger.info(msg, objs);
    }

    public void warn(@NotNull String msg, @NotNull Object... objs) {
        logger.warn(msg, objs);
    }

    public void error(@NotNull String msg, @NotNull Object... objs) {
        logger.error(msg, objs);
    }

    public void printStartup() {
        String color = "\u001B[35m";
        String reset = "\u001B[0m";
        String software = McTreasure.getInstance().getServer().getName();
        String version = McTreasure.getInstance().getServer().getVersion();

        String asciiArt = color + "  _______                                \n" + reset +
                color + " |__   __|                               \n" + reset +
                color + "    | |_ __ ___  __ _ ___ _   _ _ __ ___ \n" + reset +
                color + "    | | '__/ _ \\/ _` / __| | | | '__/ _ \\\n" + reset +
                color + "    | | | |  __/ (_| \\__ \\ |_| | | |  __/\n" + reset +
                color + "    |_|_|  \\___|\\__,_|___/\\__,_|_|  \\___|" + reset;

        info("");
        String[] lines = asciiArt.split("\n");

        for (String line : lines) {
            info(line);
        }

        info("");
        info("{}   The plugin successfully started.{}", color, reset);
        info("{}   mc-Treasure {} {}{}", color, software, version, reset);
        info("{}   Discord @ dc.mongenscave.com{}", color, reset);
        info("");
    }
}
