package me.wyndev.dragonhelper.client.hud.element;

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

    public void drawAll(DrawContext drawContext) {
        if (!renderCheckCallback.call()) return;

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

        parent.draw(drawContext);
        Arrays.stream(children).forEach(child -> child.draw(drawContext));
    }

    public void saveAll() {
        parent.saveData();
        Arrays.stream(children).forEach(ScreenElement::saveData);
    }

    public void setAllPosition(double mouseX, double mouseY, int dragOffsetX, int dragOffsetY, int screenWidth, int screenHeight) {
        setElementPosition(parent, mouseX, mouseY, dragOffsetX, dragOffsetY, screenWidth, screenHeight);
        Arrays.stream(children).forEach(child -> {
            setElementPosition(child, child.getX(), child.getY(), dragOffsetX, dragOffsetY, screenWidth, screenHeight);
        });
    }

    private void setElementPosition(ScreenElement element, double mouseX, double mouseY, int dragOffsetX, int dragOffsetY, int screenWidth, int screenHeight) {
        element.setX((int) mouseX - dragOffsetX);
        element.setY((int) mouseY - dragOffsetY);

        element.setX(Math.max(0, Math.min(screenWidth - element.getWidth(), element.getX())));
        element.setY(Math.max(0, Math.min(screenHeight - element.getWidth(), element.getY())));

        element.saveData();
    }

    public boolean isMouseOverAny(double mouseX, double mouseY) {
        if (parent.isMouseOver(mouseX, mouseY)) return true;
        return Arrays.stream(children).anyMatch(child -> child.isMouseOver(mouseX, mouseY));
    }

    public void resizeAfterMouseScroll(Screen screen, double mouseX, double mouseY, double verticalAmount) {
        if (parent instanceof ResizableElement resizableParent)
            resizableParent.afterMouseScroll(screen, mouseX, mouseY, verticalAmount);
        Arrays.stream(children).forEach(child -> {
            if (child instanceof ResizableElement resizableElement)
                resizableElement.afterMouseScroll(screen, mouseX, mouseY, verticalAmount);
        });
    }

    public ScreenElement getParent() {
        return parent;
    }
}
