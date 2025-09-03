package crossvas.mods.radialmenu.utils;

import crossvas.mods.radialmenu.RadialMenu;
import crossvas.mods.radialmenu.radial.IRadialEnum;
import crossvas.mods.radialmenu.radial.IRadialMenu;
import ic2.core.IC2;
import ic2.core.block.transport.item.TubeAction;
import ic2.core.utils.helpers.SanityHelper;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class RadialComponents {

    public static class TubeToolMenu implements IRadialMenu<IRadialEnum> {

        String ACTION_TAG = "action";

        List<TubeAction> allActions = TubeAction.getAll();
        List<IRadialEnum> wrappedActions = allActions.stream()
                .map(action -> new RadialEnumWrapper<>(
                        action,
                        a -> new StringTextComponent(SanityHelper.firstLetterUppercase(SanityHelper.toPascalCase(action.getId().toLowerCase(Locale.ROOT)))),
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

    public static class PainterMenu implements IRadialMenu<PainterMenu.PainterEnum> {

        public static final String RANGE_TAG = "range";

        @Override
        public Class<PainterEnum> getModeClass() {
            return PainterEnum.class;
        }

        @Override
        public List<PainterEnum> getAllModes() {
            return Arrays.asList(PainterEnum.VALUES);
        }

        @Override
        public PainterEnum getCurrentMode(ItemStack stack) {
            CompoundNBT tag = stack.getOrCreateTag();
            return PainterEnum.getFromId(tag.getByte(RANGE_TAG));
        }

        @Override
        public void setMode(PlayerEntity player, ItemStack stack, PainterEnum mode) {
            CompoundNBT tag = stack.getOrCreateTag();
            IRadialEnum existing = getCurrentMode(stack);
            if (existing != mode) {
                tag.putByte(RANGE_TAG, (byte) mode.ordinal());
                if (IC2.PLATFORM.isSimulating()) {
                    player.displayClientMessage(mode.getDescription(), false);
                }
            }
        }

        @Override
        public boolean getKeyStatusDown(PlayerEntity player) {
            return IC2.KEYBOARD.isModeSwitchKeyDown(player);
        }

        @Override
        public ITextComponent getModeMessage() {
            return new StringTextComponent("Painter Radius");
        }

        public enum PainterEnum implements IRadialEnum {
            RADIUS_1(1),
            RADIUS_3(3),
            RADIUS_5(5);

            int radius;
            public static final PainterEnum[] VALUES = values();

            PainterEnum(int radius) {
                this.radius = radius;
            }

            @Override
            public ITextComponent getTextForDisplay() {
                return new StringTextComponent("Radius: " + radius);
            }

            @Override
            public ITextComponent getDescription() {
                return new TranslationTextComponent("Painter Radius: %s Block(s)", radius);
            }

            public static PainterEnum getFromId(int id) {
                return VALUES[id % VALUES.length];
            }
        }
    }
}
