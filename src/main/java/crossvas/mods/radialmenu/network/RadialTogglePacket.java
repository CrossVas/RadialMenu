package crossvas.mods.radialmenu.network;

import crossvas.mods.radialmenu.radial.IRadialModeItem;
import crossvas.mods.radialmenu.radial.IRadialSwitch;
import crossvas.mods.radialmenu.screen.RadialToggleComponent;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class RadialTogglePacket {

    private final String tag;

    public RadialTogglePacket(IRadialSwitch radialSwitch) {
        this.tag = radialSwitch.getTagKey();
    }

    public void encode(PacketBuffer buf) {
        buf.writeUtf(tag);
    }

    public static RadialTogglePacket decode(PacketBuffer buf) {
        return new RadialTogglePacket(buf.readUtf());
    }

    public static void handle(RadialTogglePacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayerEntity player = ctx.get().getSender();
            ItemStack held = player.getMainHandItem();
            if (!held.isEmpty() && held.getItem() instanceof IRadialModeItem) {
                IRadialModeItem item = (IRadialModeItem) held.getItem();
                item.getRadialComponents(held).forEach(comp -> {
                    if (comp instanceof RadialToggleComponent) {
                        RadialToggleComponent component = (RadialToggleComponent) comp;
                        IRadialSwitch toggleComponent = component.getRadialSwitch();
                        if (toggleComponent.getTagKey().equals(msg.tag)) {
                            boolean value = held.getOrCreateTag().getBoolean(msg.tag);
                            toggleComponent.setMode(player, held, !value);
                        }
                    }
                });
            }
        });
        ctx.get().setPacketHandled(true);
    }

    private RadialTogglePacket(String tagKey) {
        this.tag = tagKey;
    }
}
