package crossvas.mods.radialmenu.screen;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import crossvas.mods.radialmenu.RadialMenu;
import crossvas.mods.radialmenu.network.RadialMenuNetwork;
import crossvas.mods.radialmenu.network.RadialModeChangePacket;
import crossvas.mods.radialmenu.radial.IRadialEnum;
import crossvas.mods.radialmenu.radial.IRadialModeGroup;
import crossvas.mods.radialmenu.utils.RenderHelper;
import crossvas.mods.radialmenu.utils.StringHelper;
import ic2.core.IC2;
import net.minecraft.client.MainWindow;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;

import static crossvas.mods.radialmenu.utils.RenderHelper.*;

public class RadialGroup<MODE extends Enum<MODE> & IRadialEnum> {

    private final Class<MODE> enumClass;
    private final MODE[] types;
    final IRadialModeGroup<MODE> group;
    private final ItemStack stack;
    private MODE selection = null;
    MODE lastSelectedMode = null;

    RadialGroup(ItemStack stack, IRadialModeGroup<MODE> group) {
        this.enumClass = group.getModeClass();
        this.types = enumClass.getEnumConstants();
        this.group = group;
        this.stack = stack;
    }

    void render(MatrixStack matrixStack, int mouseX, int mouseY) {
        Minecraft mc = Minecraft.getInstance();
        FontRenderer font = mc.font;
        MainWindow window = mc.getWindow();

        float centerX = window.getGuiScaledWidth() / 2F;
        float centerY = window.getGuiScaledHeight() / 2F;
        int activeModes = types.length;

        // --- Render radial ring ---
        matrixStack.pushPose();
        RenderSystem.enableBlend();
        RenderSystem.disableTexture();

        matrixStack.translate(centerX, centerY, 0); // Translate for radial ring only

        // base
        RenderSystem.color4f(0F, 0F, 0F, 0.5F);
        RenderHelper.drawTorus(matrixStack, 0, 360);

        // center margin
        RenderSystem.color4f(0F, 0F, 0F, 1F);
        RenderHelper.drawTorus(matrixStack, (int) (INNER - 6), (int) (INNER - 4), 0, 360);

        // center base
        RenderSystem.color4f(0F, 0F, 0F, 0.8F);
        RenderHelper.drawTorus(matrixStack, 0, (int) (INNER - 4), 0, 360);
        int[] starSlots = getCenteredStarSlots(types.length);
        MODE cur = group.getCurrentMode(stack);
        if (cur != null) {

            int section = cur.ordinal();
            float sectionStartAngle = -90F + 360F * (-0.5F + section) / 8;

            // current
            RenderSystem.color4f(0F, 0F, 0F, 0.3F);
            RenderHelper.drawTorus(matrixStack, sectionStartAngle, 360F / 8);

            double xDiff = mouseX - centerX;
            double yDiff = mouseY - centerY;
            double dist = xDiff * xDiff + yDiff * yDiff;

            if (dist >= SELECT_RADIUS * SELECT_RADIUS) {
                float angle = (float) Math.toDegrees(Math.atan2(yDiff, xDiff));
                float selectionAngle = angle + 90F + (360F / (2F * 8));
                selectionAngle = (selectionAngle + 360F) % 360F;
                int rawIndex = (int) (selectionAngle * 8 / 360F);
                for (int i = 0; i < starSlots.length; i++) {
                    if (starSlots[i] == rawIndex) {
                        selection = types[i];
                        break;
                    }
                }
;
                float startAngle = 360F * (-0.5F / 8) + angle;
                float sizeAngle = 360F / 8;

                // draw selection line
                RenderSystem.color4f(1F, 1F, 1F, 1F);
                RenderHelper.drawTorus(matrixStack, INNER - 2.95F, INNER - 1f, startAngle, sizeAngle);

                float hoveredStartAngle = -90F + 360F * (-0.5F + rawIndex) / 8;
                // selection
                RenderSystem.color4f(0F, 0F, 0F, 0.7F);
                RenderHelper.drawTorus(matrixStack, hoveredStartAngle, 360F / 8);
            } else {
                selection = null;
            }
        }

        // star
        RenderSystem.color4f(1F, 1F, 1F, .5F);
        RenderHelper.drawStar(matrixStack, 0, 0, OUTER + 5, 8);

        RenderSystem.color4f(1, 1, 1, 1);
        matrixStack.popPose();

        // --- Render icons & scrolling text ---
        RenderSystem.enableTexture();
        RenderSystem.color4f(1, 1, 1, 1);

        for (int i = 0; i < types.length; i++) {
            MODE type = types[i];
            int starIndex = starSlots[i];
            double angle = Math.toRadians(270 + 360 * ((float) starIndex / 8));
            float offsetX = (float) Math.cos(angle) * (INNER + OUTER) / 2F;
            float offsetY = (float) Math.sin(angle) * (INNER + OUTER) / 2F;
            float x = centerX + offsetX;
            float y = centerY + offsetY;

            float angleDeg = (float) Math.toDegrees(angle);

            // draw icon
            Minecraft.getInstance().textureManager.bind(type.getIcon());
            AbstractGui.blit(matrixStack, Math.round(x - 12), Math.round(y - 15), 24, 24, 0, 0, 18, 18, 18, 18);

            if (selection == type) {
                // Play the sound only once if the mode is selected and different from the previous mode
                if (lastSelectedMode != selection) {
                    IC2.AUDIO.playSound(Minecraft.getInstance().player, RadialMenu.id("sounds/selection.ogg"));
                    lastSelectedMode = selection; // Update the last selected mode
                }
            }
        }

        float centerLabelWidth = 108;
        float centerLabelHeight = 10;

        if (selection != null) {
            ITextComponent label = selection.getTextForDisplay();
            StringHelper.drawScrollingString(matrixStack, font, label,
                    centerX, centerY - font.lineHeight - 4, centerLabelWidth, centerLabelHeight,
                    0xFFFFFFFF);
            StringHelper.drawScrollingString(matrixStack, font, selection.getDescription().copy().withStyle(TextFormatting.GRAY),
                    centerX, centerY, centerLabelWidth, centerLabelHeight,
                    0xFFFFFFFF);
        }

        StringHelper.drawScrollingString(matrixStack, font, group.getModeMessage(),
                centerX, centerY + font.lineHeight + 4, centerLabelWidth, centerLabelHeight,
                0xFFFFFFFF);
    }

    public static int[] getCenteredStarSlots(int activeModes) {
        int[] starSlots = new int[activeModes];
        int offset = (8 - activeModes) / 2;
        for (int i = 0; i < activeModes; i++) {
            starSlots[i] = i + offset;
        }
        return starSlots;
    }

    void confirmSelection() {
        if (selection != null) {
            RadialMenuNetwork.CHANNEL.sendToServer(new RadialModeChangePacket(enumClass, selection.ordinal()));
        }
    }
}
