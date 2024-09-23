package me.wyndev.dragonhelper.client.hud.element;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;

/**
 * A {@link ResizableElement} that is a text.
 */
public class TextElement extends ResizableElement {

    private final ElementCallback<String> textFunction;
    private final ElementCallback<Integer> colorFunction;
    private final boolean hasBackground;

    public TextElement(String identifier, ElementCallback<String> text, ElementCallback<Integer> textColor, int defaultX, int defaultY, int minSize, int maxSize,
                       boolean hasBackground) {
        super(identifier, null, defaultX, defaultY, 100, 30, minSize, maxSize);
        this.textFunction = text;
        this.colorFunction = textColor;
        this.hasBackground = hasBackground;
        this.height = -1;
    }

    @Override
    protected void draw(DrawContext drawContext, double mouseX, double mouseY) {
        String text = textFunction.call();

        resizeBounds(text);

        if (hasBackground) drawContext.fill(x - 5, y - 4, x + width + 5, y + height + 4, 0x66000000);
        drawContext.drawText(MinecraftClient.getInstance().textRenderer, text, x, y, colorFunction.call(), false);
    }

    @Override
    public void reset() {
        this.x = defaultX;
        this.y = defaultY;
        this.width = defaultWidth;
        this.height = -1;
        saveData();
        resizeBounds(textFunction.call());
    }

    private void resizeBounds(String text) {
        this.width = MinecraftClient.getInstance().textRenderer.getWidth(text);
        if (this.height == -1) this.height = MinecraftClient.getInstance().textRenderer.fontHeight;
    }

    public boolean hasBackground() {
        return hasBackground;
    }
}
