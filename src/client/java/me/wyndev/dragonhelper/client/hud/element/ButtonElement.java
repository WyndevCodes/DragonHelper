package me.wyndev.dragonhelper.client.hud.element;

import me.wyndev.dragonhelper.client.hud.DragonHelperScreen;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;

/**
 * A {@link ScreenElement} element that has a set width and height,
 * can be clicked, and contains text within itself.
 */
public class ButtonElement extends ScreenElement implements ClickableElement {

    private final ClickConsumer clickFunction;
    private final Text buttonText;

    public ButtonElement(String identifier, ClickConsumer onClick, Text buttonText,
                         int defaultX, int defaultY, int defaultWidth, int defaultHeight) {
        super(identifier, null, defaultX, defaultY, defaultWidth, defaultHeight);
        clickFunction = onClick;
        this.buttonText = buttonText;
    }

    @Override
    protected void draw(DrawContext drawContext, double mouseX, double mouseY) {
        int textX = x + (width / 2) - (MinecraftClient.getInstance().textRenderer.getWidth(buttonText) / 2);
        int textY = y + (height / 2) - (MinecraftClient.getInstance().textRenderer.fontHeight / 2);

        boolean isMouseOver = isMouseOver(mouseX, mouseY);

        //button background
        drawContext.fill(x, y, x + width, y + height, isMouseOver ? 0xDD000000 : 0x66000000);
        //button outline
        drawContext.drawBorder(x, y, width, height, isMouseOver ? 0xFFFFFF : 0x999999);
        //text in center
        drawContext.drawText(MinecraftClient.getInstance().textRenderer, buttonText, textX, textY, 0xFFFFFF, false);
    }

    @Override
    public void onClick(DragonHelperScreen screen, ScreenElementGroup parentGroup) {
        clickFunction.accept(screen, parentGroup);
    }

    @Override
    public boolean canDrag() {
        return false;
    }

    @Override
    public boolean shouldRenderInHUD() {
        return false;
    }

}
