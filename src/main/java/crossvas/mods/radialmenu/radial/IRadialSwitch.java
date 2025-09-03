package crossvas.mods.radialmenu.radial;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;

public interface IRadialSwitch {

    ItemStack getStack();
    String getTagKey();
    boolean getCurrentMode(ItemStack stack);
    void setMode(PlayerEntity player, ItemStack stack, boolean mode);
    ITextComponent getModeMessage();
}
