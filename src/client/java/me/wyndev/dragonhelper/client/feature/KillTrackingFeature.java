package me.wyndev.dragonhelper.client.feature;

import me.wyndev.dragonhelper.client.DragonHelperClient;
import me.wyndev.dragonhelper.client.Utils;
import me.wyndev.dragonhelper.client.config.ServerConfig;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;

public class KillTrackingFeature {

    private static boolean hasSentTrackingCommand = false;
    private static int ticks = 0;

    public static void register() {
        ClientReceiveMessageEvents.ALLOW_GAME.register((text, isInActionBar) -> {
            String server = Utils.getClientServer();
            if (!hasSentTrackingCommand || server == null) return true;

            String contains = (String) ServerConfig.instance.getServerFeatureValue(server, Feature.KILL_CONTAINS_TEXT);
            String protector = (String) ServerConfig.instance.getServerFeatureValue(server, Feature.PROTECTOR_NAME_CONTAINS_TEXT);

            //kill tracking
            String textString = text.getString().toLowerCase();

            if (contains == null || !textString.contains(contains)) return true;
            if (textString.contains("zealot")) {
                DragonHelperClient.getPlayerData().setZealotKills(DragonHelperClient.getPlayerData().getZealotKills() + 1);
            } else if (protector != null && textString.contains(protector)) {
                DragonHelperClient.getPlayerData().setEndstoneProtectorKills(DragonHelperClient.getPlayerData().getEndstoneProtectorKills() + 1);
            } else if (textString.contains("dragon")) {
                DragonHelperClient.getPlayerData().setDragonKills(DragonHelperClient.getPlayerData().getDragonKills() + 1);
            } else if (textString.contains("watcher")) {
                DragonHelperClient.getPlayerData().setWatcherKills(DragonHelperClient.getPlayerData().getWatcherKills() + 1);
            } else if (textString.contains("obsidian defender")) {
                DragonHelperClient.getPlayerData().setObsidianDefenderKills(DragonHelperClient.getPlayerData().getObsidianDefenderKills() + 1);
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
            if (++ticks < 100) return;
            if (client.getNetworkHandler() != null && !hasSentTrackingCommand) {
                hasSentTrackingCommand = true;
                String server = Utils.getClientServer(client);
                if (server == null) return;
                String command = (String) ServerConfig.instance.getServerFeatureValue(server, Feature.KILL_TRACKING_COMMAND);
                if (command == null) return;
                client.getNetworkHandler().sendCommand(command);
            }
        });
    }

}
