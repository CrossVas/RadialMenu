package crossvas.mods.radialmenu.mixins;

import crossvas.mods.radialmenu.radial.IRadial;
import crossvas.mods.radialmenu.radial.IRadialMenu;
import crossvas.mods.radialmenu.radial.IRadialModeItem;
import crossvas.mods.radialmenu.screen.RadialToggleComponent;
import crossvas.mods.radialmenu.utils.RadialComponents;
import ic2.core.item.tool.PainterTool;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.StringTextComponent;
import org.spongepowered.asm.mixin.Mixin;

import java.util.ArrayList;
import java.util.List;

@Mixin(PainterTool.class)
public class PainterMixin implements IRadialModeItem {

    @Override
    public List<IRadialMenu<?>> getRadialMenus() {
        List<IRadialMenu<?>> menus = new ArrayList<>();
        menus.add(new RadialComponents.PainterMenu());
        return menus;
    }

    @Override
    public List<IRadial> getRadialComponents(ItemStack stack) {
        return new RadialToggleComponent.Builder(stack)
                .of("autoRefill", new StringTextComponent("Auto Refill Mode"), "Auto Refill: %s")
                .build();
    }
}
