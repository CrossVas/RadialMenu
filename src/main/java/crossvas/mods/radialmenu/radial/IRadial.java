package crossvas.mods.radialmenu.radial;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.BufferBuilder;

/**
 * Represents a visual element within a radial menu interface.
 *
 * <p>Components are responsible for rendering interactive UI elements such as toggles, icons,
 * labels, or selection rings. They may also handle client-side selection logic, but do not
 * directly modify item state or perform server-side actions.</p>
 *
 * <p>Each component is rendered in two phases:
 * <ul>
 *   <li>{@link #renderBackground(MatrixStack, BufferBuilder, int, int, float, float, int)} —
 *       used for batched geometry such as torus rings or gradients.</li>
 *   <li>{@link #renderForeground(MatrixStack)} —
 *       used for overlays like icons, text, and textures.</li>
 * </ul>
 * </p>
 *
 * <p>When a component is selected by the user (e.g. via click or hover), {@link #confirmSelection()}
 * is called to trigger any client-side feedback or send a packet to the server.</p>
 */

public interface IRadial {

    /**
     * Renders the background geometry of this component, typically using batched vertex data.
     *
     * @param stack   the current matrix stack
     * @param buf     the shared buffer builder for geometry
     * @param mouseX  the current mouse X position
     * @param mouseY  the current mouse Y position
     * @param centerX the center X of the radial menu
     * @param centerY the center Y of the radial menu
     * @param index   the index of this component in the menu
     */
    void renderBackground(MatrixStack stack, BufferBuilder buf, int mouseX, int mouseY, float centerX, float centerY, int index);

    /**
     * Renders the foreground overlay of this component, such as icons, labels, or textures.
     *
     * @param stack the current matrix stack
     */
    void renderForeground(MatrixStack stack);

    /**
     * Called when the user selects the component.
     *
     * <p>This method may trigger client-side effects or send a packet to the server
     * to apply a change. It does not directly modify item state.</p>
     */
    void confirmSelection();
}
