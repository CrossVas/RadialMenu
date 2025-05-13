package crossvas.mods.radialmenu.network;

import crossvas.mods.radialmenu.RadialMenu;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;

import java.util.Optional;

public class RadialMenuNetwork {
    public static final String PROTOCOL_VERSION = "1";
    public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(RadialMenu.ID, "main"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals
    );

    private static int packetId = 0;

    public static void register() {
        CHANNEL.registerMessage(packetId++,
                RadialModeChangePacket.class,
                RadialModeChangePacket::encode,
                RadialModeChangePacket::decode,
                RadialModeChangePacket::handle,
                Optional.of(NetworkDirection.PLAY_TO_SERVER));
    }
}
