package crossvas.mods.radialmenu;

import crossvas.mods.radialmenu.network.RadialMenuNetwork;
import ic2.core.block.transport.item.TubeAction;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(RadialMenu.ID)
public class RadialMenu {

    public static final String ID = "radialmenu";

    public RadialMenu() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        modEventBus.register(this);
        TubeAction action = new TubeAction("testing", "This is for testing");
    }

    @SubscribeEvent
    public void onCommonLoad(FMLCommonSetupEvent e) {
        RadialMenuNetwork.register();
    }

    @SubscribeEvent
    public void onClientLoad(FMLClientSetupEvent e) {
        MinecraftForge.EVENT_BUS.register(new RadialMenuClientTickEvent());
    }

    public static ResourceLocation id(String path) {
        return new ResourceLocation(ID, path);
    }
}
