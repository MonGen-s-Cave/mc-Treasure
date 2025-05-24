package com.mongenscave.mctreasure.identifiers.keys;

import com.mongenscave.mctreasure.McTreasure;
import com.mongenscave.mctreasure.item.ItemFactory;
import lombok.Getter;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

@Getter
public enum ItemKeys {
    OVERVIEW_CREATE_TREASURE("overview-menu.items.create-treasure-item"),
    OVERVIEW_TREASURE_ITEM("overview-menu.items.treasure-item"),

    EDIT_NAME("edit-menu.items.edit-name"),
    EDIT_PERMISSION("edit-menu.items.edit-permission"),
    EDIT_COOLDOWN("edit-menu.items.edit-cooldown"),
    EDIT_SIZE("edit-menu.items.edit-size"),
    EDIT_LOCATION("edit-menu.items.edit-location"),
    EDIT_PUSHBACK("edit-menu.items.edit-pushback"),
    EDIT_HOLOGRAM("edit-menu.items.edit-hologram"),
    EDIT_ITEMS("edit-menu.items.edit-items"),
    EDIT_PARTICLE("edit-menu.items.edit-particle"),
    SAVE_ITEM("edit-menu.items.save-item"),
    BACK_ITEM("edit-menu.items.back-item"),
    PREVIEW_ITEM("edit-menu.items.preview-item"),

    HOLOGRAM_SAVE("edit-hologram-menu.items.save-item"),
    HOLOGRAM_CREATE_LINE("edit-hologram-menu.items.create-line-item"),
    HOLOGRAM_HEIGHT_ITEM("edit-hologram-menu.items.increase-height-item"),
    HOLOGRAM_LINE_ITEM("edit-hologram-menu.items.line-item"),
    HOLOGRAM_BLANK_ITEM("edit-hologram-menu.items.blank-item"),

    ADD_ITEM_SAVE("add-item-menu.items.save-item");

    private final String path;

    ItemKeys(@NotNull final String path) {
        this.path = path;
    }

    public ItemStack getItem() {
        return ItemFactory.createItemFromString(path, McTreasure.getInstance().getGuis()).orElse(null);
    }

    public int getSlot() {
        return McTreasure.getInstance().getGuis().getInt(path + ".slot");
    }
}
