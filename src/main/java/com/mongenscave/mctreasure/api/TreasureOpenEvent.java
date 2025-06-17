package com.mongenscave.mctreasure.api;

import com.mongenscave.mctreasure.api.model.ITreasureChest;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

/**
 * Called when a player opens a treasure chest
 */
public class TreasureOpenEvent extends Event implements Cancellable {
    private static final HandlerList HANDLERS = new HandlerList();
    private boolean cancelled = false;

    private final Player player;
    private final ITreasureChest treasure;

    public TreasureOpenEvent(@NotNull Player player, @NotNull ITreasureChest treasure) {
        this.player = player;
        this.treasure = treasure;
    }

    @NotNull
    public Player getPlayer() {
        return player;
    }

    @NotNull
    public ITreasureChest getTreasure() {
        return treasure;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
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