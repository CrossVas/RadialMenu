package crossvas.mods.radialmenu.utils;

import crossvas.mods.radialmenu.radial.IRadialEnum;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;

import java.util.function.Function;

public class RadialEnumWrapper<TARGET> implements IRadialEnum {

    private final TARGET target;
    private final Function<TARGET, ITextComponent> textSupplier;
    private final Function<TARGET, ResourceLocation> iconSupplier;
    private final Function<TARGET, ITextComponent> descriptionSupplier;

    public RadialEnumWrapper(TARGET target,
                             Function<TARGET, ITextComponent> textSupplier,
                             Function<TARGET, ResourceLocation> iconSupplier,
                             Function<TARGET, ITextComponent> descriptionSupplier) {
        this.target = target;
        this.textSupplier = textSupplier;
        this.iconSupplier = iconSupplier;
        this.descriptionSupplier = descriptionSupplier;
    }

    @Override
    public ITextComponent getTextForDisplay() {
        return textSupplier.apply(target);
    }

    @Override
    public ResourceLocation getIcon() {
        return iconSupplier.apply(target);
    }

    @Override
    public ITextComponent getDescription() {
        return descriptionSupplier.apply(target);
    }

    public TARGET getTarget() {
        return target;
    }
}
