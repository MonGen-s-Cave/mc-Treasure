# 🏴‍☠️ McTreasure API

<div align="center">

![TreasureAPI Logo](https://img.shields.io/badge/Treasure_API-v1.0.0-blue?style=for-the-badge&logo=clock)

[![Java](https://img.shields.io/badge/Java-17+-orange?style=flat-square&logo=openjdk)](https://openjdk.org/)
[![Build Status](https://img.shields.io/badge/Build-Passing-brightgreen?style=flat-square)](https://github.com/mongenscave/timesapi)
[![License](https://img.shields.io/badge/License-MIT-yellow?style=flat-square)](LICENSE)

**🎯 A modern, feature-rich API for creating interactive treasure experiences in Minecraft**

[📖 Documentation](#-documentation) • [🚀 Quick Start](#-quick-start) • [💡 Examples](#-examples) • [🎪 Events](#-events)

</div>

---

## ✨ Features

- 🔒 **Permission-based access control**
- ⏰ **Flexible cooldown system**
- 🎆 **Particle effects & holograms**
- 📦 **Dynamic inventory management**
- 🎪 **Comprehensive event system**
- 🔧 **Easy integration & setup**
- 📱 **Modern API design**

## 🚀 Quick Start

### 📦 Installation

<details>
<summary><b>Gradle (Kotlin DSL)</b></summary>

```kotlin
repositories {
    maven("https://repo.mongenscave.com/releases")
}

dependencies {
    compileOnly("com.mongenscave:mc-Treasure:1.0.1")
}
```
</details>

<details>
<summary><b>Gradle (Groovy)</b></summary>

```groovy
repositories {
    maven {
        url 'https://repo.mongenscave.com/releases'
    }
}

dependencies {
    compileOnly 'com.mongenscave:mc-Treasure:1.0.1'
}
```
</details>

<details>
<summary><b>Maven</b></summary>

```xml
<repositories>
    <repository>
        <id>mongenscave-releases</id>
        <url>https://repo.mongenscave.com/releases</url>
    </repository>
</repositories>

<dependencies>
    <dependency>
        <groupId>com.mongenscave</groupId>
        <artifactId>mc-Treasure</artifactId>
        <version>1.0.1</version>
        <scope>provided</scope>
    </dependency>
</dependencies>
```
</details>

### 🔧 Plugin Setup

Add to your `plugin.yml`:
```yaml
depend: [mc-Treasure]
```

## 💡 Examples

### 🎯 Basic Usage

```java
import com.mongenscave.mctreasure.api.McTreasureAPI;
import com.mongenscave.mctreasure.api.model.ITreasureChest;

public class TreasureExample {
    
    public void handleTreasure(Player player, String treasureId) {
        // 🔍 Get treasure by ID
        ITreasureChest treasure = McTreasureAPI.getTreasure(treasureId);
        
        if (treasure == null) {
            player.sendMessage("§c❌ Treasure not found!");
            return;
        }
        
        // ✅ Check if player can open
        if (McTreasureAPI.canPlayerOpen(player, treasure)) {
            player.sendMessage("§a🎉 Opening treasure: " + treasure.getName());
        } else {
            player.sendMessage("§c🔒 You cannot open this treasure!");
        }
    }
    
    // 🗺️ Find treasures near location
    public List<ITreasureChest> findNearbyTreasures(Location center, double radius) {
        return McTreasureAPI.getAllTreasures().stream()
            .filter(treasure -> treasure.getLocation() != null)
            .filter(treasure -> treasure.getLocation().distance(center) <= radius)
            .collect(Collectors.toList());
    }
}
```

### 🎪 Advanced Event Handling

```java
public class AdvancedTreasureListener implements Listener {
    
    @EventHandler(priority = EventPriority.HIGH)
    public void onTreasureOpen(TreasureOpenEvent event) {
        Player player = event.getPlayer();
        ITreasureChest treasure = event.getTreasure();
        
        // 🏆 VIP exclusive treasures
        if (treasure.getName().contains("💎 VIP")) {
            if (!player.hasPermission("treasure.vip")) {
                event.setCancelled(true);
                player.sendMessage("§c🔒 This treasure requires VIP membership!");
                player.playSound(player.getLocation(), Sound.BLOCK_CHEST_LOCKED, 1.0f, 1.0f);
                return;
            }
        }
        
        // 🌙 Time-based restrictions
        if (treasure.getName().contains("🌙 Midnight")) {
            if (!isNightTime(player.getWorld())) {
                event.setCancelled(true);
                player.sendMessage("§c🌙 This treasure only opens at midnight!");
                return;
            }
        }
        
        // 🎊 Opening effects
        player.sendMessage("§a✨ " + treasure.getName() + " §ais opening...");
        player.playSound(player.getLocation(), Sound.BLOCK_CHEST_OPEN, 1.0f, 1.2f);
    }
    
    @EventHandler
    public void onTreasureClose(TreasureCloseEvent event) {
        Player player = event.getPlayer();
        ITreasureChest treasure = event.getTreasure();
        List<ItemStack> itemsTaken = event.getItemsTaken();
        
        // 📊 Statistics tracking
        int itemCount = event.getItemCount();
        if (itemCount > 0) {
            player.sendMessage(String.format("§6📦 You collected §e%d §6items from §a%s", 
                itemCount, treasure.getName()));
            
            // 🎁 Bonus rewards for big hauls
            if (itemCount >= 10) {
                player.sendMessage("§a🎉 Bonus reward for finding so many treasures!");
                player.giveExp(100);
                player.getWorld().spawnParticle(Particle.VILLAGER_HAPPY, 
                    player.getLocation().add(0, 2, 0), 20, 1, 1, 1, 0.1);
            }
        }
        
        // 📈 Update player statistics
        updatePlayerStats(player, treasure, itemsTaken);
    }
    
    private boolean isNightTime(World world) {
        long time = world.getTime();
        return time >= 13000 && time <= 23000;
    }
    
    private void updatePlayerStats(Player player, ITreasureChest treasure, List<ItemStack> items) {
        // Your statistics implementation
    }
}
```

### 🛠️ Treasure Management

```java
public class TreasureManager {
    
    // 🏗️ Create and configure treasure
    public ITreasureChest createCustomTreasure(String id, Location location) {
        ITreasureChest treasure = McTreasureAPI.createTreasure(id);
        
        // Configuration would be done through the main plugin
        // This API provides read-only access
        
        return treasure;
    }
    
    // 📋 List all configured treasures
    public void listTreasures(CommandSender sender) {
        List<ITreasureChest> treasures = McTreasureAPI.getAllTreasures();
        
        sender.sendMessage("§6📦 Available Treasures §7(" + treasures.size() + "):");
        
        treasures.forEach(treasure -> {
            String status = treasure.isConfigured() ? "§a✅" : "§c❌";
            String location = treasure.getLocation() != null ? 
                formatLocation(treasure.getLocation()) : "§7Not set";
                
            sender.sendMessage(String.format("  %s §f%s §7- %s §7(%d items)", 
                status, treasure.getName(), location, treasure.getItemCount()));
        });
    }
    
    // 🔍 Treasure search functionality
    public List<ITreasureChest> searchTreasures(String query) {
        return McTreasureAPI.getAllTreasures().stream()
            .filter(treasure -> treasure.getName().toLowerCase()
                .contains(query.toLowerCase()))
            .collect(Collectors.toList());
    }
    
    private String formatLocation(Location loc) {
        return String.format("§7%s §f%d,%d,%d", 
            loc.getWorld().getName(), 
            loc.getBlockX(), 
            loc.getBlockY(), 
            loc.getBlockZ());
    }
}
```

## 📚 Documentation

### 🏛️ Core Classes

<details>
<summary><b>McTreasureAPI</b> - Main API entry point</summary>

```java
// 🔍 Treasure retrieval
ITreasureChest getTreasure(String id)
List<ITreasureChest> getAllTreasures()
ITreasureChest getTreasureAtLocation(Location location)

// 🏗️ Treasure management
ITreasureChest createTreasure(String id)
boolean deleteTreasure(String id)

// ✅ Permission checking
boolean canPlayerOpen(Player player, ITreasureChest treasure)
```
</details>

<details>
<summary><b>ITreasureChest</b> - Treasure data interface</summary>

```java
// 📝 Basic properties
String getId()
String getName()
Location getLocation()
String getPermission()
long getCooldownMillis()

// 📦 Inventory data
int getSize()
List<ItemStack> getItems()
boolean hasItems()
int getItemCount()

// ✨ Features
boolean isPushbackEnabled()
double getPushbackStrength()
boolean isHologramEnabled()
List<String> getHologramLines()
boolean isParticleEnabled()
ParticleTypes getParticleType()
Particle getParticleDisplay()

// 🔧 Utility methods
boolean isConfigured()
boolean isAtLocation(Location location)
boolean hasPermission(Player player)
OpenResult canPlayerOpen(Player player)
```
</details>

### 📦 Data Records

<details>
<summary><b>OpenResult</b> - Open attempt result</summary>

```java
public record OpenResult(
    boolean canOpen,        // Can the player open?
    @Nullable String message // Optional message
) {}
```
</details>

<details>
<summary><b>CooldownResult</b> - Cooldown information</summary>

```java
public record CooldownResult(
    boolean canOpen,              // Is cooldown satisfied?
    @Nullable String formattedTime, // Human-readable time left
    long remainingMillis          // Milliseconds remaining
) {}
```
</details>

## 🎪 Events

### 🚪 TreasureOpenEvent
> **Cancellable** - Fired when a player attempts to open a treasure

```java
@EventHandler
public void onOpen(TreasureOpenEvent event) {
    Player player = event.getPlayer();
    ITreasureChest treasure = event.getTreasure();
    
    // Cancel if needed
    event.setCancelled(true);
}
```

### 🏁 TreasureCloseEvent
> **Not Cancellable** - Fired when a player closes a treasure

```java
@EventHandler
public void onClose(TreasureCloseEvent event) {
    Player player = event.getPlayer();
    ITreasureChest treasure = event.getTreasure();
    List<ItemStack> itemsTaken = event.getItemsTaken();
    int itemCount = event.getItemCount();
}
```

## 🎯 Use Cases

- 🎮 **Game Mechanics**: Custom treasure hunt systems
- 🏆 **Rewards**: Achievement and milestone rewards
- 🛡️ **Access Control**: Permission-based treasure access
- 📊 **Analytics**: Player behavior tracking
- 🎪 **Events**: Special event treasures with time restrictions
- 🗺️ **World Integration**: Location-based treasure systems

## 🤝 Contributing

We welcome contributions! Please feel free to submit issues and enhancement requests.

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

---

<div align="center">

**Made with ❤️ by Mongenss Cave**

</div>