package crossvas.mods.radialmenu.utils;

import crossvas.mods.radialmenu.radial.IRadialModeGroup;
import ic2.core.IC2;
import ic2.core.block.transport.item.TubeAction;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

public class ItemModeGroups {

    public static class TubeToolConfigGroup implements IRadialModeGroup<TubeToolMode> {

        String ACTION_TAG = "action";

        @Override
        public Class<TubeToolMode> getModeClass() {
            return TubeToolMode.class;
        }

        @Override
        public TubeToolMode getCurrentMode(ItemStack stack) {
            return TubeToolMode.getByIndex(stack.getOrCreateTag().getInt(ACTION_TAG));
        }

        @Override
        public void setMode(PlayerEntity player, ItemStack stack, TubeToolMode mode) {
            CompoundNBT tag = stack.getOrCreateTag();
            TubeToolMode existing = getCurrentMode(stack);
            if (existing != mode) {
                tag.putInt(ACTION_TAG, mode.ordinal());
                if (IC2.PLATFORM.isSimulating()) {
                    player.displayClientMessage(mode.action.getName(), false);
                }
            }
        }

        @Override
        public boolean getKeyStatusDown(PlayerEntity player) {
            return IC2.KEYBOARD.isModeSwitchKeyDown(player);
        }

        @Override
        public ITextComponent getModeMessage() {
            return new TranslationTextComponent("tooltip.item.ic2.tube_tool.switch_mode");
        }
    }
}
