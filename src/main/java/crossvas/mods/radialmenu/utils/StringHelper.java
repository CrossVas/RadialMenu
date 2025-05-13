package crossvas.mods.radialmenu.utils;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import crossvas.mods.radialmenu.utils.internal.ScissorsStack;
import crossvas.mods.radialmenu.utils.internal.ScreenRectangle;
import net.minecraft.client.MainWindow;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;

public class StringHelper {

    private static final ScissorsStack STACK = new ScissorsStack();

    public static void drawScrollingString(MatrixStack stack, FontRenderer font, ITextComponent text, float centerX, float centerY, float width, float height, int color) {
        int textWidth = font.width(text);
        float drawY = centerY - (font.lineHeight / 2F);

        // compute clipping box
        float boxX = centerX - (width / 2F);
        float boxY = centerY - (height / 2F);
        int clipX = MathHelper.floor(boxX);
        int clipY = MathHelper.floor(boxY);
        int clipW = MathHelper.ceil(width);
        int clipH = MathHelper.ceil(height);

        if (textWidth > width) {
            float scrollOffset = getScrollOffset(width, textWidth);
            float alignedX = boxX + 2 - scrollOffset;

            pushScissors(clipX, clipY, clipW, clipH);
            stack.pushPose();
            font.draw(stack, text, alignedX, drawY, color);
            stack.popPose();
            popScissors();
        } else {
            float drawX = centerX - (textWidth / 2F); // centered
            font.draw(stack, text, drawX, drawY, color);
        }
    }

    private static float getScrollOffset(float width, int textWidth) {
        float scrollRange = textWidth - width + 4F;
        float scrollSpeed = 20F; // pixels per second

        long now = Util.getMillis();
        float totalScrollTime = scrollRange / scrollSpeed;
        float cycleTime = totalScrollTime * 2F;

        // Infinite looping bounce
        float timeInCycle = (now / 1000F) % cycleTime;
        boolean reverse = timeInCycle >= totalScrollTime;
        float timeInDirection = reverse ? (timeInCycle - totalScrollTime) : timeInCycle;

        float offset = timeInDirection * scrollSpeed;
        return reverse ? (scrollRange - offset) : offset;
    }

    public static void pushScissors(int x, int y, int width, int height) {
        pushScissors(new ScreenRectangle(x, y, width, height));
    }

    public static void pushScissors(ScreenRectangle rect) {
        STACK.push(rect);
        applyScissors(rect);
    }

    public static void popScissors() {
        applyScissors(STACK.pop());
    }

    private static void applyScissors(ScreenRectangle rect) {
        if (rect == null) {
            RenderSystem.disableScissor();
            return;
        }

        Minecraft mc = Minecraft.getInstance();
        MainWindow window = mc.getWindow();

        double scaleX = (double) window.getWidth() / (double) window.getGuiScaledWidth();
        double scaleY = (double) window.getHeight() / (double) window.getGuiScaledHeight();

        int x = (int) (rect.getX() * scaleX);
        int y = (int) ((mc.getWindow().getGuiScaledHeight() - rect.getY() - rect.getHeight()) * scaleY);
        int w = (int) (rect.getWidth() * scaleX);
        int h = (int) (rect.getHeight() * scaleY);

        RenderSystem.enableScissor(x, y, w, h);
    }
}
