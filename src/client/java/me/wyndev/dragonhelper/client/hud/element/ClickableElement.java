package me.wyndev.dragonhelper.client.hud.element;

import me.wyndev.dragonhelper.client.hud.DragonHelperScreen;
import net.minecraft.client.MinecraftClient;
import net.minecraft.sound.SoundEvents;

/**
 * A screen element that can be clicked.
 */
public interface ClickableElement {

    /**
     * Click this element, playing a sound and running the click functionality defined
     * in {@link ClickableElement#onClick(DragonHelperScreen, ScreenElementGroup)}.
     * @param screen The screen containing the group with this clickable element
     * @param parentGroup The screen element group containing this clickable element
     */
    default void click(DragonHelperScreen screen, ScreenElementGroup parentGroup) {
        if (MinecraftClient.getInstance().player != null) {
            MinecraftClient.getInstance().player.playSound(SoundEvents.UI_BUTTON_CLICK.value(), 1F, 1F);
        }
        onClick(screen, parentGroup);
    }

    /**
     * Code to run when this element is clicked in the mod menu
     * @param screen The screen containing the group with this clickable element
     * @param parentGroup The screen element group containing this clickable element
     */
    void onClick(DragonHelperScreen screen, ScreenElementGroup parentGroup);

    @FunctionalInterface
    interface ClickConsumer {
        void accept(DragonHelperScreen screen, ScreenElementGroup parentGroup);
    }

}
