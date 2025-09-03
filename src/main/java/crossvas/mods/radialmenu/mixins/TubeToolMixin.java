package crossvas.mods.radialmenu.mixins;

import crossvas.mods.radialmenu.radial.IRadialMenu;
import crossvas.mods.radialmenu.radial.IRadialModeItem;
import crossvas.mods.radialmenu.utils.RadialComponents;
import ic2.core.item.tool.TubeTool;
import org.spongepowered.asm.mixin.Mixin;

import java.util.ArrayList;
import java.util.List;

@Mixin(TubeTool.class)
public class TubeToolMixin implements IRadialModeItem {

    @Override
    public List<IRadialMenu<?>> getRadialMenus() {
        List<IRadialMenu<?>> menus = new ArrayList<>();
        menus.add(new RadialComponents.TubeToolMenu());
        return menus;
    }
}
