package me.wyndev.dragonhelper.client;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.text.MutableText;
import org.jetbrains.annotations.Nullable;

import java.text.DecimalFormat;

/**
 * Utility class for this mod.
 */
public class Utils {

    /**
     * Sends a title to the client specified by {@link MinecraftClient#getInstance()}.
     * @param titleText The text that should be displayed in the title
     */
    public static void sendTitleToClient(MutableText titleText) {
        sendTitleToClient(titleText, null);
    }

    /**
     * Sends a title to the client specified by {@link MinecraftClient#getInstance()}.
     * @param titleText The text that should be displayed in the title
     * @param subtitleText The text that should be displayed in the subtitle below the
     *                     title, or null if no text should be displayed there
     */
    public static void sendTitleToClient(MutableText titleText, @Nullable MutableText subtitleText) {
        sendTitleToClient(titleText, subtitleText, 5, 40, 15);
    }

    /**
     * Sends a title to the client specified by {@link MinecraftClient#getInstance()}.
     * @param titleText The text that should be displayed in the title
     * @param subtitleText The text that should be displayed in the subtitle below the
     *                     title, or null if no text should be displayed there
     * @param fadeIn Time, in ticks, for the title to fade in
     * @param stay Time, in ticks, for the title to stay at full opacity
     * @param fadeOut Time, in ticks, for the title to fade out
     */
    public static void sendTitleToClient(MutableText titleText, @Nullable MutableText subtitleText, int fadeIn, int stay, int fadeOut) {
        MinecraftClient.getInstance().inGameHud.setTitle(titleText);
        if (subtitleText != null) MinecraftClient.getInstance().inGameHud.setSubtitle(titleText);
        MinecraftClient.getInstance().inGameHud.setTitleTicks(fadeIn, stay, fadeOut);
    }

    /**
     * Formats a number into a formatted string, like 10000 -> 10,000.
     * Uses commas for separation and a period for the decimal point.
     * @param number The number to format
     * @return The formatted number
     */
    public static String formatNumber(double number) {
        return formatNumber(number, ',', '.');
    }

    /**
     * Formats a number into a formatted string, like 10000 -> 10,000
     * @param number The number to format
     * @param spacer The spacer (default is a comma)
     * @param decimal The decimal point (default is a period)
     * @return The formatted number
     */
    public static String formatNumber(double number, char spacer, char decimal) {
        DecimalFormat decimalFormat = new DecimalFormat("#" + spacer + "###" + decimal + "##");
        return decimalFormat.format(number);
    }

    /**
     * Gets the current server that the minecraft client is connected to, if it is a valid server for this mod.
     * @return The lowercased server name of the server the {@link MinecraftClient#getInstance()} is connected to, or null if not valid
     */
    @Nullable
    public static String getClientServer() {
        return getClientServer(MinecraftClient.getInstance());
    }

    /**
     * Gets the current server that the minecraft client is connected to, if it is a valid server for this mod.
     * @return The lowercased server name of the server the {@link MinecraftClient} is connected to, or null if not valid
     */
    @Nullable
    public static String getClientServer(MinecraftClient minecraftClient) {
        return getClientServer(minecraftClient.getNetworkHandler());
    }

    /**
     * Gets the current server that the minecraft client's network handler is connected to, if it is a valid server for this mod.
     * @return The lowercased server name of the server the {@link ClientPlayNetworkHandler} is connected to, or null if not valid
     */
    @Nullable
    public static String getClientServer(@Nullable ClientPlayNetworkHandler networkHandler) {
        if (networkHandler == null) return null;
        ServerInfo serverInfo = networkHandler.getServerInfo();
        return (serverInfo != null ? serverInfo.name.toLowerCase() : null);
    }

}
