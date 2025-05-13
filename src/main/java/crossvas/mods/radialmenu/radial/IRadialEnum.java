package crossvas.mods.radialmenu.radial;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;

public interface IRadialEnum {
    ITextComponent getTextForDisplay();
    ResourceLocation getIcon();
    ITextComponent getDescription();
}
