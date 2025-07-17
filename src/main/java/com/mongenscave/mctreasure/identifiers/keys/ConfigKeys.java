package com.mongenscave.mctreasure.identifiers.keys;

import com.mongenscave.mctreasure.McTreasure;
import com.mongenscave.mctreasure.config.Config;
import com.mongenscave.mctreasure.item.ItemFactory;
import com.mongenscave.mctreasure.processor.MessageProcessor;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public enum ConfigKeys {
    ALIASES("aliases"),

    TOAST_ENABLED("toast.enabled"),
    TOAST_MESSAGE("toast.message"),
    TOAST_MATERIAL("toast.material"),

    TRACKER_ENABLED("treasure-tracker.enabled"),
    TRACKER_DIRECTIONS_FORWARD("treasure-tracker.directions.forward"),
    TRACKER_DIRECTIONS_BACKWARD("treasure-tracker.directions.backward"),
    TRACKER_DIRECTIONS_LEFT("treasure-tracker.directions.left"),
    TRACKER_DIRECTIONS_RIGHT("treasure-tracker.directions.right"),
    TRACKER_MESSAGE("treasure-tracker.message"),
    TRACKER_ITEM("treasure-tracker.item");

    private static final Config config = McTreasure.getInstance().getConfiguration();
    private final String path;

    ConfigKeys(@NotNull String path) {
        this.path = path;
    }

    public static @NotNull String getString(@NotNull String path) {
        return config.getString(path);
    }

    public @NotNull String getString() {
        return MessageProcessor.process(config.getString(path));
    }

    public boolean getBoolean() {
        return config.getBoolean(path);
    }

    public int getInt() {
        return config.getInt(path);
    }

    public List<String> getList() {
        return config.getList(path);
    }

    public ItemStack getItem() {
        return ItemFactory.createItemFromString(path, McTreasure.getInstance().getConfiguration()).orElse(null);
    }
}
