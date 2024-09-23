package me.wyndev.dragonhelper.client.hud.element;

import net.minecraft.client.gui.screen.Screen;

/**
 * A {@link ScreenElement} element that can be resized.
 */
public class ResizableElement extends ScreenElement {

    private final int minSize;
    private final int maxSize;

    public ResizableElement(String identifier, String texture, int defaultX, int defaultY, int defaultWidth, int defaultHeight, int minSize, int maxSize) {
        super(identifier, texture, defaultX, defaultY, defaultWidth, defaultHeight);
        this.minSize = minSize;
        this.maxSize = maxSize;
    }

    public void afterMouseScroll(Screen screen, double mouseX, double mouseY, double verticalAmount) {
        int scaleAmount = (int) verticalAmount;
        if (scaleAmount != 0) {
            int newWidth = width + scaleAmount;

            newWidth = Math.max(minSize, Math.min(maxSize, newWidth));

            float ratio = (float) width / (float) height;
            width = newWidth;
            height = (int) (height / ratio);

            x = (int) mouseX - (width / 2);
            y = (int) mouseY - (height / 2);

            x = Math.max(0, Math.min(screen.width - width, x));
            y = Math.max(0, Math.min(screen.height - height, y));
        }
    }

}
