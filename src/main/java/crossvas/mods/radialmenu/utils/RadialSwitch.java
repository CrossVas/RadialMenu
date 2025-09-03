package crossvas.mods.radialmenu.utils;

import crossvas.mods.radialmenu.radial.IRadialSwitch;
import ic2.core.IC2;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

public class RadialSwitch implements IRadialSwitch {

    private final String tagKey;
    private final ITextComponent label;
    private final String translationKey;
    private final ItemStack stack;

    public RadialSwitch(ItemStack stack, String tagKey, ITextComponent label, String translationKey) {
        this.stack = stack;
        this.tagKey = tagKey;
        this.label = label;
        this.translationKey = translationKey;
    }

    public ItemStack getStack() {
        return stack;
    }

    @Override
    public String getTagKey() {
        return this.tagKey;
    }

    @Override
    public boolean getCurrentMode(ItemStack stack) {
        return stack.getOrCreateTag().getBoolean(this.tagKey);
    }

    @Override
    public void setMode(PlayerEntity player, ItemStack stack, boolean mode) {
        CompoundNBT tag = stack.getOrCreateTag();
        tag.putBoolean(this.tagKey, mode);
        if (IC2.PLATFORM.isSimulating()) {
            player.displayClientMessage(new TranslationTextComponent(this.translationKey, mode), false);
        }
    }

    @Override
    public ITextComponent getModeMessage() {
        return this.label;
    }
}
