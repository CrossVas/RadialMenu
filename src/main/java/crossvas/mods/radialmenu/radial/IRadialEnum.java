package crossvas.mods.radialmenu.radial;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;

/**
 * Represents a selectable entry in a radial menu, typically used to expose tool modes,
 * actions, or other interactive states to the player through a graphical radial interface.
 */
public interface IRadialEnum {

    /**
     * Returns the text label that should be shown in the radial menu for this mode or entry.
     *
     * @return a {@link ITextComponent} representing the display name for this entry.
     */
    ITextComponent getTextForDisplay();

    /**
     * Returns the resource location of the icon used to represent this entry in the radial menu.
     *
     * @return the {@link ResourceLocation} of the icon.
     */
    ResourceLocation getIcon();

    /**
     * Returns a detailed description of this mode or entry, typically shown
     * in a help dialog when the player selects the entry.
     *
     * @return a {@link ITextComponent} with a descriptive text for this entry.
     */
    ITextComponent getDescription();
}
