package crossvas.mods.radialmenu.screen;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import crossvas.mods.radialmenu.RadialMenu;
import crossvas.mods.radialmenu.network.RadialMenuNetwork;
import crossvas.mods.radialmenu.network.RadialModeChangePacket;
import crossvas.mods.radialmenu.radial.IRadial;
import crossvas.mods.radialmenu.radial.IRadialEnum;
import crossvas.mods.radialmenu.radial.IRadialMenu;
import crossvas.mods.radialmenu.utils.RenderHelper;
import crossvas.mods.radialmenu.utils.StringHelper;
import crossvas.mods.radialmenu.utils.internal.RColor;
import ic2.core.IC2;
import net.minecraft.client.MainWindow;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;

import java.util.List;

import static crossvas.mods.radialmenu.utils.RenderHelper.*;

public class RadialModeComponent<MODE extends IRadialEnum> implements IRadial {

    private final Class<MODE> enumClass;
    private final List<MODE> types;
    final IRadialMenu<MODE> group;
    private final ItemStack stack;
    private MODE selection = null;
    MODE lastSelectedMode = null;

    public RadialModeComponent(ItemStack stack, IRadialMenu<MODE> group) {
        this.enumClass = group.getModeClass();
        this.types = group.getAllModes();
        this.group = group;
        this.stack = stack;
    }

    @Override
    public void renderBackground(MatrixStack stack, BufferBuilder buf, int mouseX, int mouseY, float centerX, float centerY, int index) {
        int activeModes = types.size();

        stack.pushPose();
        RenderSystem.enableBlend();
        RenderSystem.disableTexture();
        stack.translate(centerX, centerY, 0);

        RenderHelper.drawTorus(stack, buf, 0, 360, new RColor(0, 0, 0, .5f));
        RenderHelper.drawTorus(stack, buf, (int) (INNER - 7), (int) (INNER - 4), 0, 360, new RColor(1F, 1F, 1F, .6F));
        RenderHelper.drawTorus(stack, buf, 0, (int) (INNER - 7), 0, 360, new RColor(0F, 0F, 0F, 0.8F));
        RenderHelper.drawTorus(stack, buf, (int) (INNER), (int) (INNER + 3), 0, 360, new RColor(0F, 0F, 0F, .6F));
        RenderHelper.drawTorus(stack, buf, (int) (OUTER - 3), (int) (OUTER), 0, 360, new RColor(1F, 1F, 1F, .3F));

        MODE cur = group.getCurrentMode(this.stack);
        if (cur != null) {
            int section = group.getAllModes().indexOf(cur);
            float sectionStartAngle = -90F + 360F * (-0.5F + section) / activeModes;
            RenderHelper.drawGradientTorus(stack, buf, INNER, OUTER + 3, sectionStartAngle, 360F / activeModes,
                    new RColor(242, 213, 156, 40), new RColor(255, 255, 255, 40), (int) (OUTER - INNER));

            double xDiff = mouseX - centerX;
            double yDiff = mouseY - centerY;
            double dist = xDiff * xDiff + yDiff * yDiff;

            if (dist >= SELECT_RADIUS * SELECT_RADIUS && dist <= OUTER * OUTER) {
                float angle = (float) Math.toDegrees(Math.atan2(yDiff, xDiff));
                float selectionAngle = angle + 90F + (360F / (2F * activeModes));
                selectionAngle = (selectionAngle + 360F) % 360F;
                int selectionDrawnPos = (int) (selectionAngle * activeModes / 360F);
                selection = types.get(selectionDrawnPos);
                float startAngle = 360F * (-0.5F / activeModes) + angle;
                float sizeAngle = 360F / activeModes;

                RenderHelper.drawTorus(stack, buf, INNER - 3F, INNER - 1f, startAngle, sizeAngle, new RColor(1F, 1F, 1F, 1F));
                float hoveredStartAngle = -90F + 360F * (-0.5F + selectionDrawnPos) / activeModes;
                RenderHelper.drawGradientTorus(stack, buf, INNER, OUTER + 3, hoveredStartAngle, 360F / activeModes,
                        new RColor(242, 213, 156, 150), new RColor(255, 255, 255, 200), (int) (OUTER - INNER));
                RenderHelper.drawTorus(stack, buf, (int) (INNER), (int) (INNER + 3), hoveredStartAngle, 360F / activeModes,
                        new RColor(0.949F, 0.835F, 0.612F, 0.6F));
            } else {
                selection = null;
            }
        }
        stack.popPose();
    }

    @Override
    public void renderForeground(MatrixStack stack) {
        Minecraft mc = Minecraft.getInstance();
        MainWindow window = Minecraft.getInstance().getWindow();
        float centerX = window.getGuiScaledWidth() / 2F;
        float centerY = window.getGuiScaledHeight() / 2F;
        FontRenderer font = mc.font;
        RenderSystem.enableTexture();
        RenderSystem.color4f(1, 1, 1, 1);

        int activeModes = types.size();
        int position = 0;

        if (activeModes >= 4) {
            RenderSystem.disableTexture();
            RenderSystem.enableBlend();
            RenderHelper.drawStar(stack, centerX, centerY, OUTER + 3, activeModes, new RColor(1F, 1F, 1F, .3F));
            RenderSystem.enableTexture();
            RenderSystem.disableBlend();
        }

        for (MODE type : types) {
            double angle = Math.toRadians(270 + 360 * ((float) position / activeModes));
            float offsetX = (float) Math.cos(angle) * (INNER + OUTER) / 2F;
            float offsetY = (float) Math.sin(angle) * (INNER + OUTER) / 2F;
            float x = centerX + offsetX;
            float y = centerY + offsetY;

            boolean isSelected = selection == type;
            float scale = isSelected ? 1.5F : 1.0F;

            mc.textureManager.bind(type.getIcon());
            stack.pushPose();
            stack.translate(x, y, 0);
            stack.scale(scale, scale, 1.0F);
            stack.translate(-12, -15, 0);
            AbstractGui.blit(stack, 0, 0, 24, 24, 0, 0, 18, 18, 18, 18);
            stack.popPose();

            if (isSelected && lastSelectedMode != selection) {
                IC2.AUDIO.playSound(mc.player, RadialMenu.id("sounds/selection.ogg"));
                lastSelectedMode = selection;
            }

            position++;
        }

        float centerLabelWidth = 108;
        float centerLabelHeight = 10;

        if (selection != null) {
            ITextComponent label = selection.getTextForDisplay();
            StringHelper.drawScrollingString(stack, font, label,
                    centerX, centerY - font.lineHeight - 4, centerLabelWidth, centerLabelHeight, 0xFFFFFFFF);
            StringHelper.drawScrollingString(stack, font, selection.getDescription().copy().withStyle(TextFormatting.GRAY),
                    centerX, centerY, centerLabelWidth, centerLabelHeight, 0xFFFFFFFF);
        }

        StringHelper.drawScrollingString(stack, font, group.getModeMessage(),
                centerX, centerY + font.lineHeight + 4, centerLabelWidth, centerLabelHeight, 0xFFFFFFFF);
    }

    @Override
    public void confirmSelection() {
        if (selection != null) {
            RadialMenuNetwork.CHANNEL.sendToServer(new RadialModeChangePacket(enumClass, types.indexOf(selection)));
        }
    }
}
