package crossvas.mods.radialmenu.screen;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import crossvas.mods.radialmenu.RadialMenu;
import crossvas.mods.radialmenu.network.RadialMenuNetwork;
import crossvas.mods.radialmenu.network.RadialTogglePacket;
import crossvas.mods.radialmenu.radial.IRadial;
import crossvas.mods.radialmenu.radial.IRadialSwitch;
import crossvas.mods.radialmenu.utils.RadialSwitch;
import crossvas.mods.radialmenu.utils.RenderHelper;
import crossvas.mods.radialmenu.utils.internal.RColor;
import ic2.core.IC2;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RadialToggleComponent implements IRadial {

    final IRadialSwitch radialSwitch;
    private final ItemStack stack;
    private final String tagKey;
    private final ITextComponent label;

    private float screenX, screenY;
    private static final int TOGGLE_RADIUS = 20;

    private static final List<RadialToggleComponent> registry = new ArrayList<>();

    public RadialToggleComponent(IRadialSwitch radialSwitch) {
        this.stack = radialSwitch.getStack();
        this.tagKey = radialSwitch.getTagKey();
        this.label = radialSwitch.getModeMessage();
        this.radialSwitch = radialSwitch;
    }

    public IRadialSwitch getRadialSwitch() {
        return radialSwitch;
    }

    private boolean getValue() {
        return stack.getOrCreateTag().getBoolean(tagKey);
    }

    private void setValue(boolean value) {
        stack.getOrCreateTag().putBoolean(tagKey, value);
    }

    @Override
    public void renderBackground(MatrixStack stack, BufferBuilder buf, int mouseX, int mouseY, float centerX, float centerY, int index) {
        double angleRad = Math.toRadians(285 + 360.0 * index / 12);
        float radius = 150;
        float x = (float) Math.cos(angleRad) * radius;
        float y = (float) Math.sin(angleRad) * radius;

        this.screenX = centerX + x;
        this.screenY = centerY + y;

        stack.pushPose();
        stack.translate(screenX, screenY, 0);

        int smallInner = 0;
        int smallOuter = TOGGLE_RADIUS;

        RColor color = getValue() ? new RColor(0.4f, 0.9f, 0.4f, 1f) : new RColor(0.9f, 0.4f, 0.4f, 1f);


        RenderHelper.drawTorus(stack, buf, 10, smallOuter, 0, 360, new RColor(0, 0, 0, .5f));
        RenderHelper.drawTorus(stack, buf, 7, 10, 0, 360, new RColor(0, 0, 0, 1f));
        RenderHelper.drawTorus(stack, buf, smallInner, 8, 0, 360, color); // toggle button
        RenderHelper.drawTorus(stack, buf, smallOuter - 2, smallOuter, 0, 360, new RColor(1F, 1F, 1F, .3F));
        stack.popPose();
    }

    @Override
    public void renderForeground(MatrixStack stack) {
        Minecraft mc = Minecraft.getInstance();
        FontRenderer font = mc.font;

        // Text positioning
        float textX = screenX + TOGGLE_RADIUS + 4;
        float textY = screenY - font.lineHeight / 2f + 1;

        // Measure text
        int textWidth = font.width(label);
        int textHeight = font.lineHeight;
        float padding = 2f;

        // Draw background rectangle
        RenderSystem.disableTexture();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();

        AbstractGui.fill(stack,
                (int) (textX - padding),
                (int) (textY - padding),
                (int) (textX + textWidth + padding),
                (int) (textY + textHeight + padding),
                0xAA000000
        );
        RenderSystem.enableTexture();
        font.draw(stack, label, textX, textY, 0xFFFFFF);
    }


    public boolean isHovered(double mouseX, double mouseY) {
        double dx = mouseX - screenX;
        double dy = mouseY - screenY;
        return dx * dx + dy * dy <= TOGGLE_RADIUS * TOGGLE_RADIUS;
    }

    @Override
    public void confirmSelection() {
        boolean newValue = !getValue();
        setValue(newValue);
        IC2.AUDIO.playSound(Minecraft.getInstance().player, RadialMenu.id("sounds/selection.ogg"));
        RadialMenuNetwork.CHANNEL.sendToServer(new RadialTogglePacket(this.radialSwitch));
    }

    public static class Builder {
        private final ItemStack stack;
        private final List<RadialToggleComponent> toggles = new ArrayList<>();

        public Builder(ItemStack stack) {
            this.stack = stack;
            RadialToggleComponent.registry.clear();
        }

        public Builder of(String tagKey, ITextComponent label, String translationKey) {
            IRadialSwitch toggle = new RadialSwitch(stack, tagKey, label, translationKey);
            RadialToggleComponent comp = new RadialToggleComponent(toggle);
            toggles.add(comp);
            return this;
        }

        public List<IRadial> build() {
            return Collections.unmodifiableList(toggles);
        }
    }
}

