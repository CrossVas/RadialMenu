package crossvas.mods.radialmenu.screen;

import com.mojang.blaze3d.matrix.MatrixStack;
import crossvas.mods.radialmenu.radial.IRadialModeItem;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.StringTextComponent;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.stream.Collectors;

public class RadialScreen extends Screen {

    private final RadialGroup<?> current;

    public RadialScreen(ItemStack stack, IRadialModeItem item, @Nullable Class<?> openToGroupClass) {
        super(StringTextComponent.EMPTY);
        List<RadialGroup<?>> groups = item.getRadialGroups().stream()
                .map(group -> new RadialGroup<>(stack, group))
                .collect(Collectors.toList());

        this.current = openToGroupClass != null
                ? groups.stream()
                .filter(g -> g.group.getModeClass().equals(openToGroupClass))
                .findFirst()
                .orElse(groups.get(0))
                : groups.get(0);
    }

    @Override
    public void render(@Nonnull MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        if (current != null) {
            current.render(matrixStack, mouseX, mouseY);
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (current != null) {
            current.confirmSelection();
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
        if (current != null) {
            current.confirmSelection();
        }
    }
}
