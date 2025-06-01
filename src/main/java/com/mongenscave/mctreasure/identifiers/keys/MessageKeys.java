package com.mongenscave.mctreasure.identifiers.keys;

import com.mongenscave.mctreasure.McTreasure;
import com.mongenscave.mctreasure.config.Config;
import com.mongenscave.mctreasure.processor.MessageProcessor;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

@Getter
public enum MessageKeys {
    NO_PERMISSION("messages.no-permission"),
    PLAYER_REQUIRED("messages.player-required"),

    SESSION_PERMISSION_INPUT("messages.sessions.permission.input"),
    SESSION_PERMISSION_FAILED("messages.sessions.permission.failed"),
    SESSION_PERMISSION_START("messages.sessions.permission.start"),

    SESSION_NAME_INPUT("messages.sessions.name.input"),
    SESSION_NAME_FAILED("messages.sessions.name.failed"),
    SESSION_NAME_START("messages.sessions.name.start"),

    SESSION_COOLDOWN_INPUT("messages.sessions.cooldown.input"),
    SESSION_COOLDOWN_FAILED("messages.sessions.cooldown.failed"),
    SESSION_COOLDOWN_START("messages.sessions.cooldown.start"),
    SESSION_COOLDOWN_INVALID_FORMAT("messages.sessions.cooldown.invalid-format"),

    SESSION_SIZE_INPUT("messages.sessions.size.input"),
    SESSION_SIZE_FAILED("messages.sessions.size.failed"),
    SESSION_SIZE_START("messages.sessions.size.start"),
    SESSION_SIZE_INVALID_SIZE("messages.sessions.size.invalid-size"),
    SESSION_SIZE_INVALID_FORMAT("messages.sessions.size.invalid-format"),

    SESSION_PUSHBACK_INPUT("messages.sessions.pushback.input"),
    SESSION_PUSHBACK_FAILED("messages.sessions.pushback.failed"),
    SESSION_PUSHBACK_START("messages.sessions.pushback.start"),
    SESSION_PUSHBACK_INVALID_STRENGTH("messages.sessions.pushback.invalid-strength"),
    SESSION_PUSHBACK_INVALID_FORMAT("messages.sessions.pushback.invalid-format"),

    SESSION_HOLOGRAM_BLANK("messages.sessions.hologram.blank"),
    SESSION_HOLOGRAM_INPUT("messages.sessions.hologram.input"),
    SESSION_HOLOGRAM_FAILED("messages.sessions.hologram.failed"),
    SESSION_HOLOGRAM_START("messages.sessions.hologram.start"),

    SESSION_LOCATION_START("messages.sessions.location.start"),
    SESSION_LOCATION_INPUT("messages.sessions.location.input"),

    SUCCESS_SAVE("messages.success-save"),
    ITEM_OBTAINED("messages.item-obtained"),

    COOLDOWN("messages.cooldown"),
    INVENTORY_FULL("messages.inventory-full"),

    UPDATE_NOTIFY("messages.update-notify"),
    RELOAD("messages.reload");

    private static final Config language = McTreasure.getInstance().getLanguage();
    private final String path;

    MessageKeys(@NotNull String path) {
        this.path = path;
    }

    public @NotNull String getMessage() {
        return MessageProcessor.process(language.getString(path)).replace("%prefix%", MessageProcessor.process(language.getString("prefix")));
    }
}
