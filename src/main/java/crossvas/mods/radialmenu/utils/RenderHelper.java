package crossvas.mods.radialmenu.utils;

import com.mojang.blaze3d.matrix.MatrixStack;
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

    public static void drawTorus(MatrixStack matrix, float startAngle, float sizeAngle) {
        drawTorus(matrix, (int) INNER, (int) OUTER, startAngle, sizeAngle);
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
