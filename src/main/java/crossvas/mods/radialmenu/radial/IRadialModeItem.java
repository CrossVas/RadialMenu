package crossvas.mods.radialmenu.radial;

import net.minecraft.item.ItemStack;

import java.util.Collections;
import java.util.List;

/**
 * Represents an item that supports one or more radial mode groups, each of which
 * controls a different aspect of the item's behavior or configuration.
 *
 * <p>Implementing this interface allows an item to expose one or more {@link IRadialMenu}
 * instances, which can then be rendered and interacted with through a radial menu GUI.
 */

public interface IRadialModeItem {

    /**
     * Returns all {@link IRadialMenu} instances associated with this item for logic and mode handling.
     *
     * <p>Each menu represents a distinct configurable mode set, typically backed by an {@link IRadialEnum},
     * and is responsible for interpreting and applying mode changes to the item.</p>
     *
     * @return a list of {@link IRadialMenu} objects used for mode logic
     */
    List<IRadialMenu<?>> getRadialMenus();

    /**
     * Returns all {@link IRadial} instances used to visually render with the radial menu for this item.
     * <p>These are not linked to a specific {@link IRadialMenu}, instead these are displayed across multiple menus.
     *
     * <p>Components may include toggles, mode selectors, icons, or other interactive elements which have a simpler logic.
     *
     * @param stack the {@link ItemStack} being queried
     * @return a list of {@link IRadial} objects used to render the radial menu
     */

    default List<IRadial> getRadialComponents(ItemStack stack) {
        return Collections.emptyList();
    }
}

