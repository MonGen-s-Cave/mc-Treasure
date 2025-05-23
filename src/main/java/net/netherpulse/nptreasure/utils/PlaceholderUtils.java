package net.netherpulse.nptreasure.utils;

import lombok.experimental.UtilityClass;
import net.netherpulse.nptreasure.NpTreasure;
import net.netherpulse.nptreasure.identifiers.keys.PlaceholderKeys;
import net.netherpulse.nptreasure.model.TreasureChest;
import net.netherpulse.nptreasure.processor.MessageProcessor;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@UtilityClass
@SuppressWarnings("deprecation")
public class PlaceholderUtils {
    @NotNull
    public String replacePlaceholders(@NotNull String line, @NotNull TreasureChest treasure) {
        line = line.replace("{name}", treasure.getName());

        String locationStatus = treasure.getLocation() != null ?
                PlaceholderKeys.STATUS_SET.getString() :
                PlaceholderKeys.STATUS_NOT_SET.getString();
        line = line.replace("{location-status}", locationStatus);

        String pushbackStatus = treasure.isPushbackEnabled()
                ? PlaceholderKeys.STATUS_ENABLED.getString()
                : PlaceholderKeys.STATUS_DISABLED.getString();
        line = line.replace("{pushback-status}", pushbackStatus);
        line = line.replace("{pushback-value}", String.valueOf(treasure.getPushbackStrength()));

        String hologramStatus = treasure.isHologramEnabled()
                ? PlaceholderKeys.STATUS_ENABLED.getString()
                : PlaceholderKeys.STATUS_DISABLED.getString();

        line = line.replace("{hologram-status}", hologramStatus);

        String cooldownStatus = treasure.getCooldownMillis() > 0
                ? PlaceholderKeys.COOLDOWN_FORMAT.format(TimeUtils.formatTime(treasure.getCooldownMillis()))
                : PlaceholderKeys.COOLDOWN_NONE.getString();
        line = line.replace("{cooldown-status}", cooldownStatus);

        String sizeStatus = PlaceholderKeys.SIZE_FORMAT.format(treasure.getSize());
        line = line.replace("{size-status}", sizeStatus);

        String particleStatus = treasure.isParticleEnabled() ?
                PlaceholderKeys.STATUS_ENABLED.getString() :
                PlaceholderKeys.STATUS_DISABLED.getString();
        line = line.replace("{particle-status}", particleStatus);

        String particleDetails = treasure.isParticleEnabled() ?
                NpTreasure.getInstance().getConfig().getString("placeholders.particle.format", "&a%s")
                        .replace("%s", treasure.getParticleType().name()) :
                PlaceholderKeys.STATUS_DISABLED.getString();
        line = line.replace("{particle-details}", particleDetails);

        if (line.contains("{permission-status}")) {
            String permissionStatus = treasure.getPermission() != null && !treasure.getPermission().isEmpty()
                    ? PlaceholderKeys.PERMISSION_FORMAT.format(treasure.getPermission())
                    : PlaceholderKeys.PERMISSION_NONE.getString();
            line = line.replace("{permission-status}", permissionStatus);
        }

        if (line.contains("{location-details}")) {
            if (treasure.getLocation() != null) {
                String locationDetails = PlaceholderKeys.LOCATION_FORMAT.format(
                        treasure.getLocation().getWorld().getName(),
                        treasure.getLocation().getBlockX(),
                        treasure.getLocation().getBlockY(),
                        treasure.getLocation().getBlockZ()
                );
                line = line.replace("{location-details}", locationDetails);
            } else line = line.replace("{location-details}", PlaceholderKeys.LOCATION_NOT_SET.getString());
        }

        return line;
    }

    @NotNull
    public ItemStack applyPlaceholders(@NotNull ItemStack item, @NotNull TreasureChest chest) {
        ItemMeta meta = item.getItemMeta();

        if (meta == null) return item;
        if (meta.hasDisplayName())
            meta.setDisplayName(MessageProcessor.process(meta.getDisplayName().replace("{name}", chest.getName())));

        if (meta.hasLore() && meta.getLore() != null) {
            List<String> lore = Collections.synchronizedList(new ArrayList<>());

            for (String line : meta.getLore()) {
                line = line.replace("{name-status}", chest.getName());
                line = line.replace("{location-status}", chest.getLocation() != null ?
                        PlaceholderKeys.STATUS_SET.getString() :
                        PlaceholderKeys.STATUS_NOT_SET.getString());

                if (chest.getLocation() != null) {
                    Location location = chest.getLocation();

                    line = line.replace("{location-details}", PlaceholderKeys.LOCATION_FORMAT.format(
                            location.getWorld().getName(),
                            location.getBlockX(),
                            location.getBlockY(),
                            location.getBlockZ()
                    ));
                } else line = line.replace("{location-details}", PlaceholderKeys.LOCATION_NOT_SET.getString());

                line = line.replace("{permission-status}", chest.getPermission() != null && !chest.getPermission().isEmpty() ?
                        PlaceholderKeys.PERMISSION_FORMAT.format(chest.getPermission()) :
                        PlaceholderKeys.PERMISSION_NONE.getString());
                line = line.replace("{pushback-status}", chest.isPushbackEnabled() ?
                        PlaceholderKeys.STATUS_ENABLED.getString() :
                        PlaceholderKeys.STATUS_DISABLED.getString());
                line = line.replace("{pushback-value}", String.valueOf(chest.getPushbackStrength()));

                line = line.replace("{hologram-status}", chest.isHologramEnabled() ?
                        PlaceholderKeys.STATUS_ENABLED.getString() :
                        PlaceholderKeys.STATUS_DISABLED.getString());

                line = line.replace("{cooldown-status}", chest.getCooldownMillis() > 0 ?
                        PlaceholderKeys.COOLDOWN_FORMAT.format(TimeUtils.formatTime(chest.getCooldownMillis())) :
                        PlaceholderKeys.COOLDOWN_NONE.getString());

                line = line.replace("{size-status}", PlaceholderKeys.SIZE_FORMAT.format(chest.getSize()));

                String particleStatus = chest.isParticleEnabled() && chest.getParticleType() != null
                        ? "&a" + chest.getParticleType().name()
                        : PlaceholderKeys.STATUS_DISABLED.getString();
                line = line.replace("{particle-status}", particleStatus);

                lore.add(MessageProcessor.process(line));
            }

            meta.setLore(lore);
        }

        item.setItemMeta(meta);
        return item;
    }
}