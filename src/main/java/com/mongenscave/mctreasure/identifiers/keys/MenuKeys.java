package com.mongenscave.mctreasure.identifiers.keys;

import com.artillexstudios.axapi.config.Config;
import com.mongenscave.mctreasure.McTreasure;
import com.mongenscave.mctreasure.processor.MessageProcessor;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public enum MenuKeys {
    MENU_OVERVIEW_TITLE("overview-menu.title"),
    MENU_OVERVIEW_SIZE("overview-menu.size"),

    MENU_EDIT_TITLE("edit-menu.title"),
    MENU_EDIT_SIZE("edit-menu.size"),

    MENU_PREVIEW_TITLE("preview-menu.title"),
    MENU_PREVIEW_SIZE("preview-menu.size"),

    MENU_ADD_ITEM_TITLE("add-item-menu.title"),

    MENU_EDIT_HOLOGRAM_TITLE("edit-hologram-menu.title"),
    MENU_EDIT_HOLOGRAM_SIZE("edit-hologram-menu.size");


    private static final Config config = McTreasure.getInstance().getGuis();
    private final String path;

    MenuKeys(@NotNull String path) {
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
}

