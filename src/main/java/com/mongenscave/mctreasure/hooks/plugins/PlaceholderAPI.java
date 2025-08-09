package com.mongenscave.mctreasure.hooks.plugins;

import com.mongenscave.mctreasure.McTreasure;
import com.mongenscave.mctreasure.managers.cooldown.CooldownManager;
import com.mongenscave.mctreasure.managers.TreasureManager;
import com.mongenscave.mctreasure.model.TreasureChest;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@SuppressWarnings("deprecation")
public class PlaceholderAPI {
    public static boolean isRegistered = false;

    public static void registerHook() {
        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            new PlaceholderIntegration().register();
            isRegistered = true;
        }
    }

    private static class PlaceholderIntegration extends PlaceholderExpansion {
        private static final McTreasure plugin = McTreasure.getInstance();

        @Override
        public @NotNull String getIdentifier() {
            return "mctreasure";
        }

        @Override
        public @NotNull String getAuthor() {
            return "coma112";
        }

        @Override
        public @NotNull String getVersion() {
            return plugin.getDescription().getVersion();
        }

        @Override
        public boolean canRegister() {
            return true;
        }

        @Override
        public boolean persist() {
            return true;
        }

        @Override
        public String onRequest(OfflinePlayer player, @NotNull String params) {
            if (player == null) return "";

            String[] args = params.split("_");
            if (args.length < 2) return "Invalid placeholder";

            String type = args[0].toLowerCase();

            if (type.equals("time") && args.length >= 3 && args[1].equals("left")) {
                String chestId = args[2];
                TreasureChest chest = TreasureManager.getInstance().getTreasure(chestId);

                if (chest == null) return "Chest not found";

                Player onlinePlayer = player.getPlayer();

                if (onlinePlayer == null) return null;

                return chest.getTimeLeftDisplay(player.getPlayer());
            }

            args = params.split("_", 2);
            if (args.length < 2) return "Invalid placeholder";

            type = args[0].toLowerCase();
            String worldName = args[1];

            List<TreasureChest> chestsInWorld = TreasureManager.getInstance().getAllTreasures()
                    .stream()
                    .filter(chest -> chest.getLocation() != null &&
                            chest.getLocation().getWorld() != null &&
                            chest.getLocation().getWorld().getName().equals(worldName))
                    .toList();

            return switch (type) {
                case "remaining" -> {
                    long remainingChests = chestsInWorld.stream()
                            .filter(chest -> {
                                if (chest.getCooldownMillis() <= 0) return true;
                                return CooldownManager.getInstance()
                                        .checkCooldown(chest.getId(), player.getUniqueId(), chest.getCooldownMillis())
                                        .canOpen();
                            })
                            .count();
                    yield String.valueOf(remainingChests);
                }

                case "opened" -> {
                    long openedChests = chestsInWorld.stream()
                            .filter(chest -> {
                                if (chest.getCooldownMillis() <= 0) return false;
                                return !CooldownManager.getInstance()
                                        .checkCooldown(chest.getId(), player.getUniqueId(), chest.getCooldownMillis())
                                        .canOpen();
                            })
                            .count();
                    yield String.valueOf(openedChests);
                }

                case "all" -> String.valueOf(chestsInWorld.size());
                default -> "Invalid placeholder";
            };
        }
    }
}