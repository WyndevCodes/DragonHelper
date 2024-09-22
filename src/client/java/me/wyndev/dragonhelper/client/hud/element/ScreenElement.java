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

    public ScreenElement(String identifier, String texture, int defaultX, int defaultY, int defaultWidth, int defaultHeight) {
        this.identifier = identifier;
        if (texture != null) { this.texture = Identifier.of(DragonHelperClient.NAMESPACE, "textures/gui/" + texture); }
        this.width = defaultWidth;
        this.height = defaultHeight;
        loadData(defaultX, defaultY); //IMPORTANT: must be after height and width initialization
    }

    public void draw(DrawContext drawContext) {
        if (texture == null) return;
        MinecraftClient.getInstance().getTextureManager().bindTexture(texture);
        drawContext.drawTexture(texture, x, y, 0, 0, width, height, width, height);
    }

    public boolean isMouseOver(double mouseX, double mouseY) {
        return mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height;
    }

    public void loadData(int defaultX, int defaultY) {
        this.x = ModData.getInstance().getInt(identifier + "-x", defaultX);
        this.y = ModData.getInstance().getInt(identifier + "-y", defaultY);
        this.width = ModData.getInstance().getInt(identifier + "-width", width);
        this.height = ModData.getInstance().getInt(identifier + "-height", height);
    }

    public void saveData() {
        ModData.getInstance().setInt(identifier + "-x", x);
        ModData.getInstance().setInt(identifier + "-y", y);
        ModData.getInstance().setInt(identifier + "-width", width);
        ModData.getInstance().setInt(identifier + "-height", height);
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
}
