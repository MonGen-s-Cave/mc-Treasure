[![CodeFactor](https://www.codefactor.io/repository/github/mongen-s-cave/mc-treasure/badge)](https://www.codefactor.io/repository/github/mongen-s-cave/mc-treasure)

# MCTreasure API Usage

This document describes how to use the MCTreasure plugin's API to interact with treasure chests and listen to treasure-related events in your own plugins.

## Getting Started

### Maven Dependency
```xml
<dependency>
    <groupId>com.mongenscave</groupId>
    <artifactId>mctreasure</artifactId>
    <version>1.0.0</version>
    <scope>provided</scope>
</dependency>
```

### Plugin Dependencies
Add mc-Treasure as a dependency in your `plugin.yml`:
```yaml
depend: [MCTreasure]
# or
softdepend: [MCTreasure]
```

## Event Listening


### Register Your Listener

```java
public class YourPlugin extends JavaPlugin {
    
    @Override
    public void onEnable() {
        // Register the event listener
        getServer().getPluginManager().registerEvents(new TreasureEventListener(), this);
    }
}
```

## API Usage Examples

### Permission-Based Access Control

```java
public class TreasureSecurityListener implements Listener {
    
    @EventHandler(priority = EventPriority.HIGH)
    public void onTreasureOpen(final @NotNull TreasureOpenEvent event) {
        Player player = event.getPlayer();
        TreasureChest treasure = event.getTreasure();
        
        // VIP-only treasures
        if (treasure.getName().contains("VIP") && !player.hasPermission("treasure.vip")) {
            event.setCancelled(true);
            player.sendMessage("§cThis treasure is for VIP members only!");
            return;
        }
        
        // Region-based restrictions
        if (treasure.getName().contains("SPAWN") && !isInSpawnRegion(player)) {
            event.setCancelled(true);
            player.sendMessage("§cYou must be in spawn to open this treasure!");
            return;
        }
        
        // Time-based restrictions
        if (treasure.getName().contains("NIGHT") && !isNightTime(player.getWorld())) {
            event.setCancelled(true);
            player.sendMessage("§cThis treasure can only be opened at night!");
        }
    }
    
    private boolean isInSpawnRegion(Player player) {
        // Your region checking logic here
        return true;
    }
    
    private boolean isNightTime(World world) {
        long time = world.getTime();
        return time >= 13000 && time <= 23000;
    }
}
```

### Statistics and Logging

```java
public class TreasureStatsListener implements Listener {
    @EventHandler
    public void onTreasureOpen(final @NotNull TreasureOpenEvent event) {}
    
    @EventHandler
    public void onTreasureClose(final @NotNull TreasureCloseEvent event) {}
}
```

## Event Reference

### TreasureOpenEvent
- **When**: Called when a player attempts to open a treasure chest
- **Cancellable**: Yes
- **Methods**:
    - `getPlayer()` - Returns the player opening the treasure
    - `getTreasure()` - Returns the treasure being opened
    - `setCancelled(boolean)` - Cancel the opening

### TreasureCloseEvent
- **When**: Called when a player closes a treasure chest
- **Cancellable**: No
- **Methods**:
    - `getPlayer()` - Returns the player who closed the treasure
    - `getTreasure()` - Returns the treasure that was closed
    - `getItemsTaken()` - Returns list of items the player took
    - `getItemCount()` - Returns number of items taken


