package crossvas.mods.radialmenu.network;

import crossvas.mods.radialmenu.radial.IRadialModeGroup;
import crossvas.mods.radialmenu.radial.IRadialModeItem;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

//TODO: add support for EquipmentSlotType instead of hardcodded main hand
public class RadialModeChangePacket {

    private final String className;
    private final int ordinal;
//    private final EquipmentSlotType slotType;

    public <T extends Enum<T>> RadialModeChangePacket(Class<T> clazz, int ordinal) {
        this.className = clazz.getName();
        this.ordinal = ordinal;
//        this.slotType = EquipmentSlotType.MAINHAND;
    }

    public static RadialModeChangePacket decode(PacketBuffer buf) {
//        EquipmentSlotType slotType = buf.readEnum(EquipmentSlotType.class);
        return new RadialModeChangePacket(buf.readUtf(), buf.readInt());
    }

    public void encode(PacketBuffer buf) {
        buf.writeUtf(className);
        buf.writeInt(ordinal);
//        buf.writeEnum(this.slotType);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public void handle(Supplier<NetworkEvent.Context> context) {
        context.get().enqueueWork(() -> {
            PlayerEntity player = context.get().getSender();
            if (player != null) {
                ItemStack stack = player.getMainHandItem();
                if (!stack.isEmpty() && stack.getItem() instanceof IRadialModeItem) {
                    IRadialModeItem item = (IRadialModeItem) stack.getItem();
                    item.getRadialGroups().forEach(group -> {
                        if (group.getModeClass().getName().equals(className)) {
                            Enum<?>[] values = group.getModeClass().getEnumConstants();
                            ((IRadialModeGroup) group).setMode(player, stack, values[ordinal]);
                        }
                    });
                }
            }
        });
    }

    private RadialModeChangePacket(String className, int ordinal) {
        this.className = className;
        this.ordinal = ordinal;
    }
}
