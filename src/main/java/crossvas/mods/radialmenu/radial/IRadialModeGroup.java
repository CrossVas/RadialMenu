package crossvas.mods.radialmenu.radial;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;

public interface IRadialModeGroup<TYPE extends Enum<TYPE> & IRadialEnum> {
    Class<TYPE> getModeClass();
    TYPE getCurrentMode(ItemStack stack);
    void setMode(PlayerEntity player, ItemStack stack, TYPE mode);
    boolean getKeyStatusDown(PlayerEntity player);
    ITextComponent getModeMessage();
}
