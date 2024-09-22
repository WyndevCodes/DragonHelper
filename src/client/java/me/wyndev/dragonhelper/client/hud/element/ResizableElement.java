package me.wyndev.dragonhelper.client.hud.element;

import me.wyndev.dragonhelper.client.data.ModData;
import net.minecraft.client.gui.screen.Screen;

/**
 * A {@link ScreenElement} element that can be resized.
 */
public class ResizableElement extends ScreenElement {

    private int scale; //scale (in pixels)
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

    @Override
    public void loadData(int defaultX, int defaultY) {
        this.x = ModData.getInstance().getInt(identifier + "-x", defaultX);
        this.y = ModData.getInstance().getInt(identifier + "-y", defaultY);
        this.width = ModData.getInstance().getInt(identifier + "-width", super.width);
        this.height = ModData.getInstance().getInt(identifier + "-height", super.height);
        this.scale = ModData.getInstance().getInt(identifier + "-scale", scale);
    }

    @Override
    public void saveData() {
        ModData.getInstance().setInt(identifier + "-x", x);
        ModData.getInstance().setInt(identifier + "-y", y);
        ModData.getInstance().setInt(identifier + "-width", width);
        ModData.getInstance().setInt(identifier + "-height", height);
        ModData.getInstance().setInt(identifier + "-scale", scale);
    }

}
