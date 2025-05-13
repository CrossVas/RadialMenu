package crossvas.mods.radialmenu.mixins;

import crossvas.mods.radialmenu.radial.IRadialModeGroup;
import crossvas.mods.radialmenu.radial.IRadialModeItem;
import crossvas.mods.radialmenu.utils.ItemModeGroups;
import ic2.core.item.tool.TubeTool;
import org.spongepowered.asm.mixin.Mixin;

import java.util.ArrayList;
import java.util.List;

@Mixin(TubeTool.class)
public class TubeToolMixin implements IRadialModeItem {

    @Override
    public List<IRadialModeGroup<?>> getRadialGroups() {
        List<IRadialModeGroup<?>> groups = new ArrayList<>();
        groups.add(new ItemModeGroups.TubeToolConfigGroup());
        return groups;
    }
}
