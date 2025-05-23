package net.netherpulse.nptreasure.gui.models.main;

import net.netherpulse.nptreasure.data.MenuController;
import net.netherpulse.nptreasure.gui.Menu;
import net.netherpulse.nptreasure.gui.models.TreasureItemsMenu;
import net.netherpulse.nptreasure.identifiers.ParticleTypes;
import net.netherpulse.nptreasure.identifiers.keys.ItemKeys;
import net.netherpulse.nptreasure.identifiers.keys.MenuKeys;
import net.netherpulse.nptreasure.identifiers.keys.MessageKeys;
import net.netherpulse.nptreasure.manager.TreasureManager;
import net.netherpulse.nptreasure.model.TreasureChest;
import net.netherpulse.nptreasure.sessions.ChatSessions;
import net.netherpulse.nptreasure.sessions.LocationSession;
import net.netherpulse.nptreasure.utils.PlaceholderUtils;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class TreasureEditMenu extends Menu {
    private final TreasureChest chest;

    public TreasureEditMenu(@NotNull MenuController menuController, @NotNull TreasureChest chest) {
        super(menuController);
        this.chest = chest;
    }

    @Override
    public void handleMenu(final @NotNull InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        ItemStack clickedItem = event.getCurrentItem();

        if (clickedItem == null || clickedItem.getType() == Material.AIR) return;

        event.setCancelled(true);

        switch (event.getSlot()) {
            case 10:
                player.closeInventory();
                ChatSessions.setName(player, chest, new TreasureEditMenu(menuController, chest));
                break;
            case 12:
                player.closeInventory();
                ChatSessions.setPermission(player, chest, new TreasureEditMenu(menuController, chest));
                break;
            case 14:
                player.closeInventory();
                ChatSessions.setCooldown(player, chest, new TreasureEditMenu(menuController, chest));
                break;
            case 16:
                player.closeInventory();
                ChatSessions.setSize(player, chest, new TreasureEditMenu(menuController, chest));
                break;
            case 29:
                player.closeInventory();
                LocationSession.startSettingLocation(player, chest);
                player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 0.5f, 1.0f);
                break;
            case 31:
                if (event.getClick() == ClickType.LEFT) {
                    player.closeInventory();
                    ChatSessions.setPushbackStrength(player, chest, new TreasureEditMenu(menuController, chest));
                } else if (event.getClick() == ClickType.DROP) {
                    chest.setPushbackEnabled(!chest.isPushbackEnabled());
                    player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 0.5f, 1.0f);
                    open();
                }
                break;
            case 33:
                if (event.getClick() == ClickType.LEFT) {
                    new TreasureHologramMenu(MenuController.getMenuUtils(player), chest).open();
                    player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 0.5f, 1.0f);
                } else if (event.getClick() == ClickType.DROP) {
                    chest.setHologramEnabled(!chest.isHologramEnabled());
                    if (chest.isHologramEnabled()) chest.setupHologram();
                    else chest.removeHologram();
                    player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 0.5f, 1.0f);
                    open();
                }
                break;
            case 22:
                new TreasureItemsMenu(MenuController.getMenuUtils(player), chest).open();
                player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 0.5f, 1.0f);
                break;
            case 35:
                if (event.getClick() == ClickType.LEFT) {
                    ParticleTypes currentType = chest.getParticleType();

                    if (currentType == null) chest.setParticleType(ParticleTypes.HEART);
                    else chest.setParticleType(currentType.next());

                    chest.setParticleDisplay(TreasureManager.getInstance().getParticleFromConfig(chest.getParticleType()));

                    if (chest.isParticleEnabled()) chest.setupParticleEffect();

                    player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 0.5f, 1.0f);
                    open();
                } else if (event.getClick() == ClickType.RIGHT) {
                    ParticleTypes currentType = chest.getParticleType();

                    if (currentType == null) chest.setParticleType(ParticleTypes.TORNADO);
                    else chest.setParticleType(currentType.previous());

                    chest.setParticleDisplay(TreasureManager.getInstance().getParticleFromConfig(chest.getParticleType()));
                    if (chest.isParticleEnabled()) chest.setupParticleEffect();

                    player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 0.5f, 1.0f);
                    open();
                } else if (event.getClick() == ClickType.DROP) {
                    chest.setParticleEnabled(!chest.isParticleEnabled());
                    if (chest.isParticleEnabled()) {
                        if (chest.getParticleType() == null) chest.setParticleType(ParticleTypes.HEART);
                        chest.setParticleDisplay(TreasureManager.getInstance().getParticleFromConfig(chest.getParticleType()));
                        chest.setupParticleEffect();
                    } else chest.removeParticleEffect();
                    player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 0.5f, 1.0f);
                    open();
                }
                break;
            case 49:
                TreasureManager.getInstance().saveTreasures();
                player.sendMessage(MessageKeys.SUCCESS_SAVE.getMessage());
                player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 0.5f, 1.0f);
                new TreasureOverviewMenu(MenuController.getMenuUtils(player)).open();
                break;
        }
    }

    @Override
    public void setMenuItems() {
        inventory.setItem(10, PlaceholderUtils.applyPlaceholders(ItemKeys.EDIT_NAME.getItem(), chest));
        inventory.setItem(12, PlaceholderUtils.applyPlaceholders(ItemKeys.EDIT_PERMISSION.getItem(), chest));
        inventory.setItem(14, PlaceholderUtils.applyPlaceholders(ItemKeys.EDIT_COOLDOWN.getItem(), chest));
        inventory.setItem(16, PlaceholderUtils.applyPlaceholders(ItemKeys.EDIT_SIZE.getItem(), chest));
        inventory.setItem(29, PlaceholderUtils.applyPlaceholders(ItemKeys.EDIT_LOCATION.getItem(), chest));
        inventory.setItem(31, PlaceholderUtils.applyPlaceholders(ItemKeys.EDIT_PUSHBACK.getItem(), chest));
        inventory.setItem(33, PlaceholderUtils.applyPlaceholders(ItemKeys.EDIT_HOLOGRAM.getItem(), chest));
        inventory.setItem(35, PlaceholderUtils.applyPlaceholders(ItemKeys.EDIT_PARTICLE.getItem(), chest));
        inventory.setItem(22, PlaceholderUtils.applyPlaceholders(ItemKeys.EDIT_ITEMS.getItem(), chest));
        inventory.setItem(49, PlaceholderUtils.applyPlaceholders(ItemKeys.SAVE_ITEM.getItem(), chest));
    }

    @Override
    public String getMenuName() {
        return MenuKeys.MENU_EDIT_TITLE.getString().replace("{name}", chest.getName());
    }

    @Override
    public int getSlots() {
        return MenuKeys.MENU_EDIT_SIZE.getInt();
    }

    @Override
    public int getMenuTick() {
        return 20;
    }
}