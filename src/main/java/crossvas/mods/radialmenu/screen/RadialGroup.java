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
import crossvas.mods.radialmenu.utils.internal.RColor;
import ic2.core.IC2;
import net.minecraft.client.MainWindow;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldVertexBufferUploader;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import org.lwjgl.opengl.GL11;

import java.util.List;

import static crossvas.mods.radialmenu.utils.RenderHelper.*;

public class RadialGroup<MODE extends IRadialEnum> {

    private final Class<MODE> enumClass;
    private final List<MODE> types;
    final IRadialModeGroup<MODE> group;
    private final ItemStack stack;
    private MODE selection = null;
    MODE lastSelectedMode = null;

    RadialGroup(ItemStack stack, IRadialModeGroup<MODE> group) {
        this.enumClass = group.getModeClass();
        this.types = group.getAllModes();
        this.group = group;
        this.stack = stack;
    }

    void render(MatrixStack matrixStack, int mouseX, int mouseY) {
        Minecraft mc = Minecraft.getInstance();
        FontRenderer font = mc.font;
        MainWindow window = mc.getWindow();

        float centerX = window.getGuiScaledWidth() / 2F;
        float centerY = window.getGuiScaledHeight() / 2F;
        int activeModes = types.size();

        // --- Render radial ring ---
        matrixStack.pushPose();
        RenderSystem.enableBlend();
        RenderSystem.disableTexture();
        matrixStack.translate(centerX, centerY, 0); // Translate for radial ring only

        BufferBuilder vertexBuffer = Tessellator.getInstance().getBuilder();
        vertexBuffer.begin(GL11.GL_TRIANGLES, DefaultVertexFormats.POSITION_COLOR);

        // base circle
        drawTorus(matrixStack, vertexBuffer, 0, 360, new RColor(0, 0, 0, .5f));
        // center margin
        drawTorus(matrixStack, vertexBuffer, (int) (INNER - 7), (int) (INNER - 4), 0, 360, new RColor(1F, 1F, 1F, .6F));
        // center base
        RenderHelper.drawTorus(matrixStack, vertexBuffer, 0, (int) (INNER - 7), 0, 360, new RColor(0F, 0F, 0F, 0.8F));
        // innerMargin
        RenderHelper.drawTorus(matrixStack, vertexBuffer, (int) (INNER), (int) (INNER + 3), 0, 360, new RColor(0F, 0F, 0F, .6F));
        // outerMargin
        RenderHelper.drawTorus(matrixStack, vertexBuffer, (int) (OUTER - 3), (int) (OUTER), 0, 360, new RColor(1F, 1F, 1F, .3F));

        MODE cur = group.getCurrentMode(stack);
        if (cur != null) {
            int section = group.getAllModes().indexOf(cur);
            float sectionStartAngle = -90F + 360F * (-0.5F + section) / activeModes;
            // current
            RenderHelper.drawGradientTorus(matrixStack, vertexBuffer, INNER, OUTER + 3, sectionStartAngle, 360F / activeModes,
                    new RColor(242, 213, 156, 40), new RColor(255, 255, 255, 40), (int) (OUTER - INNER));

            double xDiff = mouseX - centerX;
            double yDiff = mouseY - centerY;
            double dist = xDiff * xDiff + yDiff * yDiff;

            if (dist >= SELECT_RADIUS * SELECT_RADIUS) {
                float angle = (float) Math.toDegrees(Math.atan2(yDiff, xDiff));
                float selectionAngle = angle + 90F + (360F / (2F * activeModes));
                selectionAngle = (selectionAngle + 360F) % 360F;
                int selectionDrawnPos = (int) (selectionAngle * activeModes / 360F);
                selection = types.get(selectionDrawnPos);
                float startAngle = 360F * (-0.5F / activeModes) + angle;
                float sizeAngle = 360F / activeModes;

                // draw selection line
                RenderHelper.drawTorus(matrixStack, vertexBuffer, INNER - 3F, INNER - 1f, startAngle, sizeAngle, new RColor(1F, 1F, 1F, 1F));
                float hoveredStartAngle = -90F + 360F * (-0.5F + selectionDrawnPos) / activeModes;
                // selection
                RenderHelper.drawGradientTorus(matrixStack, vertexBuffer, INNER, OUTER + 3, hoveredStartAngle, 360F / activeModes,
                        new RColor(242, 213, 156, 150), new RColor(255, 255, 255, 200), (int) (OUTER - INNER));
                // inner margin selected
                RenderHelper.drawTorus(matrixStack, vertexBuffer, (int) (INNER), (int) (INNER + 3), hoveredStartAngle, 360F / activeModes, new RColor(0.949F, 0.835F, 0.612F, 0.6F));
            } else {
                selection = null;
            }
        }

        vertexBuffer.end();
        WorldVertexBufferUploader.end(vertexBuffer);

        // star
        RColor starColor = new RColor(1F, 1F, 1F, .3F);
        if (activeModes >= 5) {
            RenderHelper.drawStar(matrixStack, 0, 0, OUTER + 3, activeModes, starColor);
        }
        matrixStack.popPose();

        // --- Render icons & scrolling text ---
        RenderSystem.enableTexture();
        RenderSystem.color4f(1, 1, 1, 1);

        int position = 0;

        for (MODE type : types) {
            double angle = Math.toRadians(270 + 360 * ((float) position / activeModes));
            float offsetX = (float) Math.cos(angle) * (INNER + OUTER) / 2F;
            float offsetY = (float) Math.sin(angle) * (INNER + OUTER) / 2F;
            float x = centerX + offsetX;
            float y = centerY + offsetY;

            boolean isSelected = selection == type;
            float scale = isSelected ? 1.5F : 1.0F; // 50% larger if selected

            // draw icon
            Minecraft.getInstance().textureManager.bind(type.getIcon());
            matrixStack.pushPose();
            matrixStack.translate(x, y, 0);
            matrixStack.scale(scale, scale, 1.0F);
            matrixStack.translate(-12, -15, 0);
            AbstractGui.blit(matrixStack, 0, 0, 24, 24, 0, 0, 18, 18, 18, 18);
            matrixStack.popPose();

            if (isSelected) {
                // Play the sound only once if the mode is selected and different from the previous mode
                if (lastSelectedMode != selection) {
                    IC2.AUDIO.playSound(Minecraft.getInstance().player, RadialMenu.id("sounds/selection.ogg"));
                    lastSelectedMode = selection; // Update the last selected mode
                }
            }
            position++;
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

    void confirmSelection() {
        if (selection != null) {
            RadialMenuNetwork.CHANNEL.sendToServer(new RadialModeChangePacket(enumClass, types.indexOf(selection)));
        }
    }
}
