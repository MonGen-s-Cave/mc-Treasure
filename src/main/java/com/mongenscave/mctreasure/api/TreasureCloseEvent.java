package com.mongenscave.mctreasure.api;

import com.mongenscave.mctreasure.model.TreasureChest;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Called when a player closes a treasure chest
 */
public class TreasureCloseEvent extends Event {
    private static final HandlerList HANDLERS = new HandlerList();

    private final Player player;
    private final TreasureChest treasure;
    private final List<ItemStack> itemsTaken;

    public TreasureCloseEvent(@NotNull Player player, @NotNull TreasureChest treasure, @NotNull List<ItemStack> itemsTaken) {
        this.player = player;
        this.treasure = treasure;
        this.itemsTaken = List.copyOf(itemsTaken);
    }

    @NotNull
    public Player getPlayer() {
        return player;
    }

    @NotNull
    public TreasureChest getTreasure() {
        return treasure;
    }

    @NotNull
    public List<ItemStack> getItemsTaken() {
        return itemsTaken;
    }

    public int getItemCount() {
        return itemsTaken.size();
    }

    @NotNull
    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    @NotNull
    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
}
