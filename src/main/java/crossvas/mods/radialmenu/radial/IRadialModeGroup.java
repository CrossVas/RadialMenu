package crossvas.mods.radialmenu.radial;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;

import java.util.List;

/**
 * Represents a mode group for an item or tool that supports switching between different
 * {@link IRadialEnum}-based configurations via a radial menu interface.
 *
 * <p>This interface abstracts the logic for querying, updating, and interacting with an
 * item's mode. Each mode group typically corresponds to a single tool or system that
 * supports one set of modes (such as a tool with different behaviors or configurations).</p>
 *
 * @param <TYPE> the enum type representing the available modes, which must implement {@link IRadialEnum}
 */

public interface IRadialModeGroup<TYPE extends IRadialEnum> {

    /**
     * Returns the class that defines the available modes.
     *
     * <p>This is primarily used for reflection, deserialization, or generic handling of
     * the available values.</p>
     *
     * @return the class object of the mode type
     */
    Class<TYPE> getModeClass();

    List<TYPE> getAllModes();

    /**
     * Retrieves the current mode for the given {@link ItemStack}.
     *
     * <p>This typically involves reading NBT data
     * to determine which mode is active.</p>
     *
     * @param stack the item stack being queried
     * @return the current mode of the item
     */
    TYPE getCurrentMode(ItemStack stack);

    /**
     * Sets the given mode on the specified {@link ItemStack}, potentially performing
     * validation, NBT updates, or player feedback.
     *
     * <p>This method is responsible for persisting the selected mode and optionally notifying
     * the player of the change (e.g., via chat or HUD message).</p>
     *
     * @param player the player performing the action
     * @param stack the item stack being modified
     * @param mode the new mode to set
     */
    void setMode(PlayerEntity player, ItemStack stack, TYPE mode);

    /**
     * Determines whether the input key responsible for triggering the radial menu
     * is currently held down for the given player.
     *
     * @param player the player to check input state for
     * @return true if the relevant key is currently pressed, false otherwise
     */
    boolean getKeyStatusDown(PlayerEntity player);

    /**
     * Returns a textual message to display inside Radial Menu
     *
     * <p>This message provides user feedback and may be localized using
     * {@link net.minecraft.util.text.TranslationTextComponent}.</p>
     *
     * @return a {@link ITextComponent} representing the mode-switching message
     */
    ITextComponent getModeMessage();
}
