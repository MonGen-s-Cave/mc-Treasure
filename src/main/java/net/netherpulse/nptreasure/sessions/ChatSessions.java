package net.netherpulse.nptreasure.sessions;

import net.coma112.easierchatsetup.EasierChatSetup;
import net.netherpulse.nptreasure.NpTreasure;
import net.netherpulse.nptreasure.gui.models.main.TreasureEditMenu;
import net.netherpulse.nptreasure.gui.models.main.TreasureHologramMenu;
import net.netherpulse.nptreasure.identifiers.keys.MessageKeys;
import net.netherpulse.nptreasure.model.TreasureChest;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class ChatSessions {
    private static final NpTreasure plugin = NpTreasure.getInstance();

    public static void setPermission(@NotNull Player player, @NotNull TreasureChest chest, @NotNull TreasureEditMenu menu) {
        EasierChatSetup setup = new EasierChatSetup(plugin);

        setup.setTime(15)
                .onInput(input -> {
                    chest.setPermission(input);
                    player.sendMessage(MessageKeys.SESSION_PERMISSION_INPUT.getMessage().replace("{permission}", input));
                    player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 0.5f, 1.0f);
                    menu.open();
                })
                .onFail(() -> player.sendMessage(MessageKeys.SESSION_PERMISSION_FAILED.getMessage()))
                .setCancel("cancel")
                .startSession(player);

        player.sendMessage(MessageKeys.SESSION_PERMISSION_START.getMessage());
    }

    public static void setName(@NotNull Player player, @NotNull TreasureChest chest, @NotNull TreasureEditMenu menu) {
        EasierChatSetup setup = new EasierChatSetup(plugin);

        setup.setTime(15)
                .onInput(input -> {
                    chest.setName(input);
                    player.sendMessage(MessageKeys.SESSION_NAME_INPUT.getMessage().replace("{name}", input));
                    player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 0.5f, 1.0f);
                    menu.open();
                })
                .onFail(() -> player.sendMessage(MessageKeys.SESSION_NAME_FAILED.getMessage()))
                .setCancel("cancel")
                .startSession(player);

        player.sendMessage(MessageKeys.SESSION_NAME_START.getMessage());
    }

    public static void setCooldown(@NotNull Player player, @NotNull TreasureChest chest, @NotNull TreasureEditMenu menu) {
        EasierChatSetup setup = new EasierChatSetup(plugin);

        setup.setTime(15)
                .onInput(input -> {
                    try {
                        long value = Long.parseLong(input);
                        long cooldownMillis = TimeUnit.SECONDS.toMillis(value);
                        chest.setCooldownMillis(cooldownMillis);
                        player.sendMessage(MessageKeys.SESSION_COOLDOWN_INPUT.getMessage().replace("{cooldown}", String.valueOf(value)));
                        player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 0.5f, 1.0f);
                    } catch (NumberFormatException exception) {
                        player.sendMessage(MessageKeys.SESSION_COOLDOWN_INVALID_FORMAT.getMessage());
                        player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 0.5f, 1.0f);
                    }
                    menu.open();
                })
                .onFail(() -> player.sendMessage(MessageKeys.SESSION_COOLDOWN_FAILED.getMessage()))
                .setCancel("cancel")
                .startSession(player);

        player.sendMessage(MessageKeys.SESSION_COOLDOWN_START.getMessage());
    }

    public static void setSize(@NotNull Player player, @NotNull TreasureChest chest, @NotNull TreasureEditMenu menu) {
        EasierChatSetup setup = new EasierChatSetup(plugin);

        setup.setTime(15)
                .onInput(input -> {
                    try {
                        int value = Integer.parseInt(input);
                        if (value % 9 != 0 || value < 9 || value > 54) {
                            player.sendMessage(MessageKeys.SESSION_SIZE_INVALID_SIZE.getMessage());
                            player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 0.5f, 1.0f);
                        } else {
                            chest.setSize(value);
                            player.sendMessage(MessageKeys.SESSION_SIZE_INPUT.getMessage().replace("{size}", String.valueOf(value)));
                            player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 0.5f, 1.0f);
                        }
                    } catch (NumberFormatException exception) {
                        player.sendMessage(MessageKeys.SESSION_SIZE_INVALID_FORMAT.getMessage());
                        player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 0.5f, 1.0f);
                    }
                    menu.open();
                })
                .onSuccess(menu::open)
                .onFail(() -> player.sendMessage(MessageKeys.SESSION_SIZE_FAILED.getMessage()))
                .setCancel("cancel")
                .startSession(player);

        player.sendMessage(MessageKeys.SESSION_SIZE_START.getMessage());
    }

    public static void setPushbackStrength(@NotNull Player player, @NotNull TreasureChest chest, @NotNull TreasureEditMenu menu) {
        EasierChatSetup setup = new EasierChatSetup(plugin);

        setup.setTime(15)
                .onInput(input -> {
                    try {
                        double value = Double.parseDouble(input);

                        if (value < 0 || value > 5) {
                            player.sendMessage(MessageKeys.SESSION_PUSHBACK_INVALID_STRENGTH.getMessage());
                            player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 0.5f, 1.0f);
                        } else {
                            chest.setPushbackStrength(value);
                            player.sendMessage(MessageKeys.SESSION_PUSHBACK_INPUT.getMessage().replace("{pushback}", String.valueOf(value)));
                            player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 0.5f, 1.0f);
                        }
                    } catch (NumberFormatException exception) {
                        player.sendMessage(MessageKeys.SESSION_PUSHBACK_INVALID_FORMAT.getMessage());
                        player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 0.5f, 1.0f);
                    }
                    menu.open();
                })
                .onSuccess(menu::open)
                .onFail(() -> player.sendMessage(MessageKeys.SESSION_PUSHBACK_FAILED.getMessage()))
                .setCancel("cancel")
                .startSession(player);

        player.sendMessage(MessageKeys.SESSION_PUSHBACK_START.getMessage());
    }

    public static void editHologramLine(@NotNull Player player, int lineIndex, @NotNull TreasureHologramMenu menu) {
        EasierChatSetup setup = new EasierChatSetup(plugin);

        setup.setTime(30)
                .onInput(input -> {
                    List<String> hologramLines = menu.getHologramLines();

                    if (lineIndex < hologramLines.size()) {
                        if (input.equalsIgnoreCase("%blank%")) {
                            hologramLines.set(lineIndex, "%blank%");
                            player.sendMessage(MessageKeys.SESSION_HOLOGRAM_BLANK.getMessage());
                        } else {
                            hologramLines.set(lineIndex, input);
                            player.sendMessage(MessageKeys.SESSION_HOLOGRAM_INPUT.getMessage().replace("{content}", input));
                        }

                        menu.updateHologramInRealTime();
                        player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 0.5f, 1.0f);
                    }

                    menu.open();
                })

                .onSuccess(menu::open)
                .onFail(() -> player.sendMessage(MessageKeys.SESSION_HOLOGRAM_FAILED.getMessage()))
                .setCancel("cancel")
                .startSession(player);

        player.sendMessage(MessageKeys.SESSION_HOLOGRAM_START.getMessage());
    }
}