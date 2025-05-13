package crossvas.mods.radialmenu.utils.internal;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.ArrayDeque;
import java.util.Deque;

/**
 * A utility class for managing a stack of screen rectangles for scissoring.
 */
@OnlyIn(Dist.CLIENT)
public class ScissorsStack {
    Deque<ScreenRectangle> stack = new ArrayDeque<>();

    /**
     * Pushes a screen rectangle onto the scissor stack.
     * <p>
     * @param scissor the screen rectangle to push.
     */
    public void push(ScreenRectangle scissor) {
        if (stack.isEmpty()) {
            stack.push(scissor);
            return;
        }
        scissor.limit(stack.peek());
    }

    public ScreenRectangle pop() {
        stack.pop();
        return stack.peek();
    }
}
