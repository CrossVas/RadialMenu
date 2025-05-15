package crossvas.mods.radialmenu.utils;

import com.mojang.blaze3d.matrix.MatrixStack;
import crossvas.mods.radialmenu.utils.internal.RColor;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldVertexBufferUploader;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Vector2f;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.List;

public class RenderHelper {

    public static final float DRAWS = 300;
    public static final float INNER = 60, OUTER = 120;
    public static final float SELECT_RADIUS = 60;

    public static void drawTorus(MatrixStack matrix, BufferBuilder vertexBuffer, float startAngle, float sizeAngle, RColor color) {
        drawTorus(matrix, vertexBuffer, (int) INNER, (int) OUTER, startAngle, sizeAngle, color);
    }

    public static void drawTorus(MatrixStack matrix, BufferBuilder vertexBuffer, float innerRadius, float outerRadius, float startAngle, float sizeAngle, RColor color) {
        float draws = DRAWS * (sizeAngle / 360F);
        int r = color.getRed(), g = color.getGreen(), b = color.getBlue(), a = color.getAlpha();
        Matrix4f matrix4f = matrix.last().pose();
        for (int i = 0; i < draws; i++) {
            float angle0 = (float) Math.toRadians(startAngle + (i / DRAWS) * 360);
            float angle1 = (float) Math.toRadians(startAngle + ((i + 1) / DRAWS) * 360);

            float inner0x = (float) (innerRadius * Math.cos(angle0));
            float inner0y = (float) (innerRadius * Math.sin(angle0));
            float inner1x = (float) (innerRadius * Math.cos(angle1));
            float inner1y = (float) (innerRadius * Math.sin(angle1));
            float outer0x = (float) (outerRadius * Math.cos(angle0));
            float outer0y = (float) (outerRadius * Math.sin(angle0));
            float outer1x = (float) (outerRadius * Math.cos(angle1));
            float outer1y = (float) (outerRadius * Math.sin(angle1));

            vertexBuffer.vertex(matrix4f, outer0x, outer0y, 0).color(r, g, b, a).endVertex();
            vertexBuffer.vertex(matrix4f, inner0x, inner0y, 0).color(r, g, b, a).endVertex();
            vertexBuffer.vertex(matrix4f, inner1x, inner1y, 0).color(r, g, b, a).endVertex();

            vertexBuffer.vertex(matrix4f, outer0x, outer0y, 0).color(r, g, b, a).endVertex();
            vertexBuffer.vertex(matrix4f, inner1x, inner1y, 0).color(r, g, b, a).endVertex();
            vertexBuffer.vertex(matrix4f, outer1x, outer1y, 0).color(r, g, b, a).endVertex();
        }
    }

    public static void drawGradientTorus(MatrixStack matrix, BufferBuilder buffer, float innerCircle, float outerCircle, float startAngle, float sizeAngle, RColor fromColor, RColor toColor, int steps) {
        for (int i = 0; i < steps; i++) {
            float t = i / (float)(steps - 1);

            float currentInner = innerCircle + (outerCircle - innerCircle) * (i / (float) steps);
            float currentOuter = currentInner + ((outerCircle - innerCircle) / steps);

            int r = (int) lerp(fromColor.getRed(), toColor.getRed(), t);
            int g = (int) lerp(fromColor.getGreen(), toColor.getGreen(), t);
            int b = (int) lerp(fromColor.getBlue(), toColor.getBlue(), t);
            int a = (int) lerp(fromColor.getAlpha(), toColor.getAlpha(), t);

            drawTorus(matrix, buffer, currentInner, currentOuter, startAngle, sizeAngle, new RColor(r, g, b, a));
        }
    }

    private static float lerp(float a, float b, float t) {
        return a + (b - a) * t;
    }

    public static void drawStar(MatrixStack matrixStack, float centerX, float centerY, float radius, int points, RColor color) {
        Matrix4f pose = matrixStack.last().pose();
        BufferBuilder buffer = Tessellator.getInstance().getBuilder();
        GL11.glLineWidth(3.0f);
        buffer.begin(GL11.GL_LINES, DefaultVertexFormats.POSITION_COLOR);

        List<Vector2f> mainPoints = new ArrayList<>();
        List<Vector2f> sidePoints = new ArrayList<>();

        float innerRadius = INNER + 15;

        for (int i = 0; i < points; i++) {
            double angle = Math.toRadians((360.0 / points) * i - 90);
            float mainX = centerX + (float) Math.cos(angle) * radius;
            float mainY = centerY + (float) Math.sin(angle) * radius;
            mainPoints.add(new Vector2f(mainX, mainY));

            double sideAngle = Math.toRadians((360.0 / points) * i + (180.0 / points) - 90);
            float sideX = centerX + (float) Math.cos(sideAngle) * innerRadius;
            float sideY = centerY + (float) Math.sin(sideAngle) * innerRadius;
            sidePoints.add(new Vector2f(sideX, sideY));
        }

        for (int i = 0; i < points; i++) {
            Vector2f prev = mainPoints.get(i);
            Vector2f next = mainPoints.get((i + 1) % points);
            Vector2f mid = sidePoints.get(i);

            buffer.vertex(pose, mid.x, mid.y, 0).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
            buffer.vertex(pose, prev.x, prev.y, 0).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();

            buffer.vertex(pose, mid.x, mid.y, 0).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
            buffer.vertex(pose, next.x, next.y, 0).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
        }

        buffer.end();
        WorldVertexBufferUploader.end(buffer);
        GL11.glLineWidth(1.0f);
    }
}
