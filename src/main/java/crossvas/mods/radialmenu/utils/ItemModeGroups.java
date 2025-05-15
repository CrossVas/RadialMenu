package crossvas.mods.radialmenu.utils;

import crossvas.mods.radialmenu.RadialMenu;
import crossvas.mods.radialmenu.radial.IRadialEnum;
import crossvas.mods.radialmenu.radial.IRadialModeGroup;
import ic2.core.IC2;
import ic2.core.block.transport.item.TubeAction;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ItemModeGroups {

    public static class TubeToolGroup implements IRadialModeGroup<IRadialEnum> {

        String ACTION_TAG = "action";

        List<TubeAction> allActions = TubeAction.getAll();
        List<IRadialEnum> wrappedActions = allActions.stream()
                .map(action -> new RadialEnumWrapper<>(
                        action,
                        TubeAction::getName,
                        a -> RadialMenu.id("textures/gui/blank.png"),
                        TubeAction::getDesc)
                ).collect(Collectors.toList());

        Map<String, IRadialEnum> actionLookup = wrappedActions.stream()
                .collect(Collectors.toMap(
                        e -> ((RadialEnumWrapper<TubeAction>) e).getTarget().getId(),
                        Function.identity()
                ));

        @Override
        public Class<IRadialEnum> getModeClass() {
            return IRadialEnum.class;
        }

        @Override
        public List<IRadialEnum> getAllModes() {
            return wrappedActions;
        }

        @Override
        public IRadialEnum getCurrentMode(ItemStack stack) {
            String id = stack.getOrCreateTag().getString(ACTION_TAG);
            return actionLookup.getOrDefault(id, wrappedActions.get(0));
        }

        @Override
        public void setMode(PlayerEntity player, ItemStack stack, IRadialEnum mode) {
            CompoundNBT tag = stack.getOrCreateTag();
            IRadialEnum existing = getCurrentMode(stack);
            if (existing != mode) {
                TubeAction action = ((RadialEnumWrapper<TubeAction>) mode).getTarget();
                tag.putString(ACTION_TAG, action.getId());
                if (IC2.PLATFORM.isSimulating()) {
                    player.displayClientMessage(action.getName(), false);
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
