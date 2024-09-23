package me.wyndev.dragonhelper.client.hud.element;

import me.wyndev.dragonhelper.client.DragonHelperClient;
import me.wyndev.dragonhelper.client.data.ModData;
import me.wyndev.dragonhelper.client.hud.DragonHelperScreen;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.Identifier;

/**
 * An element that should be drawn on the {@link DragonHelperScreen}.
 */
public class ScreenElement {

    protected String identifier;
    protected Identifier texture;
    protected int x;
    protected int y;
    protected int width;
    protected int height;

    protected int defaultX;
    protected int defaultY;
    protected int defaultWidth;
    protected int defaultHeight;

    public ScreenElement(String identifier, String texture, int defaultX, int defaultY, int defaultWidth, int defaultHeight) {
        this.identifier = identifier;
        if (texture != null) { this.texture = Identifier.of(DragonHelperClient.NAMESPACE, "textures/gui/" + texture); }
        this.defaultX = defaultX;
        this.defaultY = defaultY;
        this.defaultWidth = defaultWidth;
        this.defaultHeight = defaultHeight;
        this.width = defaultWidth;
        this.height = defaultHeight;
        loadData(); //IMPORTANT: must be after height and width initialization
    }

    public void render(DrawContext drawContext, double mouseX, double mouseY) {
        if (x == ScreenBound.WIDTH.getValue()) x = drawContext.getScaledWindowWidth() - width;
        if (y == ScreenBound.HEIGHT.getValue()) y = drawContext.getScaledWindowHeight() - height;

        draw(drawContext, mouseX, mouseY);
    }

    protected void draw(DrawContext drawContext, double mouseX, double mouseY) {
        if (texture == null) return;
        MinecraftClient.getInstance().getTextureManager().bindTexture(texture);
        drawContext.drawTexture(texture, x, y, 0, 0, width, height, width, height);
    }

    public boolean isMouseOver(double mouseX, double mouseY) {
        return mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height;
    }

    public void loadData() {
        this.x = ModData.getInstance().getInt(identifier + "-x", defaultX);
        this.y = ModData.getInstance().getInt(identifier + "-y", defaultY);
        this.width = ModData.getInstance().getInt(identifier + "-width", defaultWidth);
        this.height = ModData.getInstance().getInt(identifier + "-height", defaultHeight);
    }

    public void saveData() {
        ModData.getInstance().setInt(identifier + "-x", x);
        ModData.getInstance().setInt(identifier + "-y", y);
        ModData.getInstance().setInt(identifier + "-width", width);
        ModData.getInstance().setInt(identifier + "-height", height);
    }

    public void reset() {
        this.x = defaultX;
        this.y = defaultY;
        this.width = defaultWidth;
        this.height = defaultHeight;
        saveData();
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    @FunctionalInterface
    public interface ElementCallback<T> {
        T call();
    }

    public enum ScreenBound {
        WIDTH(-10),
        HEIGHT(-20);

        private final int value;

        ScreenBound(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }

    /**
     * @return True if this element can be dragged around and repositioned in
     * the mod screen, false otherwise
     */
    public boolean canDrag() {
        return true;
    }

    public boolean shouldRenderInHUD() {
        return true;
    }
}
