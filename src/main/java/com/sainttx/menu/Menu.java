package com.sainttx.menu;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Created by Matthew on 22/10/2014.
 */
public class Menu implements InventoryHolder {

    private Map<Integer, MenuItem> items = new HashMap<Integer, MenuItem>();
    private Inventory inventory;
    private String title;
    private int rows;
    private boolean exitOnClickOutside = false;
    private MenuAPI.MenuCloseBehaviour menuCloseBehaviour;
    private boolean bypassMenuCloseBehaviour = false;
    private Menu parentMenu;

    /**
     * Creates a new PopupMenu with the given title and number of rows of slots
     * for menu item. There are 9 slots per row.
     *
     * @param title The title to display at the top of the menu
     * @param rows  The number of rows of slots for menu items
     */
    public Menu(String title, int rows) {
        this(title, rows, null);
    }

    public Menu(String title, int rows, Menu parentMenu) {
        this.title = title;
        this.rows = rows;
        this.parentMenu = parentMenu;
    }

    /**
     * Change what the menu should do when it is closed by a player. Nothing by
     * default.
     *
     * @param menuCloseBehaviour The new menu closing behaviour
     */
    public void setMenuCloseBehaviour(MenuAPI.MenuCloseBehaviour menuCloseBehaviour) {
        this.menuCloseBehaviour = menuCloseBehaviour;
    }

    /**
     * Get the current menu closing behaviour, what the menu does when it is
     * closed by a player.
     *
     * @return the MenuCloseBehaviour for this menu
     */
    public MenuAPI.MenuCloseBehaviour getMenuCloseBehaviour() {
        return menuCloseBehaviour;
    }

    public void setBypassMenuCloseBehaviour(boolean bypassMenuCloseBehaviour) {
        this.bypassMenuCloseBehaviour = bypassMenuCloseBehaviour;
    }

    public boolean bypassMenuCloseBehaviour() {
        return bypassMenuCloseBehaviour;
    }

    /**
     * Sets whether the menu should exit on clicking outside the menu box
     *
     * @param exit True if the menu should close
     */
    public void setExitOnClickOutside(boolean exit) {
        this.exitOnClickOutside = exit;
    }

    /**
     * Get all menu items
     *
     * @return All the menu items
     */
    public Map<Integer, MenuItem> getMenuItems() {
        return items;
    }

    /**
     * Adds an item to the menu at the specified position
     *
     * @param item The menu item to add
     * @param x    The column position starting from 0 at the left
     * @param y    The row position starting from 0 at the top
     * @return True if the menu item was added, false if there was already a
     * menu item in this slot
     */
    public boolean addMenuItem(MenuItem item, int x, int y) {
        return addMenuItem(item, y * 9 + x);
    }

    /**
     * Adds an item to the menu at the specified index, if you are more
     * comfortable with minecraft inventory index locations
     *
     * @param item  The menu item to add
     * @param index The index location to place it
     * @return True if the menu item was added, false if there was already a
     * menu item in this slot
     */
    public boolean addMenuItem(MenuItem item, int index) {
        ItemStack slot = getInventory().getItem(index);
        if (slot != null && slot.getType() != Material.AIR) {
            return false;
        }
        item.setSlot(index);
        getInventory().setItem(index, item.getItemStack());
        items.put(index, item);
        item.addToMenu(this);
        return true;
    }

    /**
     * Removes an existing menu item from a menu at the specified position. This
     * can be used to create dynamic menus, however beware that if you change a
     * menu it will change for all viewers. You should use the
     * PopupMenuAPI.cloneMenu method if you want a copy of a menu that can be
     * safely changed.
     *
     * @param x The column position starting from 0 at the left
     * @param y The row position starting from 0 at the top
     * @return True, if there was a menu item to remove
     */
    public boolean removeMenuItem(int x, int y) {
        return removeMenuItem(y * 9 + x);
    }

    /**
     * Removes an existing menu item from a menu at the specified slot index, if
     * you are more comfortable with Minecraft inventory slot indices. This can
     * be used to create dynamic menus, however beware that if you change a menu
     * it will change for all viewers. You should use the PopupMenuAPI.cloneMenu
     * method if you want a copy of a menu that can be safely changed.
     *
     * @return True, if there was a menu item to remove
     */
    public boolean removeMenuItem(int index) {
        ItemStack slot = getInventory().getItem(index);
        if (slot == null || slot.getType().equals(Material.AIR)) {
            return false;
        }
        getInventory().clear(index);
        items.remove(index).removeFromMenu(this);
        return true;
    }

    protected void selectMenuItem(Player player, int index, InventoryClickType clickType) {
        if (items.containsKey(index)) {
            MenuItem item = items.get(index);
            item.onClick(player, clickType);
        }
    }

    /**
     * Opens a menu for a player.
     * <p/>
     * Important note: This should not be used to switch from one menu to
     * another within the same tick as there is an error with Bukkit inventories
     * that will cause it to glitch. Instead delay 1 tick before opening or use
     * the switchMenu method to do it for you.
     * <p/>
     * Be aware that if you make changes to a menu with multiple viewers it will
     * change for all of them. You should use the PopupMenuAPI.cloneMenu method
     * if you want a copy of a menu that can be safely changed. Be sure to
     * destroy it if you do not intend to use it again.
     *
     * @param player The player to open the menu for
     */
    public void openMenu(Player player) {
        if (!getInventory().getViewers().contains(player))
            player.openInventory(getInventory());
    }

    /**
     * Closes a menu for a player
     *
     * @param player
     */
    public void closeMenu(Player player) {
        if (getInventory().getViewers().contains(player)) {
            getInventory().getViewers().remove(player);
            player.closeInventory();
        }
    }

    /**
     * Get the parent menu
     *
     * @return The parent menu (null if not set)
     */
    public Menu getParent() {
        return parentMenu;
    }

    /**
     * Closes this menu and opens another in the next tick. This avoids the
     * Bukkit glitchiness caused by closing and opening inventories in the same
     * tick.
     *
     * @param player The player switching menus
     * @param toMenu The menu to switch to
     */
    public void switchMenu(Player player, Menu toMenu) {
        MenuAPI.switchMenu(player, this, toMenu);
    }

    /**
     * Gets the inventory used by this menu. It is not recommended to access or
     * change the inventory directly. Please use the API methods provided.
     *
     * @return The Inventory used to create the menu
     */
    @Override
    public Inventory getInventory() {
        if (inventory == null) {
            inventory = Bukkit.createInventory(this, rows * 9, title);
        }
        return inventory;
    }

    /**
     * Determines whether this menu should close if a player clicks outside of
     * it
     *
     * @return True, if the menu should close
     */
    public boolean exitOnClickOutside() {
        return exitOnClickOutside;
    }

    @Override
    protected Menu clone() {
        Menu clone = new Menu(title, rows);
        clone.setExitOnClickOutside(exitOnClickOutside);
        clone.setMenuCloseBehaviour(menuCloseBehaviour);
        for (Entry<Integer, MenuItem> entry : items.entrySet()) {
            clone.addMenuItem(entry.getValue(), entry.getKey());
        }
        return clone;
    }

    /**
     * Updates this menu after changes are made so that viewers can instantly
     * see them
     */
    public void updateMenu() {
        for (HumanEntity entity : getInventory().getViewers()) {
            ((Player) entity).updateInventory();
        }
    }
}
