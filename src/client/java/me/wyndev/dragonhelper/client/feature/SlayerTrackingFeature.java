package me.wyndev.dragonhelper.client.feature;

import me.wyndev.dragonhelper.client.DragonHelperClient;
import me.wyndev.dragonhelper.client.Utils;
import me.wyndev.dragonhelper.client.config.ServerConfig;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;

public class SlayerTrackingFeature {

    private static State state = State.NONE;
    private static int currentTick = 0; //delayed loading

    public static void register() {
        ClientReceiveMessageEvents.ALLOW_GAME.register((text, isInActionBar) -> {
            String server = Utils.getClientServer();
            if (server == null) return true;
            String update = (String) ServerConfig.instance.getServerFeatureValue(server, Feature.SLAYER_EXP_UPDATE_CONTAINS_TEXT);
            String load = (String) ServerConfig.instance.getServerFeatureValue(server, Feature.SLAYER_EXP_LOAD_CONTAINS_TEXT);

            String s = text.getString().toLowerCase();

            if (state == State.WAIT) {
                //rng meter exp loading
                if (load == null || !s.contains(load)) return true;

                //save data
                try {
                    DragonHelperClient.getPlayerData().setZombieSlayerExp(Integer.parseInt(s.replaceAll("[^0-9.]", "")));
                    state = State.SUCCESS;
                } catch (NumberFormatException ignored) {
                }

                return false;
            } else if (state == State.SUCCESS) {
                //exp tracking from boss kills
                if (update == null || !s.contains("level") || !s.contains(update)) return true;

                try {
                    //update slayer meter exp
                    String[] sub = s.split(">");
                    int exp = Integer.parseInt(sub[1].replaceAll("[^0-9.]", ""));
                    if (s.contains("zombie")) {
                        DragonHelperClient.getPlayerData().setZombieSlayerExp(exp);
                    }
                    return false;
                } catch (NumberFormatException ignored) {}
            }

            return true;
        });

        ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> {state = State.NONE; currentTick = 0;});
        ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> state = State.NONE);

        ClientTickEvents.START_CLIENT_TICK.register(client -> {
            if (++currentTick < 400) return;
            if (client.getNetworkHandler() != null && state == State.NONE) {
                if (DragonHelperClient.getPlayerData().getZombieSlayerExp() == 0) {
                    state = State.WAIT;

                    String server = Utils.getClientServer(client);
                    if (server == null) {
                        state = State.SUCCESS;
                        return;
                    }
                    String command = (String) ServerConfig.instance.getServerFeatureValue(server, Feature.SLAYER_EXP_TRACKING_COMMAND);
                    if (command == null) {
                        state = State.SUCCESS;
                        return;
                    }

                    client.getNetworkHandler().sendCommand(command);
                } else {
                    state = State.SUCCESS;
                }
            }
        });
    }

    private enum State {
        NONE,
        WAIT,
        SUCCESS
    }

}
