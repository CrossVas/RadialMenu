package crossvas.mods.radialmenu.radial;

import java.util.List;

public interface IRadialModeItem {
    /**
     * Should return all mode groups for this item (can be one or more).
     */
    List<IRadialModeGroup<?>> getRadialGroups();
}

