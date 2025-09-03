package crossvas.mods.radialmenu;

import crossvas.mods.radialmenu.radial.IRadialMenu;
import crossvas.mods.radialmenu.radial.IRadialModeItem;
import crossvas.mods.radialmenu.screen.RadialScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class RadialMenuClientTickEvent {

    public static final Minecraft minecraft = Minecraft.getInstance();

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent e) {
        if (e.phase == TickEvent.Phase.START) {
            tick();
        }
    }

    public void tick() {
        PlayerEntity player = minecraft.player;
        if (player == null) return;
        ItemStack stack = player.getMainHandItem();
        if (!(stack.getItem() instanceof IRadialModeItem)) return;

        IRadialModeItem item = (IRadialModeItem) stack.getItem();
        for (IRadialMenu<?> group : item.getRadialMenus()) {
            if (group.getKeyStatusDown(player)) {
                if (minecraft.screen == null) {
                    minecraft.setScreen(new RadialScreen(stack, item, group.getModeClass()));
                }
                return;
            }
        }
        if (minecraft.screen instanceof RadialScreen) {
            minecraft.setScreen(null);
        }
    }
}
