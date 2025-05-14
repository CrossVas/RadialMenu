package crossvas.mods.radialmenu.utils;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldVertexBufferUploader;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Vector2f;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class RenderHelper {

    public static final float DRAWS = 300;
    public static final float INNER = 60, OUTER = 120;
    public static final float SELECT_RADIUS = 60;

    public static void drawTorus(MatrixStack matrix, float startAngle, float sizeAngle, float gl_r, float gl_g, float gl_b, float gl_a) {
        RenderSystem.color4f(gl_r, gl_g, gl_b, gl_a);
        drawTorus(matrix, startAngle, sizeAngle);
    }

    public static void drawTorus(MatrixStack matrix, float startAngle, float sizeAngle) {
        drawTorus(matrix, (int) INNER, (int) OUTER, startAngle, sizeAngle);
    }

    public static void drawTorus(MatrixStack matrix, float innerCircle, float outerCircle, float startAngle, float sizeAngle, float gl_r, float gl_g, float gl_b, float gl_a) {
        RenderSystem.color4f(gl_r, gl_g, gl_b, gl_a);
        drawTorus(matrix, innerCircle, outerCircle, startAngle, sizeAngle);
    }

    public static void drawTorus(MatrixStack matrix, float innerCircle, float outerCircle, float startAngle, float sizeAngle) {
        BufferBuilder vertexBuffer = Tessellator.getInstance().getBuilder();
        Matrix4f matrix4f = matrix.last().pose();
        vertexBuffer.begin(GL11.GL_QUAD_STRIP, DefaultVertexFormats.POSITION);
        float draws = DRAWS * (sizeAngle / 360F);
        for (int i = 0; i <= draws; i++) {
            float angle = (float) Math.toRadians(startAngle + (i / DRAWS) * 360);
            vertexBuffer.vertex(matrix4f, (float) (outerCircle * Math.cos(angle)), (float) (outerCircle * Math.sin(angle)), 0).endVertex();
            vertexBuffer.vertex(matrix4f, (float) (innerCircle * Math.cos(angle)), (float) (innerCircle * Math.sin(angle)), 0).endVertex();
        }
        vertexBuffer.end();
        WorldVertexBufferUploader.end(vertexBuffer);
    }

    public static void drawGradientTorus(MatrixStack matrix, float innerCircle, float outerCircle, float startAngle, float sizeAngle, Color fromColor, Color toColor, int steps) {
        for (int i = 0; i < steps; i++) {
            float t = i / (float)(steps - 1);

            float currentInner = innerCircle + (outerCircle - innerCircle) * (i / (float) steps);
            float currentOuter = currentInner + ((outerCircle - innerCircle) / steps);

            // interpolation
            float r = lerp(fromColor.getRed(),   toColor.getRed(),   t) / 255f;
            float g = lerp(fromColor.getGreen(), toColor.getGreen(), t) / 255f;
            float b = lerp(fromColor.getBlue(),  toColor.getBlue(),  t) / 255f;
            float a = lerp(fromColor.getAlpha(), toColor.getAlpha(), t) / 255f;

            RenderSystem.color4f(r, g, b, a);
            drawTorus(matrix, currentInner, currentOuter, startAngle, sizeAngle);
        }

        // reset to white
        RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
    }

    private static float lerp(float a, float b, float t) {
        return a + (b - a) * t;
    }

    public static void drawStar(MatrixStack matrixStack, float centerX, float centerY, float radius, int points) {
        Matrix4f pose = matrixStack.last().pose();
        BufferBuilder buffer = Tessellator.getInstance().getBuilder();
        buffer.begin(GL11.GL_LINES, DefaultVertexFormats.POSITION);

        List<Vector2f> mainPoints = new ArrayList<>();
        List<Vector2f> sidePoints = new ArrayList<>();

        float innerRadius = radius * 0.46F;

        for (int i = 0; i < points; i++) {
            double angle = Math.toRadians((360.0 / points) * i);
            float mainX = centerX + (float) Math.cos(angle) * radius;
            float mainY = centerY + (float) Math.sin(angle) * radius;
            mainPoints.add(new Vector2f(mainX, mainY));

            double sideAngle = Math.toRadians((360.0 / points) * i + (180.0 / points));
            float sideX = centerX + (float) Math.cos(sideAngle) * innerRadius;
            float sideY = centerY + (float) Math.sin(sideAngle) * innerRadius;
            sidePoints.add(new Vector2f(sideX, sideY));
        }

        for (int i = 0; i < points; i++) {
            Vector2f prev = mainPoints.get(i);
            Vector2f next = mainPoints.get((i + 1) % points);
            Vector2f mid = sidePoints.get(i);

            buffer.vertex(pose, mid.x, mid.y, 0).endVertex();
            buffer.vertex(pose, prev.x, prev.y, 0).endVertex();

            buffer.vertex(pose, mid.x, mid.y, 0).endVertex();
            buffer.vertex(pose, next.x, next.y, 0).endVertex();
        }

        buffer.end();
        WorldVertexBufferUploader.end(buffer);
    }
}
