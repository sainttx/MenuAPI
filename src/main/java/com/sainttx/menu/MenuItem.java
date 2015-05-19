package com.sainttx.menu;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Matthew on 22/10/2014.
 */
public abstract class MenuItem {

    private Menu menu;
    private Integer slot;
    private int number;
    private ItemStack icon;
    private String text = null;
    private List<String> descriptions = new ArrayList<String>();

    /**
     * Create a new menu item with the given title text on mouse over
     * Icon defaults to a piece of paper, and no number is displayed.
     *
     * @param icon The item Icon
     */
    public MenuItem(ItemStack icon) {
        this(null, icon);
    }

    /**
     * Create a new menu item with the given title text on mouse over
     * <p/>
     * Icon defaults to a piece of paper, and no number is displayed.
     *
     * @param text The title text to display on mouse over
     */
    public MenuItem(String text) {
        this(text, new ItemStack(Material.PAPER));
    }

    /**
     * Create a new menu item with the given title text on mouse over, and using
     * the given MaterialData as its icon
     *
     * @param text The title text to display on mouse over
     * @param icon The material to use as its icon
     */
    public MenuItem(String text, ItemStack icon) {
        this(text, icon, 1);
    }

    /**
     * Create a new menu item with the given title text on mouseover, using the
     * given MaterialData as its icon, and displaying the given number
     *
     * @param text   The title text to display on mouse over
     * @param icon   The material to use as its icon
     * @param number The number to display on the item (must be greater than 1)
     */
    public MenuItem(String text, ItemStack icon, int number) {
        if (text != null) {
            this.text = text;
        }
        this.icon = icon;
        this.number = number;
    }

    protected void addToMenu(Menu menu) {
        this.menu = menu;
    }

    protected void removeFromMenu(Menu menu) {
        if (this.menu == menu) {
            this.menu = null;
        }
    }

    /**
     * Get the menu on which this item resides
     *
     * @return The popup menu
     */
    public Menu getMenu() {
        return menu;
    }

    /**
     * Get the slot number for this item
     *
     * @return The slot number
     */
    public int getSlot() {
        return slot;
    }

    /**
     * Set the slot number for this item
     * <p/>
     * Used only on initialisation
     */
    public void setSlot(int slot) {
        this.slot = slot;
    }

    /**
     * Get the number displayed on this item. 1 for no number displayed
     *
     * @return The number
     */
    public int getNumber() {
        return number;
    }

    /**
     * Get the ItemStack used as the icon for this menu item
     *
     * @return The icon
     */
    public ItemStack getIcon() {
        return icon;
    }

    /**
     * Sets the ItemStack used as the icon for this menu item
     */
    public void setIcon(ItemStack icon) {
        this.icon = icon;
        menu.getInventory().setItem(slot, getIcon());
    }

    /**
     * Get the title text used as the mouse over text for this menu item
     *
     * @return The title text
     */
    public String getText() {
        return text;
    }

    /**
     * Sets the detailed description of a menu item to display on mouse over.
     * <p/>
     * Note that this does not automatically wrap text, so you must separate the
     * strings into lines
     *
     * @param lines The lines of text to display as a description
     */
    public void setDescriptions(List<String> lines) {
        descriptions = lines;
    }

    /**
     * Adds an extra line to the description of the menu item to display on
     * mouse over.
     * <p/>
     * Note that this does not automatically wrap text, so you must separate the
     * strings into multiple lines if they are too long.
     *
     * @param line The line to add to the display text description
     */
    public void addDescription(String line) {
        descriptions.add(line);
    }

    protected ItemStack getItemStack() {
        ItemStack slot = getIcon().clone();
        ItemMeta meta = slot.getItemMeta();
        if (meta.hasLore())
            meta.getLore().addAll(descriptions);
        else
            meta.setLore(descriptions);
        if (getText() != null)
            meta.setDisplayName(getText());

        slot.setItemMeta(meta);
        return slot;
    }

    /**
     * Called when a player clicks this menu item
     *
     * @param player The clicking player
     */
    public abstract void onClick(Player player, InventoryClickType clickType);

    /**
     * A menu item that does nothing when clicked
     */
    public static class UnclickableMenuItem extends MenuItem {

        /**
         * Create a new menu item with the given icon that won't do
         * anything when clicked
         *
         * @param icon The item Icon
         */
        public UnclickableMenuItem(ItemStack icon) {
            super(icon);
        }

        @Override
        public void onClick(Player player, InventoryClickType clickType) {
            return;
        }
    }
}
