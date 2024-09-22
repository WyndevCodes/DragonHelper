package me.wyndev.dragonhelper.client.feature;

import me.wyndev.dragonhelper.client.DragonHelperClient;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;

public class KillTrackingFeature {

    private static final String KILL_TRACKING_COMMAND = "debugkill";
    private static boolean hasSentTrackingCommand = false;
    private static int ticks = 0;

    public static void register() {
        ClientReceiveMessageEvents.ALLOW_GAME.register((text, isInActionBar) -> {
            //kill tracking
            String textString = text.getString().toLowerCase();
            if (!textString.startsWith("you killed")) return true;
            //dragon kill logic is done somewhere else
            if (textString.contains("zealot")) {
                DragonHelperClient.getPlayerData().setZealotKills(DragonHelperClient.getPlayerData().getZealotKills() + 1);
            } else if (textString.contains("protector")) {
                DragonHelperClient.getPlayerData().setEndstoneProtectorKills(DragonHelperClient.getPlayerData().getEndstoneProtectorKills() + 1);
            } else if (textString.contains("crypt ghoul")) {
                DragonHelperClient.getPlayerData().setCryptGhoulKills(DragonHelperClient.getPlayerData().getCryptGhoulKills() + 1);
            } else if (textString.contains("atoned")) {
                DragonHelperClient.getPlayerData().setZombieSlayerBossKills(DragonHelperClient.getPlayerData().getZombieSlayerBossKills() + 1);
            }
            return false;
        });

        ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> hasSentTrackingCommand = false);
        ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> hasSentTrackingCommand = false);

        ClientTickEvents.START_CLIENT_TICK.register(client -> {
            if (++ticks < 2) return;
            if (client.getNetworkHandler() != null && !hasSentTrackingCommand) {
                hasSentTrackingCommand = true;
                client.getNetworkHandler().sendCommand(KILL_TRACKING_COMMAND);
            }
        });
    }

}
