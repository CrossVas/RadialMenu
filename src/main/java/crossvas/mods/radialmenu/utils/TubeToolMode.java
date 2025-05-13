package crossvas.mods.radialmenu.utils;

import crossvas.mods.radialmenu.RadialMenu;
import crossvas.mods.radialmenu.radial.IRadialEnum;
import ic2.core.block.transport.item.TubeAction;
import ic2.core.utils.helpers.SanityHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;

import java.util.Locale;

public enum TubeToolMode implements IRadialEnum {
    BLOCK_OUTPUT(TubeAction.BLOCK_OUTPUT),
    EXTRA_EXTRACION_SIDE(TubeAction.EXTRA_EXTRACION_SIDE),
    REDSTONE_CONTROL(TubeAction.REDSTONE_CONTROL),
    OUTPUT_PRIORITY(TubeAction.OUTPUT_PRIORITY),
    ONLY_EXISTING(TubeAction.ONLY_EXISTING),
    EXTRACTION_CONNECTIVITY(TubeAction.SPECIAL_EXTRACTION_CONNECTIVITY),
    PULSE(TubeAction.PULSE);

    public static final TubeToolMode[] VALUES = values();
    public final ITextComponent formattedName;
    public final TubeAction action;

    TubeToolMode(TubeAction action) {
        this.action = action;
        this.formattedName = new StringTextComponent(SanityHelper.firstLetterUppercase(SanityHelper.toPascalCase(name().toLowerCase(Locale.ROOT))));
    }


    @Override
    public ITextComponent getTextForDisplay() {
        return this.formattedName;
    }

    @Override
    public ResourceLocation getIcon() {
        return RadialMenu.id("textures/gui/blank.png");
    }

    @Override
    public ITextComponent getDescription() {
        return this.action.getDesc();
    }

    public static TubeToolMode getByIndex(int index) {
        return VALUES[index % VALUES.length];
    }
}
