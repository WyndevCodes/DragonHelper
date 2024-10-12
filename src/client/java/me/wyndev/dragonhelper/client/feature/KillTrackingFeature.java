package me.wyndev.dragonhelper.client.feature;

import it.unimi.dsi.fastutil.Pair;
import me.wyndev.dragonhelper.client.DragonHelperClient;
import me.wyndev.dragonhelper.client.Utils;
import me.wyndev.dragonhelper.client.config.ServerConfig;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class KillTrackingFeature {

    private static boolean hasSentTrackingCommand = false;
    private static int ticks = 0;

    private static Map<String, Pair<Supplier<Integer>, Consumer<Integer>>> killFunctions;

    public static void register() {
        //setup kill functions map
        killFunctions = new HashMap<>(7);
        killFunctions.put("zealot", Pair.of(
                () -> DragonHelperClient.getPlayerData().getZealotKills(),
                i -> DragonHelperClient.getPlayerData().setZealotKills(i)));
        killFunctions.put("dragon", Pair.of(
                () -> DragonHelperClient.getPlayerData().getDragonKills(),
                i -> DragonHelperClient.getPlayerData().setDragonKills(i)));
        killFunctions.put("watcher", Pair.of(
                () -> DragonHelperClient.getPlayerData().getWatcherKills(),
                i -> DragonHelperClient.getPlayerData().setWatcherKills(i)));
        killFunctions.put("obsidian defender", Pair.of(
                () -> DragonHelperClient.getPlayerData().getObsidianDefenderKills(),
                i -> DragonHelperClient.getPlayerData().setObsidianDefenderKills(i)));
        killFunctions.put("crypt ghoul", Pair.of(
                () -> DragonHelperClient.getPlayerData().getCryptGhoulKills(),
                i -> DragonHelperClient.getPlayerData().setCryptGhoulKills(i)));
        killFunctions.put("atoned", Pair.of(
                () -> DragonHelperClient.getPlayerData().getZombieSlayerBossKills(),
                i -> DragonHelperClient.getPlayerData().setZombieSlayerBossKills(i)));

        //message parsing
        ClientReceiveMessageEvents.ALLOW_GAME.register((text, isInActionBar) -> {
            String server = Utils.getClientServer();
            if (!hasSentTrackingCommand || server == null) return true;

            String contains = (String) ServerConfig.instance.getServerFeatureValue(server, Feature.KILL_CONTAINS_TEXT);
            String protector = (String) ServerConfig.instance.getServerFeatureValue(server, Feature.PROTECTOR_NAME_CONTAINS_TEXT);
            String bestiaryText = (String) ServerConfig.instance.getServerFeatureValue(server, Feature.BESTIARY_LOADING_TEXT);

            //protector kills
            if (protector != null && !killFunctions.containsKey(protector))
                killFunctions.put(protector, Pair.of(
                        () -> DragonHelperClient.getPlayerData().getEndstoneProtectorKills(),
                        i -> DragonHelperClient.getPlayerData().setEndstoneProtectorKills(i)));

            //kill tracking
            String textString = text.getString().toLowerCase();

            //load from bestiary, if possible
            if (bestiaryText != null && textString.contains(bestiaryText)) {
                //this was a bestiary load message
                int kills = 0;
                try {
                    kills = Integer.parseInt(textString.replaceAll("[^0-9.]", ""));
                } catch (NumberFormatException ignored) {}

                if (kills > 0) {
                    int finalKills = kills;
                    killFunctions.keySet().forEach(key -> {
                        if (textString.contains(key)) killFunctions.get(key).second().accept(finalKills);
                    });
                }
                return false;
            }

            if (contains == null || !textString.contains(contains)) return true;
            //increment kill stat for the first kill function mob name that matches the killed mob
            for (String key : killFunctions.keySet()) {
                if (textString.contains(key)) {
                    killFunctions.get(key).second().accept(killFunctions.get(key).first().get() + 1);
                    break;
                }
            }
            return false;
        });

        ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> hasSentTrackingCommand = false);
        ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> hasSentTrackingCommand = false);

        ClientTickEvents.START_CLIENT_TICK.register(client -> {
            if (++ticks < 100) return;
            if (!hasSentTrackingCommand && client.getNetworkHandler() != null) {
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
