package com.mongenscave.mctreasure.identifiers.keys;

import com.mongenscave.mctreasure.McTreasure;
import com.mongenscave.mctreasure.config.Config;
import com.mongenscave.mctreasure.processor.MessageProcessor;
import org.jetbrains.annotations.NotNull;

public enum PlaceholderKeys {
    STATUS_ENABLED("placeholders.status.enabled"),
    STATUS_DISABLED("placeholders.status.disabled"),
    STATUS_SET("placeholders.status.set"),
    STATUS_NOT_SET("placeholders.status.not-set"),
    STATUS_NONE("placeholders.status.none"),

    LOCATION_FORMAT("placeholders.location.format"),
    LOCATION_NOT_SET("placeholders.location.not-set"),

    COOLDOWN_FORMAT("placeholders.cooldown.set"),
    COOLDOWN_NONE("placeholders.cooldown.none"),

    SIZE_FORMAT("placeholders.size.format"),

    PERMISSION_FORMAT("placeholders.permission.format"),
    PERMISSION_NONE("placeholders.permission.none"),

    HOLOGRAM_TIME_LEFT("placeholders.hologram.time-left"),
    HOLOGRAM_READY("placeholders.hologram.ready");

    private static final Config config = McTreasure.getInstance().getConfiguration();
    private final String path;

    PlaceholderKeys(@NotNull String path) {
        this.path = path;
    }

    public @NotNull String getString() {
        return MessageProcessor.process(config.getString(path));
    }

    public @NotNull String format(Object... args) {
        String template = getString();
        return MessageProcessor.process(String.format(template, args));
    }
}
