package me.wyndev.dragonhelper.client.feature;

import me.wyndev.dragonhelper.client.DragonHelperClient;
import me.wyndev.dragonhelper.client.Utils;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;

public class SlayerTrackingFeature {

    private static final String SLAYER_EXP_TRACKING_COMMAND = "debugslayerxp";
    private static State state = State.NONE;
    private static int currentTick = 0; //delayed loading

    public static void register() {
        ClientReceiveMessageEvents.ALLOW_GAME.register((text, isInActionBar) -> {
            if (!Utils.isOnDragnet()) return true;

            String s = text.getString().toLowerCase();

            if (state == State.WAIT) {
                //rng meter exp loading
                if (!s.contains("slayer xp experience")) return true;

                //save data
                try {
                    DragonHelperClient.getPlayerData().setZombieSlayerExp(Integer.parseInt(s.replaceAll("[^0-9.]", "")));
                    state = State.SUCCESS;
                } catch (NumberFormatException ignored) {
                }

                return false;
            } else if (state == State.SUCCESS) {
                //exp tracking from boss kills
                if (!s.contains("level") || !s.contains("> total xp")) return true;

                try {
                    //update slayer meter exp
                    String[] sub = s.split(">");
                    int exp = Integer.parseInt(sub[1].replaceAll("[^0-9.]", ""));
                    if (s.contains("zombie")) {
                        DragonHelperClient.getPlayerData().setZombieSlayerExp(exp);
                    }
                } catch (NumberFormatException ignored) {}
            }

            return true;
        });

        ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> state = State.NONE);
        ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> state = State.NONE);

        ClientTickEvents.START_CLIENT_TICK.register(client -> {
            if (++currentTick < 400) return;
            if (client.getNetworkHandler() != null && state == State.NONE) {
                if (DragonHelperClient.getPlayerData().getZombieSlayerExp() == 0) {
                    state = State.WAIT;
                    if (!Utils.isOnDragnet(client)) return;
                    client.getNetworkHandler().sendCommand(SLAYER_EXP_TRACKING_COMMAND);
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
