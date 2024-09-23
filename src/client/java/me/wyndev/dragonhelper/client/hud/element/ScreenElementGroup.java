package me.wyndev.dragonhelper.client.hud.element;

import me.wyndev.dragonhelper.client.hud.DragonHelperScreen;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * A group of {@link ScreenElement}s that should share things like scale, collision, etc.
 */
public class ScreenElementGroup {

    private final ScreenElement.ElementCallback<Boolean> renderCheckCallback;
    private final ScreenElement parent;
    private final ScreenElement[] children;

    private boolean hasResized;

    /**
     * @param renderCheckCallback The callback to run to determine if this group should render
     */
    public ScreenElementGroup(ScreenElement.ElementCallback<Boolean> renderCheckCallback, ScreenElement parent, ScreenElement... children) {
        this.renderCheckCallback = renderCheckCallback;
        this.parent = parent;
        this.children = children;

        this.hasResized = false;
    }

    public void drawAll(DrawContext drawContext, double mouseX, double mouseY, boolean isConfig) {
        if (!renderCheckCallback.call()) return;

        resizeElements();

        if (parent.shouldRenderInHUD() || isConfig) parent.render(drawContext, mouseX, mouseY);
        Arrays.stream(children).forEach(child -> { if (child.shouldRenderInHUD() || isConfig) child.render(drawContext, mouseX, mouseY); });
    }

    private void resizeElements() {
        if (!hasResized) {
            //resize square elements to match the text height and width
            TextElement primaryText = null;
            if (this.parent instanceof TextElement textElement) {
                primaryText = textElement;
            } else {
                for (ScreenElement child : this.children) {
                    if (child instanceof TextElement textElement) {
                        primaryText = textElement;
                        break;
                    }
                }
            }
            if (primaryText != null) {
                TextElement textElement = primaryText;
                List<ScreenElement> elements = new ArrayList<>(Arrays.stream(this.children).toList());
                elements.add(parent); //ensure parent gets resized as well
                elements.forEach(element -> {
                    if (element.equals(textElement)) return;

                    if (element.height == element.width) {
                        if (textElement.hasBackground()) {
                            element.setX(element.getX() - 5);
                            element.setY(element.getY() - 5);
                            element.setWidth(MinecraftClient.getInstance().textRenderer.fontHeight + 10);
                            element.setHeight(MinecraftClient.getInstance().textRenderer.fontHeight + 10);
                        } else {
                            element.setWidth(MinecraftClient.getInstance().textRenderer.fontHeight);
                            element.setHeight(MinecraftClient.getInstance().textRenderer.fontHeight);
                        }
                    }
                });
            }
            hasResized = true;
        }
    }

    public void saveAll() {
        parent.saveData();
        Arrays.stream(children).forEach(ScreenElement::saveData);
    }

    public void setAllPosition(double mouseX, double mouseY, int dragOffsetX, int dragOffsetY, int screenWidth, int screenHeight) {
        int largestChildOffsetX = 0;
        int largestChildOffsetY = 0;
        int largestChildWidth = 0;
        int largestChildHeight = 0;

        for (ScreenElement child : children) {
            int offsetX = child.getX() - parent.getX();
            int offsetY = child.getY() - parent.getY();

            largestChildOffsetX = Math.max(offsetX, largestChildOffsetX);
            largestChildOffsetY = Math.max(offsetY, largestChildOffsetY);
            largestChildWidth = Math.max(child.getWidth(), largestChildWidth);
            largestChildHeight = Math.max(child.getHeight(), largestChildHeight);
        }

        int xMax = screenWidth - Math.max(parent.getWidth(), largestChildOffsetX + largestChildWidth);
        int yMax = screenHeight - Math.max(parent.getHeight(), largestChildOffsetY + largestChildHeight);

        int x = parent.getX();
        int y = parent.getY();

        setElementPosition(parent, mouseX, mouseY, dragOffsetX, dragOffsetY, 0, 0, xMax, yMax);

        Arrays.stream(children).forEach(child -> setElementPosition(child, mouseX, mouseY, dragOffsetX, dragOffsetY,
                child.getX() - x, y - parent.getY(), xMax, yMax));
    }

    private void setElementPosition(ScreenElement element, double mouseX, double mouseY, int dragOffsetX, int dragOffsetY,
                                    int offsetX, int offsetY, int xLimit, int yLimit) {
        element.setX((int) mouseX + offsetX - dragOffsetX);
        element.setY((int) mouseY + offsetY - dragOffsetY);

        element.setX(Math.max(offsetX, Math.min(xLimit, element.getX())));
        element.setY(Math.max(offsetY, Math.min(yLimit, element.getY())));

        element.saveData();
    }

    public boolean isMouseOverAnyDraggable(double mouseX, double mouseY) {
        if (parent.canDrag() && parent.isMouseOver(mouseX, mouseY)) return true;
        return Arrays.stream(children).anyMatch(child -> child.canDrag() && child.isMouseOver(mouseX, mouseY));
    }

    public void clickAny(double mouseX, double mouseY, DragonHelperScreen screen) {
        if (parent instanceof ClickableElement clickableElement && parent.isMouseOver(mouseX, mouseY)) clickableElement.click(screen, this);
        Arrays.stream(children).forEach(child -> {
            if (child instanceof ClickableElement clickableElement && child.isMouseOver(mouseX, mouseY)) {
                clickableElement.click(screen, this);
            }
        });
    }

    public void resizeAfterMouseScroll(Screen screen, double mouseX, double mouseY, double verticalAmount) {
        if (parent instanceof ResizableElement resizableParent)
            resizableParent.afterMouseScroll(screen, mouseX, mouseY, verticalAmount);
        Arrays.stream(children).forEach(child -> {
            if (child instanceof ResizableElement resizableElement)
                resizableElement.afterMouseScroll(screen, mouseX, mouseY, verticalAmount);
        });
    }

    public void resetAll() {
        parent.reset();
        Arrays.stream(children).forEach(ScreenElement::reset);
        hasResized = false;
        resizeElements();
    }

    public ScreenElement getParent() {
        return parent;
    }

    public boolean shouldRenderInHUD() {
        return parent.shouldRenderInHUD() || Arrays.stream(children).anyMatch(ScreenElement::shouldRenderInHUD);
    }
}
