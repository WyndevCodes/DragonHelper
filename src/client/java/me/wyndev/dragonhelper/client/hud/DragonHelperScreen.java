package me.wyndev.dragonhelper.client.hud;

import me.wyndev.dragonhelper.client.DragonHelperClient;
import me.wyndev.dragonhelper.client.Utils;
import me.wyndev.dragonhelper.client.config.DragonHelperConfig;
import me.wyndev.dragonhelper.client.config.ServerConfig;
import me.wyndev.dragonhelper.client.data.SlayerLevel;
import me.wyndev.dragonhelper.client.feature.Feature;
import me.wyndev.dragonhelper.client.hud.element.ButtonElement;
import me.wyndev.dragonhelper.client.hud.element.ScreenElement;
import me.wyndev.dragonhelper.client.hud.element.ScreenElementGroup;
import me.wyndev.dragonhelper.client.hud.element.TextElement;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.fabric.api.client.screen.v1.ScreenMouseEvents;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

import java.util.Arrays;

/**
 * Custom screen configuration for this mod.
 */
public class DragonHelperScreen extends Screen {

    private static final ScreenElementGroup[] elements = new ScreenElementGroup[]{
            new ScreenElementGroup(
                    () -> (ServerConfig.instance.currentServerHasFeature(Feature.KILL_TRACKING_COMMAND) &&
                            DragonHelperConfig.get().getBoolean("ui.zealotKillCounter", true)),
                    new ScreenElement("zealot-counter", "zealot.png", 90, 10, 30, 30),
                    new TextElement("zealot-counter-text", () -> Utils.formatNumber(DragonHelperClient.getPlayerData().getZealotKills()) + " Kills",
                            () -> 0xFFFFFF, 110, 10, 50, 50, true)
            ),
            new ScreenElementGroup(
                    () -> (ServerConfig.instance.currentServerHasFeature(Feature.PROTECTOR_NAME_CONTAINS_TEXT) &&
                            DragonHelperConfig.get().getBoolean("ui.endstoneProtectorKillCounter", true)),
                    new ScreenElement("endstone-protector-counter", "endstone-golem.png", 180, 10, 10, 25),
                    new TextElement("endstone-protector-text", () -> Utils.formatNumber(DragonHelperClient.getPlayerData().getEndstoneProtectorKills()) + " Kills",
                            () -> 0xFFFFFF, 200, 10, 50, 50, true)
            ),
            new ScreenElementGroup(
                    () -> (ServerConfig.instance.currentServerHasFeature(Feature.KILL_TRACKING_COMMAND) &&
                            DragonHelperConfig.get().getBoolean("ui.dragonKillCounter", true)),
                    new ScreenElement("dragon-kill-counter", "ender-dragon.png", 360, 10, 30, 30),
                    new TextElement("dragon-kill-counter-text", () -> Utils.formatNumber(DragonHelperClient.getPlayerData().getDragonKills()) + " Kills",
                            () -> 0xFFFFFF, 380, 10, 50, 50, true)
            ),
            new ScreenElementGroup(
                    () -> (ServerConfig.instance.currentServerHasFeature(Feature.PROTECTOR_NAME_CONTAINS_TEXT) &&
                            DragonHelperConfig.get().getBoolean("ui.endstoneProtectorTimer", true)),
                    new ScreenElement("endstone-protector-timer", "endstone.png", 270, 10, 30, 30),
                    new TextElement("endstone-protector-text", () -> {
                        if (DragonHelperClient.getServerDataTracker().getLastSpawnedEndstoneProtectorTime() == 0) return "Loading...";
                        long timeUntilNext = 60_000 - (System.currentTimeMillis() - DragonHelperClient.getServerDataTracker().getLastSpawnedEndstoneProtectorTime());
                        if (timeUntilNext < 0) return "Spawning Soon!";
                        timeUntilNext = timeUntilNext / 1_000; //convert to seconds
                        return timeUntilNext / 60 + ":" + (timeUntilNext % 60 < 10 ? "0" : "") + (timeUntilNext % 60);
                    }, () -> 0xFFFFFF, 290, 10, 50, 50, true)
            ),
            new ScreenElementGroup(
                    () -> (ServerConfig.instance.currentServerHasFeature(Feature.RNG_METER_TRACKING_COMMAND) &&
                            DragonHelperConfig.get().getBoolean("ui.rngMeterTracker", true)),
                    new TextElement("rng-meter-exp-text", () -> Utils.formatNumber(DragonHelperClient.getPlayerData().getRngMeterExp()) + " RNG Meter Experience",
                            () -> 0xF803FC, 15, 200, 50, 50, true)
            ),
            new ScreenElementGroup(
                    () -> (ServerConfig.instance.currentServerHasFeature(Feature.SLAYER_EXP_TRACKING_COMMAND) &&
                            DragonHelperConfig.get().getBoolean("ui.zombieSlayerLevelTracker", true)),
                    new ScreenElement("zombie-slayer-tracker", "zombie-slayer.png", 450, 10, 15, 30),
                    new TextElement("zombie-slayer-tracker-text", () -> {
                        int exp = DragonHelperClient.getPlayerData().getZombieSlayerExp();
                        String currentExp = Utils.formatNumber(exp);
                        int key = SlayerLevel.slayerLevels.ceilingKey(exp);
                        String[] slayers = SlayerLevel.slayerLevels.getOrDefault(key, new String[]{"MAXED OUT!"});
                        for (String slayer : slayers) {
                            if (slayer.equals("MAXED OUT")) {
                                return currentExp;
                            } else if (slayer.toLowerCase().contains("zombie")) {
                                return currentExp + "/" + Utils.formatNumber(key) + " (" + slayer + " Progress)";
                            }
                        }
                        return "Unknown slayer!";
                    }, () -> 0x00FF40, 470, 10, 50, 50, true)
            ),
            new ScreenElementGroup(
                    () -> true, //always show
                    new ButtonElement("reset-button", (screen, group) -> Arrays.stream(DragonHelperScreen.getElements()).forEach(ScreenElementGroup::resetAll),
                            Text.translatable("text.config.dragonhelper.resetbutton"),
                            ScreenElement.ScreenBound.WIDTH.getValue(), ScreenElement.ScreenBound.HEIGHT.getValue(),
                            60, 20)
            )
    };

    private ScreenElementGroup dragging;
    private int dragOffsetX;
    private int dragOffsetY;

    public DragonHelperScreen() {
        super(Text.of("DragonHelper UI Configuration"));
    }

    public static void register() {
        KeyBinding keyBinding = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.dragonhelper.configScreen",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_U,
                "category.dragonhelper"
        ));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (keyBinding.wasPressed()) {
                if (client.currentScreen != null) client.currentScreen.close();
                client.execute(() -> {
                    DragonHelperScreen screen = new DragonHelperScreen();
                    client.setScreen(screen);
                    ScreenMouseEvents.afterMouseScroll(screen).register(screen::afterMouseScroll);
                });
            }
        });

        //mouse pos are -1 because this is not being rendered in a GUI and is just a HUD overlay
        HudRenderCallback.EVENT.register((drawContext, tickCounter) -> Arrays.stream(elements).forEach(element -> {
            if (element.shouldRenderInHUD()) element.drawAll(drawContext, -1, -1, false);
        }));
    }

    public static ScreenElementGroup[] getElements() {
        return elements;
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);

        Arrays.stream(elements).forEach(element -> element.drawAll(context, mouseX, mouseY, true));
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0) {
            boolean shouldCheckDrag = true;
            for (ScreenElementGroup element : elements) {
                if (element.isMouseOverAnyDraggable(mouseX, mouseY) && shouldCheckDrag) {
                    dragging = element;
                    dragOffsetX = (int) mouseX - element.getParent().getX();
                    dragOffsetY = (int) mouseY - element.getParent().getY();
                    shouldCheckDrag = false;
                }
                element.clickAny(mouseX, mouseY, this);
            }
            return true;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (button == 0 && dragging != null) {
            dragging.saveAll();
            dragging = null;
            return true;
        }
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        if (dragging != null) {
            dragging.setAllPosition(mouseX, mouseY, dragOffsetX, dragOffsetY, width, height);
            return true;
        }
        return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
    }

    private void afterMouseScroll(Screen screen, double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        Arrays.stream(elements).forEach(element -> element.resizeAfterMouseScroll(screen, mouseX, mouseY, verticalAmount));
    }

}
