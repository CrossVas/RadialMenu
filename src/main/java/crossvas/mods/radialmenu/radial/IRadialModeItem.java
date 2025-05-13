package crossvas.mods.radialmenu.radial;

import java.util.List;

/**
 * Represents an item that supports one or more radial mode groups, each of which
 * controls a different aspect of the item's behavior or configuration.
 *
 * <p>Implementing this interface allows an item to expose one or more {@link IRadialModeGroup}
 * instances, which can then be rendered and interacted with through a radial menu GUI.
 */

public interface IRadialModeItem {

    /**
     * Returns all {@link IRadialModeGroup} instances associated with this item.
     *
     * <p>Each group represents a distinct configurable mode set, and may be used to populate
     * multiple radial menus.</p>
     *
     * @return a list of {@link IRadialModeGroup} objects associated with this item
     */
    List<IRadialModeGroup<?>> getRadialGroups();
}

