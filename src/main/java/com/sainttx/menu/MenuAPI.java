package com.sainttx.menu;

import org.bukkit.Bukkit;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * Created by Matthew on 22/10/2014.
 */
public class MenuAPI implements Listener {

    /*
     * The only MenuAPI instance
     */
    private static MenuAPI i = null;

    /**
     * Singleton constructor
     */
    protected MenuAPI() {

    }

    public static MenuAPI getMenuAPI() {
        return i == null ? i = new MenuAPI() : i;
    }

    /**
     * Create a new pop-up menu and stores it for later use
     *
     * @param title The menu title
     * @param rows  The number of rows on the menu
     * @return The menu
     */
    public Menu createMenu(String title, int rows) {
        return new Menu(title, rows);
    }

    /**
     * Creates an exact copy of an existing pop-up menu. This is intended to be
     * used for creating dynamic pop-up menus for individual players. Be sure to
     * call destroyMenu for menus that are no longer needed.
     *
     * @param menu The menu to clone
     * @return The cloned copy
     */
    public Menu cloneMenu(Menu menu) {
        return menu.clone();
    }

    /**
     * Destroys an existing menu, and closes it for any viewers
     * <p/>
     * Please note: you should not store any references to destroyed menus
     *
     * @param menu The menu to destroy
     */
    public void removeMenu(Menu menu) {
        for (HumanEntity viewer : menu.getInventory().getViewers()) {
            if (viewer instanceof Player) {
                menu.closeMenu((Player) viewer);
            } else {
                viewer.closeInventory();
            }
        }
    }

    /**
     * Due to a bug with inventories, switching from one menu to another in the
     * same tick causes glitchiness. In order to prevent this, the opening must
     * be done in the next tick. This is a convenience method to perform this
     * task for you.
     *
     * @param player   The player switching menus
     * @param fromMenu The menu the player is currently viewing
     * @param toMenu   The menu the player is switching to
     */
    public static void switchMenu(final Player player, Menu fromMenu, final Menu toMenu) {
        fromMenu.closeMenu(player);
        new BukkitRunnable() {
            @Override
            public void run() {
                toMenu.openMenu(player);
            }
        }.runTask(Bukkit.getPluginManager().getPlugin("Core"));
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onMenuItemClicked(final InventoryClickEvent event) {
        Inventory inventory = event.getInventory();
        if (inventory.getHolder() instanceof Menu) {
            event.setCancelled(true);
            ((Player) event.getWhoClicked()).updateInventory();

            switch (event.getAction()) {
                case DROP_ALL_CURSOR:
                case DROP_ALL_SLOT:
                case DROP_ONE_CURSOR:
                case DROP_ONE_SLOT:
                case PLACE_ALL:
                case PLACE_ONE:
                case PLACE_SOME:
                case COLLECT_TO_CURSOR:
                case UNKNOWN:
                    return;
                default:
                    break;
            }

            Menu menu = (Menu) inventory.getHolder();
            if (event.getWhoClicked() instanceof Player) {
                Player player = (Player) event.getWhoClicked();
                if (event.getSlotType() == InventoryType.SlotType.OUTSIDE) {
                    // Quick exit for a menu, click outside of it
                    if (menu.exitOnClickOutside())
                        menu.closeMenu(player);
                } else {
                    int index = event.getRawSlot();
                    if (index < inventory.getSize()) {
                        if (event.getAction() != InventoryAction.NOTHING)
                            menu.selectMenuItem(player, index, InventoryClickType.fromInventoryAction(event.getAction()));
                    } else {
                        // If they want to mess with their inventory they don't need to do so in a menu
                        if (menu.exitOnClickOutside())
                            menu.closeMenu(player);
                    }
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onMenuClosed(InventoryCloseEvent event) {
        if (event.getPlayer() instanceof Player) {
            Inventory inventory = event.getInventory();
            if (inventory.getHolder() instanceof Menu) {
                Menu menu = (Menu) inventory.getHolder();
                MenuCloseBehaviour menuCloseBehaviour = menu.getMenuCloseBehaviour();
                if (menuCloseBehaviour != null) {
                    menuCloseBehaviour.onClose((Player) event.getPlayer(), menu, menu.bypassMenuCloseBehaviour());
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerLogoutCloseMenu(PlayerQuitEvent event) {
        if (event.getPlayer().getOpenInventory() == null ||
                event.getPlayer().getOpenInventory().getTopInventory().getHolder() instanceof Menu == false)
            return;

        Menu menu = (Menu) event.getPlayer().getOpenInventory().getTopInventory().getHolder();
        menu.setBypassMenuCloseBehaviour(true);
        menu.setMenuCloseBehaviour(null);
        event.getPlayer().closeInventory();
    }

    public interface MenuCloseBehaviour {
        /**
         * Called when a player closes a menu
         *
         * @param player The player closing the menu
         */
        public void onClose(Player player, Menu menu, boolean bypassMenuCloseBehaviour);
    }
}
