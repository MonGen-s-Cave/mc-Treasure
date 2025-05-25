package com.mongenscave.mctreasure.handler;

import com.mongenscave.mctreasure.identifiers.keys.MessageKeys;
import org.jetbrains.annotations.NotNull;
import revxrsal.commands.locales.LocaleReader;

import java.util.Locale;

@SuppressWarnings("deprecation")
public class ErrorHandler implements LocaleReader {
    @Override
    public boolean containsKey(@NotNull String string) {
        return true;
    }

    @Override
    public String get(@NotNull String string) {
        String result;

        switch (string) {
            case "no-permission" -> result = MessageKeys.NO_PERMISSION.getMessage();
            case "must-be-player" -> result = MessageKeys.PLAYER_REQUIRED.getMessage();
            default -> result = "";
        }

        return result;
    }

    private final Locale LOCALE = new Locale("en", "US");

    @Override
    public Locale getLocale() {
        return LOCALE;
    }
}
