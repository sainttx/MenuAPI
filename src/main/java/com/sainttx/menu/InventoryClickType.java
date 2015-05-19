package com.sainttx.menu;

import org.bukkit.event.inventory.InventoryAction;

/**
 * Created by Matthew on 22/10/2014.
 */
public enum InventoryClickType {

    LEFT(true, false),
    SHIFT_LEFT(true, true),
    RIGHT(false, false),
    OTHER(false, false);

    /*
     * Whether or not this click is a left click
     */
    private boolean leftClick;

    /*
     * Whether or not this click is a shift click
     */
    private boolean shiftClick;

    /*
     * A new Click Type
     */
    InventoryClickType(boolean leftClick, boolean shiftClick) {
        this.leftClick = leftClick;
        this.shiftClick = shiftClick;
    }

    /**
     * Returns if this click is a left click
     *
     * @return True if this click type was a left click, false otherwise
     */
    public boolean isLeftClick() {
        return this.leftClick && this != OTHER;
    }

    /**
     * Returns if this click is a right click
     *
     * @return Tue if this click type was a right click, false otherwise
     */
    public boolean isRightClick() {
        return !this.leftClick && this != OTHER;
    }

    /**
     * Returns if this click was a shift left click
     *
     * @return True if the click was a result of a shift click, false otherwise
     */
    public boolean isShiftClick() {
        return this.shiftClick;
    }

    /**
     * Gets the InventoryClickType from an InventoryAction
     *
     * @param action The Inventory action
     * @return
     */
    public static InventoryClickType fromInventoryAction(InventoryAction action) {
        switch (action) {
            case PICKUP_ALL:
            case PLACE_SOME:
            case PLACE_ALL:
            case SWAP_WITH_CURSOR:
                return LEFT;

            case PICKUP_HALF:
            case PLACE_ONE:
                return RIGHT;

            case MOVE_TO_OTHER_INVENTORY:
                return SHIFT_LEFT;

            default:
                return OTHER;
        }
    }
}
