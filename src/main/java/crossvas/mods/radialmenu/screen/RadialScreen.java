package crossvas.mods.radialmenu.screen;

import com.mojang.blaze3d.matrix.MatrixStack;
import crossvas.mods.radialmenu.radial.IRadial;
import crossvas.mods.radialmenu.radial.IRadialModeItem;
import net.minecraft.client.MainWindow;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldVertexBufferUploader;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.StringTextComponent;
import org.lwjgl.opengl.GL11;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.stream.Collectors;

public class RadialScreen extends Screen {

    private final RadialModeComponent<?> currentMenu;
    private final List<IRadial> components;

    public RadialScreen(ItemStack stack, IRadialModeItem item, @Nullable Class<?> openToGroupClass) {
        super(StringTextComponent.EMPTY);
        this.components = item.getRadialComponents(stack);
        List<RadialModeComponent<?>> menus = item.getRadialMenus().stream()
                .map(group -> new RadialModeComponent<>(stack, group))
                .collect(Collectors.toList());
        this.currentMenu = openToGroupClass != null
                ? menus.stream()
                .filter(g -> g.group.getModeClass().equals(openToGroupClass))
                .findFirst()
                .orElse(menus.get(0))
                : menus.get(0);
    }

    @Override
    public void render(@Nonnull MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        MainWindow window = Minecraft.getInstance().getWindow();

        float centerX = window.getGuiScaledWidth() / 2F;
        float centerY = window.getGuiScaledHeight() / 2F;

        BufferBuilder buf = Tessellator.getInstance().getBuilder();
        buf.begin(GL11.GL_TRIANGLES, DefaultVertexFormats.POSITION_COLOR);
        // render menu
        if (currentMenu != null) {
            currentMenu.renderBackground(matrixStack, buf, mouseX, mouseY, centerX, centerY, 0);
        }
        // render additional components: toggles
        int total = components.size();
        for (int i = 0; i < total; i++) {
            components.get(i).renderBackground(matrixStack, buf, mouseX, mouseY, centerX, centerY, i);
        }
        buf.end();
        WorldVertexBufferUploader.end(buf);

        // render menu
        if (currentMenu != null) {
            currentMenu.renderForeground(matrixStack);
        }
        // render additional components: toggles
        for (int i = 0; i < total; i++) {
            components.get(i).renderForeground(matrixStack);
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        for (IRadial component : components) {
            if (component instanceof RadialToggleComponent) {
                if (((RadialToggleComponent) component).isHovered(mouseX, mouseY)) {
                    component.confirmSelection();
                }
            } else {
                component.confirmSelection(); // just in case we add more types
            }
        }
        if (currentMenu != null) { // handle menus
            currentMenu.confirmSelection();
        }
        return true;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        return true;
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public void removed() {
        for (IRadial component : components) {
            if (!(component instanceof RadialToggleComponent)) { // just in case we add more types
                component.confirmSelection();
            }
        }
        if (currentMenu != null) { // handle menus
            currentMenu.confirmSelection();
        }
    }
}
